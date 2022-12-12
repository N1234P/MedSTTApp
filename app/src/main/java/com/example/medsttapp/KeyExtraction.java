package com.example.medsttapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Iterator;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

public class KeyExtraction {

    private final List<String> restrictions;
    private Set<String> keyPhrases;
    private final String speech;

    /**
     * Constructor, initialize common words under restrictions
     */
    public KeyExtraction(String speech) {
        this.speech = removeStops(speech.toLowerCase());
        keyPhrases = new HashSet<>();
        List<String> dups = new ArrayList<>();
        restrictions = new ArrayList<>();
        restrictions.add("it");
        restrictions.add("do");
        restrictions.add("lots");
        restrictions.add("lot");
        restrictions.add("i");
        restrictions.add("but");
        restrictions.add("and");
        restrictions.add("then");
        restrictions.add("than");
        restrictions.add("they");
        restrictions.add("will");
        restrictions.add("have");
        restrictions.add("had");
        restrictions.add("when");
        restrictions.add("who");
        restrictions.add("has");
        restrictions.add("while");
        restrictions.add("for");
        restrictions.add("be");
        restrictions.add("been");
        restrictions.add("well");
        restrictions.add("is");
        restrictions.add("which");
        restrictions.add("a");
        restrictions.add("as");
        restrictions.add("so");
        restrictions.add("such");
        restrictions.add("the");
        restrictions.add("of");
        restrictions.add("why");

        // add negations
        restrictions.add("not");
        restrictions.add("cant");
        restrictions.add("shouldnt");
        restrictions.add("wouldnt");
        restrictions.add("isnt");
        restrictions.add("wont");
        restrictions.add("dont");
        restrictions.add("didnt");
        restrictions.add("wasnt");
        restrictions.add("doesnt");
        restrictions.add("cannot");

        restrictions.add("can");
        restrictions.add("could");
        restrictions.add("give");
        restrictions.add("gave");
        // restrictions.add("gave");
        restrictions.add("now");
        restrictions.add("later");
        restrictions.add("get");
        restrictions.add("say");
        restrictions.add("take");
        restrictions.add("thus");
        restrictions.add("everywhere");
        restrictions.add("very");
        restrictions.add("also");
        restrictions.add("going");
        restrictions.add("lately");
        restrictions.add("late");
        restrictions.add("back");
        restrictions.add("want");
        restrictions.add("having");
        restrictions.add("anything");
        restrictions.add("nobody");
        restrictions.add("everything");
        restrictions.add("everybody");
        restrictions.add("everyone");
        restrictions.add("anyone");
        restrictions.add("nothing");
        restrictions.add("nobody");
        restrictions.add("try");
        restrictions.add("tried");
        restrictions.add("like");
    }


    /**
     * method aims to tags crucial parts of the speech, process, and extract keywords
     * Calls a collection of NLP libraries for parts of speech tagging
     */
    public void keyExtraction(InputStream is) {

        try {
            System.out.println("BEGINNING PARSEMODEL CREATION - This May" +
                    " Take A While!");

            POSModel model = new POSModel(is);
            POSTaggerME tagger = new POSTaggerME(model);


            WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
            String[] tokens = tokenizer.tokenize(speech);
            String[] tags = tagger.tag(tokens);

            POSSample sample = new POSSample(tokens, tags);
            // phrases now contains an arraylist of every word with an annotation after it
            // annotation is "_partofspeech", e.g. "_NN" after every noun
            List<String> phrases = Arrays.asList(sample.toString().split(" "));

            // System.out.println(negatedIndexes);
            System.out.println(phrases);

            keyPhrases = removeRestricts(getNLPKeyPhrases(phrases));
            removeNegation(phrases);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get all key phrases
     * @param phrases   The entered words with annotations
     */
    private Set<String> getNLPKeyPhrases(List<String> phrases) {
        Set<String> updatedList = new HashSet<String>();
        System.out.println(phrases);
        int index = 0;
        // pOS contains important NLP keywords that we want to include
        String[] pOS = {"NNPS", "NNP", "NNS", "NN", "VB", "VBG", "VBN", "VBD", "JJ"};

        while(index < phrases.size()) {
            String phrase = phrases.get(index);
            for(String p : pOS) {
                // multiword extraction, JJ is a type of adjective, include the word after it.
                if(phrase.endsWith(p) && p.equals("JJ") && index+1<phrases.size()) {
                    String nextPhrase = phrases.get(index+1);
                    updatedList.add(phrase.substring(0, phrase.indexOf("_")) + " " +
                            nextPhrase.substring(0, nextPhrase.indexOf("_")));
                    index++;
                    break;
                }
                if(phrase.endsWith(p)) {
                    updatedList.add(phrase.substring(0, phrase.indexOf("_")));
                    break;
                }

            }
            index++;
        }
        return updatedList;
    }


    /**
     * Ensures that common words do not go through
     * @param Phrases, this will consist of all the words filtered by getNLPPhrases
     */
    private Set<String> removeRestricts(Set<String> Phrases) {
        boolean flag;
        Set<String> updatedKeyPhrases = new HashSet<>();

        for(String phrase : Phrases) {
            flag = false;
            for(String restricted : restrictions) {
                if(phrase.equals(restricted) || phrase.length() > 50) {
                    flag = true;
                    break;
                }
            }
            if(!flag) {
                updatedKeyPhrases.add(phrase);
            }
        }
        return updatedKeyPhrases;
    }


    /**
     * Remove symbols, punctuation, etc. from phrase
     */
    private String removeStops(String phrase) {
        phrase = phrase.replace(",", "");
        phrase = phrase.replace(".", "");
        phrase = phrase.replace("!", "");
        phrase = phrase.replace("?", "");
        phrase = phrase.replace("'", "");
        return phrase;
    }

    public Set<String> getKeyPhrases() {
        return keyPhrases;
    }

    // This could be called after removing restricts. 1. store the index
    // of any "not" in keyExtraction (line 121) iterate through
    // keyPhrases and if any keyphrases occur the index right after the "not",
    // remove it from keyPhrases.

    /**
     * Removes negated keywords
     * @param phrases           All words filtered by getNLPPhrases
     */
    public void removeNegation(List<String> phrases) {
        // the phrases without annotations (every word in the speech)
        List<String> phrase_without = new ArrayList<String>();

        // make a copy of phrases to remove annotations
        for(String s : phrases) {
            s = s.substring(0, s.lastIndexOf("_"));
            phrase_without.add(s);
        }

        // remove 'a' and 'the'
        Iterator<String> athe = phrase_without.iterator();
        while(athe.hasNext()) {
            String a = athe.next();
            if(a.equals("a") || a.equals("the")) {
                athe.remove();
            }
        }
        //System.out.println("after removing a, the: " + phrase_without);

        // find index of "not" or negating keywords right on this line?
        List<Integer> negatedIndexes = new ArrayList<Integer>();
        for(int i = 0; i < phrase_without.size(); i++) {
            if(phrase_without.get(i).contains("not") ||
                    phrase_without.get(i).contains("cant") ||
                    phrase_without.get(i).contains("shouldnt") ||
                    phrase_without.get(i).contains("wouldnt") ||
                    phrase_without.get(i).contains("isnt") ||
                    phrase_without.get(i).contains("wont") ||
                    phrase_without.get(i).contains("dont") ||
                    phrase_without.get(i).contains("didnt") ||
                    phrase_without.get(i).contains("wasnt") ||
                    phrase_without.get(i).contains("doesnt") ||
                    phrase_without.get(i).contains("cannot")) {
                negatedIndexes.add(i);
            }
        }

        System.out.println(phrase_without);

        // iterate through keyphrases to remove negated
        Iterator<String> iterator = keyPhrases.iterator();
        while(iterator.hasNext()) {
            String s = iterator.next();
            // if it's a multi-word, separate it by space
            // remove the multi-word
            if(s.contains(" ")) {
                s = s.substring(0, s.lastIndexOf(" "));
            }
            if(phrase_without.contains(s)) {
                // get index of negated word and remove from keyPhrases
                // if the negation comes right before the keyPhrase
                int index = phrase_without.indexOf(s);
                //System.out.println("A " + phrase_without.get(index));
                if(negatedIndexes.contains(index - 1)) {
                    iterator.remove();
                }
                // the keyPhrase if it's can't, etc.
                else if(negatedIndexes.contains(index - 2)) {
                    iterator.remove();
                }
                System.out.println(s);
                // testing to handle something like "I don't have a cold"
//                else if(negatedIndexes.contains(index - 3) && phrase_without.get(index - 1) == "a") {
//                    System.out.println("Reached a");
//                    iterator.remove();
//                }
            }

        }

        System.out.println("after negated " + keyPhrases);
    }
}

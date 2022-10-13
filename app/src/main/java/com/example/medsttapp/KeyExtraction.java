package com.example.medsttapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

    private List<String> restrictions;
    private Set<String> keyPhrases;
    private List<String> dups;
    private String speech;


    public KeyExtraction(String speech) {
        this.speech = removeStops(speech.toLowerCase());
        keyPhrases = new HashSet<>();
        dups = new ArrayList<>();
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
        restrictions.add("not"); // need to add negation method for this
        restrictions.add("can");
        restrictions.add("could");
        restrictions.add("give");
        restrictions.add("gave");
        restrictions.add("gave");
        restrictions.add("now");
        restrictions.add("later");
        restrictions.add("get");
        restrictions.add("say");
        restrictions.add("take");
    }

    public void speechTagger(InputStream is) {

        try {
            System.out.println("BEGINNING PARSEMODEL CREATION");

            POSModel model = new POSModel(is);
            POSTaggerME tagger = new POSTaggerME(model);

            System.out.println("DONE???");

            WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
            String[] tokens = tokenizer.tokenize(speech);
            String[] tags = tagger.tag(tokens);

            POSSample sample = new POSSample(tokens, tags);
            List<String> phrases = Arrays.asList(sample.toString().split(" "));
            keyPhrases = removeRestricts(getNLPKeyPhrases(phrases));



        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Set<String> getNLPKeyPhrases(List<String> phrases) {
        Set<String> updatedList = new HashSet<String>();
        for(String phrase : phrases) {
            if(phrase.endsWith("NN") || phrase.endsWith("VB")) {
                updatedList.add(phrase.substring(0, phrase.indexOf("_")));
            }
        }
        return updatedList;

    }

    private Set<String> removeRestricts(Set<String> Phrases) {
        boolean flag;
        Set<String> updatedKeyPhrases = new HashSet<>();

        for(String phrase : Phrases) {
            flag = false;
            for(String restricted : restrictions) {
                if(phrase.equals(restricted)) {
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



    private String removeStops(String phrase) {
        phrase = phrase.replace(",", "");
        phrase = phrase.replace(".", "");
        phrase = phrase.replace("!", "");
        phrase = phrase.replace("?", "");
        return phrase;
    }

    public Set<String> getKeyPhrases() {
        return keyPhrases;
    }
}

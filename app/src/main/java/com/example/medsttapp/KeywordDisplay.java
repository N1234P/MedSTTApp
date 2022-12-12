    package com.example.medsttapp;

    import androidx.annotation.RequiresApi;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.cardview.widget.CardView;

    import android.annotation.SuppressLint;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.graphics.Typeface;
    import android.os.Build;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.ImageView;
    import android.widget.TextView;

    import com.fasterxml.jackson.databind.JsonSerializer;

    import java.io.IOException;
    import java.io.InputStream;
    import java.security.Key;
    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;
    import java.util.concurrent.CountDownLatch;
    import java.util.concurrent.ExecutorService;
    import java.util.concurrent.Executors;
    import java.util.stream.Collectors;


    public class KeywordDisplay extends AppCompatActivity {

        private ExecutorService executor = Executors.newSingleThreadExecutor();
        private String speech;

        private TextView header;

        private List<TextView> cards;

        private TextView card;
        private TextView card2;
        private TextView card3;
        private TextView card4;
        private TextView card5;
        private TextView card6;
        private TextView card7;
        private TextView card8;

        private TextView association;

        private ImageView backArrow;




        /**
         * This is the activity that is shown after speech confirmation or text
         * confirmation
         *  UI representation - activity_keyword_display.xml
         * @param savedInstanceState
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);





            setContentView(R.layout.activity_keyword_display);


            initializeCards();



            backArrow = findViewById(R.id.keyBack); // arrow to go back to main page (bottom left corner)

            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(KeywordDisplay.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            header = (TextView) findViewById(R.id.keyheader);
            header.setText("LOADING...");

            Intent intent = getIntent();
            this.speech = intent.getStringExtra("input");

            runBackgroundNLP(speech);
            System.out.println("SPEECH IS HERE!!!" + speech);


        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void initializeCards() {
            cards = new ArrayList<>();
            card = findViewById(R.id.card);
            card2 = findViewById(R.id.card2);
            card3 = findViewById(R.id.card3);
            card4 = findViewById(R.id.card4);
            card5 = findViewById(R.id.card5);
            card6 = findViewById(R.id.card6);
            card7 = findViewById(R.id.card7);
            card8 = findViewById(R.id.card8);
            association = findViewById(R.id.association);

            cards.add(card);
            cards.add(card2);
            cards.add(card3);
            cards.add(card4);
            cards.add(card5);
            cards.add(card6);
            cards.add(card7);
            cards.add(card8);

            cards.forEach(e -> e.setVisibility(View.GONE));
            association.setVisibility(View.GONE);


        }

        /**
         * This runs key extraction in a background thread, so that the heavy
         * computation work is not freezing up the UI
         * @param speech
         */
        public void runBackgroundNLP(String speech) {

            executor.execute(new Runnable() {
                Set<String> keywords;
                @Override
                public void run() {
                     InputStream is = null;

                     try {
                         System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
                         is = getAssets().open("en-pos-maxent.bin");
                         if(speech.length() > 0) {
                             KeyExtraction keyObj = new KeyExtraction(speech);
                             keyObj.keyExtraction(is);
                             keywords = keyObj.getKeyPhrases();
                             System.out.println("KEYWORDS " + keywords);
                         }
                         else {
                             keywords = new HashSet<>();
                         }


                     } catch (IOException e) {
                         e.printStackTrace();
                     }

                     // the moment keywords is retrieved, we can now modify UI thread and show keywords on screen

                     runOnUiThread(new Runnable() {
                         @RequiresApi(api = Build.VERSION_CODES.N)
                         @Override
                         public void run() {


                             List<String> keywordsList = new ArrayList<>(keywords);

                             if (keywordsList.size() == 0) {
                                 header.setText("No Keyword Found!");
                             } else {
                                 header.setText("Keyword(s) Found");
                                 if (keywordsList.size() >= 2) {
                                     association.setVisibility(View.VISIBLE);
                                     association.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Intent intent = new Intent(KeywordDisplay.this, Association.class);
                                             intent.putExtra("keywords", String.join(", ", keywordsList));
                                             startActivity(intent);
                                         }
                                     });
                                 }
                                 int i = 0; // i
                                 while (i < 8 && i < keywordsList.size()) {
                                     int j = i;
                                     cards.get(i).setText(keywordsList.get(i));
                                     cards.get(i).setTypeface(null, Typeface.ITALIC);
                                     cards.get(i).setVisibility(View.VISIBLE);


                                     cards.get(i).setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Intent intent = new Intent(KeywordDisplay.this, MedicalNote.class);
                                             intent.putExtra("keyword", keywordsList.get(j));
                                             startActivity(intent);
                                         }
                                     });

                                     SharedPreferences getCounter = getSharedPreferences("counter", 0);
                                     int count = getCounter.getInt(keywordsList.get(i), 0);

                                     SharedPreferences.Editor edit = getCounter.edit();
                                     edit.putInt(keywordsList.get(i), count + 1);
                                     edit.apply();
                                     i++;


                                 }


                             }
                         }
                        });
                    }

            });


        }
    }
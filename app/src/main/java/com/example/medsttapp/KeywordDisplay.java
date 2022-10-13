package com.example.medsttapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KeywordDisplay extends AppCompatActivity {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private String speech;

    private TextView header;
    private TextView loadingText;

    private CountDownLatch doneSignal = new CountDownLatch(1);

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        setContentView(R.layout.activity_keyword_display);

        loadingText = (TextView) findViewById(R.id.loading);
        header = (TextView) findViewById(R.id.keyheader);
        header.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        this.speech = intent.getStringExtra("input");

        runBackgroundNLP(speech);
        System.out.println("SPEECH IS HERE!!!" + speech);


    }

    public void runBackgroundNLP(String speech) {

        executor.execute(new Runnable() {
            Set<String> keywords;
            @Override
            public void run() {
                 InputStream is = null;

                 try {
                     System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver");
                     is = getAssets().open("en-pos-maxent.bin");
                     KeyExtraction keyObj = new KeyExtraction(speech);
                     keyObj.speechTagger(is);
                     keywords = keyObj.getKeyPhrases();
                     System.out.println("KEYWORDS " + keywords);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }


                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         header.setVisibility(View.VISIBLE);
                         loadingText.setText(String.join(",", keywords));
                     }
                 });
            }

        });


    }
}
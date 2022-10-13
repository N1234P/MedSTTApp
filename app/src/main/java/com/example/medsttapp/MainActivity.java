package com.example.medsttapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.speech.RecognizerIntent;
import android.view.View;




import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import org.jetbrains.annotations.Nullable;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;


public class MainActivity extends AppCompatActivity {

    private TextView prompt;
    private TextView text;
    private ImageView confirm;
    private ImageView reject;
    private ImageView sendMessage;
    private TextView messageInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        text = findViewById(R.id.userspeech);
        prompt = findViewById(R.id.prompt);
        confirm = findViewById(R.id.confirmation_button);
        reject = findViewById(R.id.reject_button);
        sendMessage = findViewById(R.id.send_message);
        messageInput = findViewById(R.id.message_input);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = messageInput.getText().toString();
                beginActivity(text);

            }
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();

    }


    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000000);


        startActivityForResult(intent, 100);






    }



    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK) {
            assert data != null;
            String input = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            text.setText('"' + input + '"');
            text.setTypeface(null, Typeface.ITALIC);
            prompt.setText("Is this what you wanted to say?");
            confirm.setImageDrawable(getResources().getDrawable(R.drawable.stt_speech_confirmation));
            reject.setImageDrawable(getResources().getDrawable(R.drawable.stt_speech_rejection));
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clear();
                    beginActivity(input);
                }
            });
            reject.setOnClickListener(this::speak);

        }
    }

    public void resetListeners() {
        confirm.setOnClickListener(v -> {
            // do nothing
        });

        reject.setOnClickListener(v -> {
            // do nothing
        });
    }

    public void clear() {
        text.setText("");
        prompt.setText("");
        confirm.setImageResource(0);
        reject.setImageResource(0);
        resetListeners();
        Toast.makeText(this, "analyzing your results...", Toast.LENGTH_SHORT).show();
    }

    public void beginActivity(String input) {
        Intent intent = new Intent(MainActivity.this, KeywordDisplay.class);
        intent.putExtra("input", input);
        startActivity(intent);
    }

}
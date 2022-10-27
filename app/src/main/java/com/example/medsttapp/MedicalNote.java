package com.example.medsttapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MedicalNote extends AppCompatActivity {

    TextView keywordView;
    ImageView backArrow;
    TextView keywordCounter;

    TextView note;

    String keyword;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_note);

        Intent intent = getIntent();
        this.keyword = intent.getStringExtra("keyword");

        keywordView = findViewById(R.id.keyword);
        keywordView.setText('"' + keyword + '"');
        keywordView.setTypeface(null, Typeface.ITALIC);


        backArrow = findViewById(R.id.keyBack); // arrow to go back to main page (bottom left corner)

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences getCounter = getSharedPreferences("counter", 0);
        int count = getCounter.getInt(keyword, 0);

        keywordCounter = findViewById(R.id.counter);
        keywordCounter.setText("Keyword found " + count + " time(s)");
        keywordCounter.setTypeface(null, Typeface.BOLD);

        note = findViewById(R.id.note);
        loadNote();
    }


    public void save(View v) {
        String keywordValue = note.getText().toString();
        SharedPreferences keywordPair = getSharedPreferences("pair", 0);
        SharedPreferences.Editor edit = keywordPair.edit();
        edit.putString(keyword, keywordValue);
        edit.apply();
        Toast.makeText(this, "Saving Definition", Toast.LENGTH_SHORT).show();
    }

    public void loadNote() {
        SharedPreferences keywordPair = getSharedPreferences("pair", 0);
        String keywordValue = keywordPair.getString(keyword, "");
        note.setText(keywordValue);
    }
}
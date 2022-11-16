package com.example.medsttapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class Association extends AppCompatActivity {

    private EditText associationInput;

    private ImageView associationConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_association);

        Intent intent = getIntent();
        String keywords = intent.getStringExtra("keywords");
        setDescription(keywords);


        char[] chars = keywords.toCharArray();
        Arrays.sort(chars);
        String associationKey = new String(chars);
        String association = findData(associationKey);

        displayAssociation(association);

        associationConfirmation = findViewById(R.id.association_confirmation);
        associationConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(associationKey);
            }
        });
    }


    @SuppressLint("SetTextI18n")
    public void setDescription(String keywords) {
        TextView associationLabel = findViewById(R.id.association_label);
        associationLabel.setText("associate " + '"' + keywords + '"');
        associationLabel.setTypeface(null, Typeface.ITALIC);

    }

    public String findData(String associationKey) {
        SharedPreferences sharedPref = getSharedPreferences("association", 0);
        return sharedPref.getString(associationKey, "");


    }

    public void saveData(String associationKey) {
        associationInput = findViewById(R.id.association_message_input);
        SharedPreferences sharedPref = getSharedPreferences("association", 0);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString(associationKey, associationInput.getText().toString());
        edit.apply();

        Toast.makeText(this, "Saving association...", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    public void displayAssociation(String association) {
        associationInput = findViewById(R.id.association_message_input);
        if(association.length() > 0) {
            associationInput.setText("found association! -> " + association);
        }
    }


}
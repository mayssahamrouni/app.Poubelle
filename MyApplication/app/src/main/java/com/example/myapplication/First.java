package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class First extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 5000; // 5 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first); // Vérifie que ce fichier XML existe

        // Délai de 30 secondes avant de passer à MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(First.this, MainActivity.class);
            startActivity(intent);
            finish(); // Ferme FirstActivity pour éviter le retour en arrière
        }, SPLASH_TIME_OUT);
    }
}

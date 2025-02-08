package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap capturedImage;
    private Classifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        Button captureButton = findViewById(R.id.captureButton);

        // Charger le modèle TensorFlow Lite
        try {
            classifier = new Classifier(this, "model.tflite"); // Vérifie le nom de ton modèle
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Prendre une photo avec la caméra
        ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            capturedImage = (Bitmap) extras.get("data");
                            imageView.setImageBitmap(capturedImage);

                            // Classifier immédiatement après la capture
                            classifyImage(capturedImage);
                        }
                    }
                }
        );

        captureButton.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(cameraIntent);
        });
    }

    // Classifier l'image et afficher une alerte
    private void classifyImage(Bitmap image) {
        String result = classifier.classifyImage(image);
        showAlert(result);
    }

    // Afficher une alerte avec le résultat
    private void showAlert(String classification) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Classification Result");

        if ("dirty".equals(classification)) {
            builder.setMessage("The trash can is DIRTY!");
            builder.setIcon(R.drawable.dirty); // Remplace par ton image dans drawable
        } else {
            builder.setMessage("The trash can is CLEAN!");
            builder.setIcon(R.drawable.clean); // Remplace par ton image dans drawable
        }

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}

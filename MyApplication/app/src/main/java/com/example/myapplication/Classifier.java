package com.example.myapplication;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Classifier {

    private Interpreter tflite;

    // Constructeur pour initialiser l'interpréteur
    public Classifier(Context context, String modelPath) throws IOException {
        tflite = new Interpreter(loadModelFile(context, modelPath));
    }

    // Charger le modèle depuis les assets
    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream fileInputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

        fileInputStream.close();
        fileDescriptor.close();

        return mappedByteBuffer;
    }

    // Exécuter l'inférence sur une image
    public String classifyImage(Bitmap image) {
        Bitmap resizedImage = Bitmap.createScaledBitmap(image, 224, 224, true);

        float[][] output = new float[1][1]; // Sortie unique (proba de "sale")

        float[] input = preprocessImage(resizedImage);
        tflite.run(input, output);

        float probability = output[0][0]; // Récupérer la probabilité
        return probability==1 ? "dirty" : "clean"; // Seuil de 50%
    }

    // Prétraitement de l'image
    private float[] preprocessImage(Bitmap image) {
        int width = 224;
        int height = 224;
        int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);

        float[] input = new float[width * height * 3];

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            input[i * 3] = ((pixel >> 16) & 0xFF) / 255.0f;
            input[i * 3 + 1] = ((pixel >> 8) & 0xFF) / 255.0f;
            input[i * 3 + 2] = (pixel & 0xFF) / 255.0f;
        }

        return input;
    }
}

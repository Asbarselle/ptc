package com.example.smartfarming.models;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ModelPredictor {
    private static final String TAG = "ModelPredictor";
    private static final String MODEL_NAME = "tf_model.tflite"; // Nama file model
    private Interpreter tflite;

    public ModelPredictor(Context context) {
        try {
            // Memuat model menggunakan interpreter
            tflite = new Interpreter(loadModelFile(context));
            Log.d(TAG, "Model berhasil dimuat.");
        } catch (IOException e) {
            Log.e(TAG, "Gagal memuat model TensorFlow Lite.", e);
        }
    }

    // Load model file dari folder assets
    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(context.getAssets().openFd(MODEL_NAME).getFileDescriptor());
             FileChannel fileChannel = inputStream.getChannel()) {
            long startOffset = context.getAssets().openFd(MODEL_NAME).getStartOffset();
            long declaredLength = context.getAssets().openFd(MODEL_NAME).getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    // Melakukan prediksi menggunakan model
    public String predict(float[][] inputFeatures) {
        // Output model untuk hasil prediksi (satu nilai, 0 atau 1)
        float[][] output = new float[1][3];

        // Jalankan model
        tflite.run(inputFeatures, output);

        // Ambil nilai prediksi (0 atau 1)
        int prediction = Math.round(output[0][0]);

        // Return hasil prediksi
        if (prediction == 0) {
            return "Tidak Menyiram";
        } else {
            return "Menyiram";
        }
    }

    // Simpan hasil prediksi ke Firebase
    public void savePredictionToFirebase(String hasilPrediksi, String timestampSensor) {
        // Referensi ke node riwayat_prediksi di Firebase
        DatabaseReference riwayatPrediksiRef = FirebaseDatabase.getInstance().getReference("riwayat_prediksi");

        // Simpan data ke Firebase
        riwayatPrediksiRef.child(timestampSensor).setValue(hasilPrediksi)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Prediksi berhasil disimpan ke Firebase.");
                    } else {
                        Log.e(TAG, "Gagal menyimpan prediksi: ", task.getException());
                    }
                });
    }
}

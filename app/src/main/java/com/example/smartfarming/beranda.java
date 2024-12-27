package com.example.smartfarming;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.smartfarming.models.ModelPredictor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class beranda extends AppCompatActivity {
    private static final String TAG = "Beranda";
    private ModelPredictor modelPredictor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_beranda);

        // Inisialisasi ModelPredictor
        modelPredictor = new ModelPredictor(this);

        // Tombol untuk membuka halaman bagan
        CardView btn = findViewById(R.id.lihatBaganButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(beranda.this, bagan.class);
                startActivity(intent);
            }
        });

        // Tampilkan tanggal hari ini
        TextView tanggal = findViewById(R.id.tanggal);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.US);
        String formattedDate = today.format(formatter);
        tanggal.setText(formattedDate);

        // Akses data dari Firebase dan jalankan prediksi
        aksesDataDB();
    }

    private void aksesDataDB() {
        TextView txtTemp = findViewById(R.id.temp);
        TextView txtHumidity = findViewById(R.id.humidity);
        TextView txtSoil = findViewById(R.id.soil);
        TextView txtPrediction = findViewById(R.id.newData); // Tambahkan TextView untuk hasil prediksi

        DatabaseReference refDHT = FirebaseDatabase.getInstance().getReference("DHT21");
        DatabaseReference refSoil = FirebaseDatabase.getInstance().getReference("SoilMoisture");

        refDHT.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double temperature = snapshot.child("Temperature").getValue(Double.class);
                Double humidity = snapshot.child("Humidity").getValue(Double.class);

                // Periksa jika data null
                if (temperature != null) {
                    txtTemp.setText(temperature + "°C");
                } else {
                    txtTemp.setText("N/A");
                }

                if (humidity != null) {
                    txtHumidity.setText(humidity + "%");
                } else {
                    txtHumidity.setText("N/A");
                }

                // Jalankan prediksi setelah data DHT21 berhasil diambil
                refSoil.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer soilMoisture = snapshot.child("Percentage").getValue(Integer.class);

                        if (soilMoisture != null) {
                            txtSoil.setText(soilMoisture + "%");

                            // Jalankan model prediksi
                            float[][] inputFeatures = {
                                    {temperature != null ? temperature.floatValue() : 0.0f},
                                    {humidity != null ? humidity.floatValue() : 0.0f},
                                    {soilMoisture.floatValue()}
                            };


                            String prediction = modelPredictor.predict(inputFeatures);

                            // Tampilkan hasil prediksi di TextView
                            txtPrediction.setText("Hasil Prediksi: " + prediction);

                            // Simpan hasil prediksi ke Firebase
                            String timestamp = String.valueOf(System.currentTimeMillis());
                            modelPredictor.savePredictionToFirebase(prediction, timestamp);
                        } else {
                            txtSoil.setText("N/A");
                            txtPrediction.setText("Prediksi: Tidak tersedia");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Gagal mengambil data SoilMoisture: ", error.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Gagal mengambil data DHT21: ", error.toException());
            }
        });
    }
}

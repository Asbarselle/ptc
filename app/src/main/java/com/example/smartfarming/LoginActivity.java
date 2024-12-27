package com.example.smartfarming;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    // Contoh kredensial statis (untuk demonstrasi)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_halaman_login);

        TextInputEditText emailEditText, passwordEditText;
        MaterialButton loginButton;

        String VALID_EMAIL = "admin";
        String VALID_PASSWORD = "smart";

        // Atur padding untuk system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi view
        loginButton = findViewById(R.id.button);

        // Atur listener untuk tombol login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validasi login
                login(VALID_EMAIL, VALID_PASSWORD);
            }
        });
    }

    private void login(String VALID_EMAIL, String VALID_PASSWORD) {
        TextInputEditText emailEditText, passwordEditText;
        emailEditText = findViewById(R.id.Email);
        passwordEditText = findViewById(R.id.Password);
        // Ambil email dan password yang dimasukkan
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Lakukan validasi
        if (email.isEmpty()) {
            emailEditText.setError("Email tidak boleh kosong");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password tidak boleh kosong");
            return;
        }

        // Periksa kredensial
        if (email.equals(VALID_EMAIL) && password.equals(VALID_PASSWORD)) {
            // Login berhasil
            Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();

            // Pindah ke MainActivity (Anda perlu membuat kelas MainActivity)
            Intent intent = new Intent(LoginActivity.this, beranda.class);
            startActivity(intent);
            finish(); // Tutup aktivitas login
        } else {
            // Login gagal
            Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.santaellafinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CrudActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button btnListarClientes;
    private Button btnListarProductos;
    private Button btninventario;
    private TextView welcomeTextView;
    private Button logoutButton;
    private Button btnGemini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);

        // Ajuste de padding para la barra de sistema (opcional)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Inicializar vistas
        welcomeTextView = findViewById(R.id.welcomeTextView);
        logoutButton = findViewById(R.id.logoutButton);
        btnListarClientes = findViewById(R.id.btnListarClientes);
        btnListarProductos = findViewById(R.id.btnListarProductos);
        btninventario = findViewById(R.id.btninventario);
        btnGemini = findViewById(R.id.btnGemini);

        // Verificar si el usuario está autenticado
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            // Si no hay usuario autenticado, regresar al login
            Intent intent = new Intent(CrudActivity.this, IniciarsActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Mostrar información del usuario en el TextView
        welcomeTextView.setText("Bienvenido: " + user.getEmail());

        // Configurar botón de logout
        logoutButton.setOnClickListener(v -> logout());

        // Configurar los botones de CRUD
        setupButtons();
    }

    private void setupButtons() {
        btnListarClientes.setOnClickListener(view -> {
            Intent intent = new Intent(CrudActivity.this, ClienteActivity.class);
            startActivity(intent);
        });

        btnListarProductos.setOnClickListener(view -> {
            Intent intent = new Intent(CrudActivity.this, ProductActivity.class);
            startActivity(intent);
        });

        btninventario.setOnClickListener(view -> {
            Intent intent = new Intent(CrudActivity.this, InventoryActivity.class);
            startActivity(intent);
        });

        // Configurar el botón btnGemini para abrir GeminiActivity
        btnGemini.setOnClickListener(view -> {
            Intent intent = new Intent(CrudActivity.this, GeminiActivity.class);
            startActivity(intent);
        });
    }

    private void logout() {
        auth.signOut(); // Desconectar al usuario
        // Regresar a la pantalla principal (CrudActivity)
        Intent intent = new Intent(CrudActivity.this, CrudActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar el stack de actividades
        startActivity(intent);
        finish(); // Finalizar esta actividad
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Verificar usuario cada vez que la activity se inicie
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(CrudActivity.this, IniciarsActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
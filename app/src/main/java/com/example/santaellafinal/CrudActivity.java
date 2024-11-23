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
    }

    private void logout() {
        auth.signOut();
        // Regresar a la pantalla de login y limpiar el stack de actividades
        Intent intent = new Intent(CrudActivity.this, IniciarsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            // Si el usuario no está autenticado, redirige a la pantalla de inicio de sesión
            Intent intent = new Intent(CrudActivity.this, IniciarsActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Mostrar información del usuario en el TextView
            welcomeTextView.setText("Bienvenido: " + user.getEmail());
        }
    }

}
package com.example.santaellafinal;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crud);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Verificar si el usuario está autenticado
        if (auth.getCurrentUser() == null) {
            // Si no hay usuario autenticado, regresar al login
            Intent intent = new Intent(CrudActivity.this, IniciarsActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Inicializar vistas
        welcomeTextView = findViewById(R.id.welcomeTextView);
        logoutButton = findViewById(R.id.logoutButton);

        // Mostrar información del usuario
        FirebaseUser user = auth.getCurrentUser();
        welcomeTextView.setText("Bienvenido: " + user.getEmail());

        // Configurar botón de logout
        logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        auth.signOut();
        // Regresar a la pantalla de login
        Intent intent = new Intent(CrudActivity.this, IniciarsActivity.class);
        // Limpiar el stack de activities
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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


        btnListarClientes = (Button) findViewById(R.id.btnListarClientes);
        btnListarClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrudActivity.this, ClienteActivity.class);
                startActivity(intent);
            }
        });

        btnListarProductos = (Button) findViewById(R.id.btnListarProductos);
        btnListarProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrudActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });
        btninventario = (Button) findViewById(R.id.btninventario) ;
        btninventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CrudActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });


    }
    }

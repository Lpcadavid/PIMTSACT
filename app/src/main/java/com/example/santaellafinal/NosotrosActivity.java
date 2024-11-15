package com.example.santaellafinal;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NosotrosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nosotros);

        // Asegura que los márgenes del sistema (notch, barras de estado) sean tomados en cuenta
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Ajusta el padding del contenedor principal según los márgenes de las barras del sistema
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Método para manejar el clic del botón "Más Información"
    public void onMoreInfoClicked(View view) {
        // Aquí puedes abrir una nueva actividad o mostrar más información
        Toast.makeText(this, "Más información", Toast.LENGTH_SHORT).show();
    }
}

package com.example.santaellafinal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

public class GeminiActivity extends AppCompatActivity {

    TextView tvResultado;
    EditText ed1;
    Button btnGenerar;
    FirebaseFirestore db;
    private int tareasPendientes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gemini);

        // Inicializar vistas
        tvResultado = findViewById(R.id.tvResultado);
        ed1 = findViewById(R.id.et1);
        btnGenerar = findViewById(R.id.btnGenerar);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Configurar el botón para generar contenido
        btnGenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = ed1.getText().toString().trim();
                if (query.isEmpty()) {
                    Toast.makeText(GeminiActivity.this, "Por favor ingresa un texto.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Reiniciar TextView y el contador de tareas pendientes
                tvResultado.setText("");
                tareasPendientes = 0;

                // Verificar la consulta del usuario y ejecutar la lógica correspondiente
                if (query.toLowerCase().startsWith("cantidad de ")) {
                    String nombreProducto = query.substring(12).trim();
                    obtenerCantidadProducto(nombreProducto);
                } else if (query.toLowerCase().startsWith("correo de ")) {
                    String nombreCliente = query.substring(10).trim();
                    obtenerCorreoCliente(nombreCliente);
                } else if (query.toLowerCase().contains("productos")) {
                    obtenerProductos();
                } else if (query.toLowerCase().contains("clientes")) {
                    obtenerClientes();
                } else {
                    generarContenido(query);
                }
            }
        });
    }

    private void obtenerProductos() {
        tareasPendientes++;
        db.collection("productos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder productos = new StringBuilder("Productos:\n");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nombre = document.getString("nombre");
                                String categoria = document.getString("categoria");
                                String precio = document.getString("precio");
                                String cantidad = document.getString("cantidad");

                                productos.append("Nombre: ").append(nombre)
                                        .append(", Categoría: ").append(categoria)
                                        .append(", Precio: ").append(precio)
                                        .append(", Cantidad: ").append(cantidad)
                                        .append("\n");
                            }
                            tvResultado.append(productos.toString());
                        } else {
                            tvResultado.append("Error al obtener productos: " + task.getException().getMessage());
                        }
                        tareasCompletadas();
                    }
                });
    }

    private void obtenerClientes() {
        tareasPendientes++;
        db.collection("clientes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder clientes = new StringBuilder("\n\nClientes:\n");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nombre = document.getString("name");
                                String email = document.getString("email");
                                String ciudad = document.getString("ciudad");
                                String edad = document.getString("edad");

                                clientes.append("Nombre: ").append(nombre)
                                        .append(", Email: ").append(email)
                                        .append(", Ciudad: ").append(ciudad)
                                        .append(", Edad: ").append(edad)
                                        .append("\n");
                            }
                            tvResultado.append(clientes.toString());
                        } else {
                            tvResultado.append("\n\nError al obtener clientes: " + task.getException().getMessage());
                        }
                        tareasCompletadas();
                    }
                });
    }

    private void obtenerCantidadProducto(String nombreProducto) {
        tareasPendientes++;
        db.collection("productos")
                .whereEqualTo("nombre", nombreProducto)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder resultado = new StringBuilder("Cantidad de " + nombreProducto + ":\n");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String cantidad = document.getString("cantidad");
                                resultado.append(cantidad != null ? cantidad : "No disponible").append("\n");
                            }
                            tvResultado.append(resultado.toString());
                        } else {
                            tvResultado.append("Error al obtener cantidad: " + task.getException().getMessage());
                        }
                        tareasCompletadas();
                    }
                });
    }

    private void obtenerCorreoCliente(String nombreCliente) {
        tareasPendientes++;
        db.collection("clientes")
                .whereEqualTo("name", nombreCliente)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder resultado = new StringBuilder("Correo de " + nombreCliente + ":\n");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String email = document.getString("email");
                                resultado.append(email != null ? email : "No disponible").append("\n");
                            }
                            tvResultado.append(resultado.toString());
                        } else {
                            tvResultado.append("Error al obtener correo: " + task.getException().getMessage());
                        }
                        tareasCompletadas();
                    }
                });
    }

    private void tareasCompletadas() {
        tareasPendientes--;
        if (tareasPendientes == 0) {
            String query = ed1.getText().toString().trim();
            generarContenido(query);
        }
    }

    private String construirPromptGemini(String query) {
        String firestoreInfo = tvResultado.getText().toString();
        return query + "\nInformación de Firestore:\n" + firestoreInfo;
    }

    private void generarContenido(String query) {
        String prompt = construirPromptGemini(query);

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyC5enlIHk6kTC0N72Brt2epl4L7sqUld7E");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                tvResultado.append("\n\nIA Generada:\n" + resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                tvResultado.append("\n\nError al generar contenido: " + t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }
}

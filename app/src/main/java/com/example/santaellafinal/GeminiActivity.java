package com.example.santaellafinal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

                // Primero, consulta los datos de la colección "productos"
                db.collection("productos") // Suponiendo que la colección se llama "productos"
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    String productos = "";
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Obtén el nombre del producto o los datos que necesites
                                        productos += document.getString("nombre") + "\n";
                                    }
                                    // Mostrar los productos en el TextView
                                    tvResultado.setText("Productos:\n" + productos);
                                } else {
                                    tvResultado.setText("Error al obtener productos.");
                                }
                            }
                        });

                // Luego, consulta los datos de la colección "clientes"
                db.collection("clientes") // Suponiendo que la colección se llama "clientes"
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    String clientes = "";
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Obtén los datos del cliente
                                        clientes += "Nombre: " + document.getString("nombre") +
                                                ", Email: " + document.getString("email") + "\n";
                                    }
                                    // Mostrar los clientes en el TextView
                                    tvResultado.append("\n\nClientes:\n" + clientes);
                                } else {
                                    tvResultado.append("\n\nError al obtener clientes.");
                                }
                            }
                        });

                // Luego, genera el contenido con Gemini
                GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "YOUR_API_KEY");
                GenerativeModelFutures model = GenerativeModelFutures.from(gm);

                Content content = new Content.Builder()
                        .addText("Write a story about a magic backpack.")
                        .build();

                ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

                Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = result.getText(); // Asegúrate de que 'getText()' sea el método adecuado
                        tvResultado.append("\n\nIA Generada:\n" + resultText); // Mostrar el resultado de Gemini
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        tvResultado.setText("Error al generar contenido.");
                    }
                }, MoreExecutors.directExecutor());
            }
        });
    }
}

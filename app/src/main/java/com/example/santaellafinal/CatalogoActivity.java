package com.example.santaellafinal;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.santaellafinal.R;
import com.example.santaellafinal.data.adapter.ProductAdapter;
import com.example.santaellafinal.data.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class CatalogoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance();
        productList = new ArrayList<>();

        // Usar el ProductAdapter sin listener (en caso de que no necesites manejar clics en CatalogoActivity)
        productAdapter = new ProductAdapter(productList);  // Constructor sin listener
        recyclerView.setAdapter(productAdapter);

        // Consultar productos desde Firestore
        fetchProductsFromFirestore();
    }

    // Método para consultar productos desde Firestore
    private void fetchProductsFromFirestore() {
        firestore.collection("productos")  // Asegúrate de que el nombre de la colección sea correcto
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                // Convertir cada documento en un objeto Product
                                Product product = document.toObject(Product.class);
                                product.setId(document.getId());  // Establecer el ID de Firestore como el ID del producto
                                productList.add(product);  // Agregar el producto a la lista
                            }

                            productAdapter.notifyDataSetChanged();  // Notificar al adaptador que los datos han cambiado
                        }
                    } else {
                        Toast.makeText(CatalogoActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CatalogoActivity.this, "Error al consultar Firestore", Toast.LENGTH_SHORT).show();
                });
    }
}

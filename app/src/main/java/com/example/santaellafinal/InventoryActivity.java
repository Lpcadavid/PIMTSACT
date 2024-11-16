package com.example.santaellafinal;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;  // Importar Toast para mostrar mensajes al usuario
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.santaellafinal.data.adapter.InventoryAdapter;
import com.example.santaellafinal.data.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView inventoryRecyclerView;
    private InventoryAdapter inventoryAdapter;
    private List<Product> productList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerView);
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Instancia del adaptador usando la variable global
        inventoryAdapter = new InventoryAdapter(this, productList); // No crear una nueva variable
        inventoryRecyclerView.setAdapter(inventoryAdapter);

        // Cargar productos desde Firebase
        loadInventory();
    }

    private void loadInventory() {
        db.collection("productos")  // Suponiendo que la colección se llama "productos"
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }
                        inventoryAdapter.notifyDataSetChanged();
                    } else {
                        // Si no hay productos, muestra un mensaje
                        Toast.makeText(InventoryActivity.this, "No hay productos disponibles", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("InventoryActivity", "Error al cargar el inventario", e);
                    // Mostrar un mensaje al usuario en caso de error
                    Toast.makeText(InventoryActivity.this, "Error al cargar los productos", Toast.LENGTH_SHORT).show();
           });
}
}
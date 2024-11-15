package com.example.santaellafinal.data.dao;

// Importaciones necesarias para el manejo de productos y Firestore
import com.example.santaellafinal.data.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// Clase para manejar las operaciones de datos de productos en Firestore
public class ProductDao {
    // Instancia de Firestore
    private FirebaseFirestore db;

    // Constructor que recibe la instancia de Firestore
    public ProductDao(FirebaseFirestore db) {
        this.db = db;
    }

    // Método para crear un nuevo producto en Firestore
    public void createProduct(Product product, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        // Añade el producto a la colección "productos"
        db.collection("productos").add(product)
                .addOnSuccessListener(documentReference -> {
                    // Obtener el ID generado por Firestore
                    product.setId(documentReference.getId());
                    // Actualiza el producto con su ID
                    db.collection("productos").document(product.getId()).set(product);
                    // Notifica el éxito al listener
                    onSuccessListener.onSuccess(documentReference);
                })
                .addOnFailureListener(onFailureListener); // Notifica el fallo al listener
    }

    // Método para leer todos los productos de Firestore
    public void readProducts(OnSuccessListener<QuerySnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        // Obtiene todos los documentos de la colección "productos"
        db.collection("productos").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Crea una lista para almacenar los productos
                    List<Product> products = new ArrayList<>();
                    // Itera sobre los documentos obtenidos
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        // Convierte el documento en un objeto Product
                        Product product = document.toObject(Product.class);
                        if (product != null) {
                            product.setId(document.getId());  // Asigna el ID del documento
                            products.add(product); // Añade el producto a la lista
                        }
                    }
                    // Pasa la lista de productos al listener de éxito
                    onSuccessListener.onSuccess(queryDocumentSnapshots);
                })
                .addOnFailureListener(onFailureListener); // Notifica el fallo al listener
    }

    // Método para actualizar un producto existente en Firestore
    public void updateProduct(Product product, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        // Actualiza el documento del producto por su ID
        db.collection("productos").document(product.getId()).set(product)
                .addOnSuccessListener(onSuccessListener) // Notifica el éxito al listener
                .addOnFailureListener(onFailureListener); // Notifica el fallo al listener
    }




    // Método para eliminar un producto de Firestore
    public void deleteProduct(String productId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        // Elimina el documento del producto por su ID
        db.collection("productos").document(productId).delete()
                .addOnSuccessListener(onSuccessListener) // Notifica el éxito al listener
                .addOnFailureListener(onFailureListener); // Notifica el fallo al listener
    }
}
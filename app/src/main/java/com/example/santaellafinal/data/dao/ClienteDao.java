package com.example.santaellafinal.data.dao;

// Importaciones necesarias para el manejo de productos y Firestore
import com.example.santaellafinal.data.model.Clientes;
import com.example.santaellafinal.data.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// Clase para manejar las operaciones de datos de en Firestore
public class ClienteDao {
    // Instancia de Firestore
    private FirebaseFirestore db;

    // Constructor que recibe la instancia de Firestore
    public ClienteDao(FirebaseFirestore db) {
        this.db = db;
    }

    // Método para crear un nuevo producto en Firestore
    public void createCliente(Clientes clientes, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
       // Añade el producto a la colección "productos"
        db.collection("clientes").add(clientes)
                .addOnSuccessListener(documentReference -> {
                    // Obtener el ID generado por Firestore
                    clientes.setId(documentReference.getId());
                    // Actualiza el producto con su ID
                    db.collection("clientes").document(clientes.getId()).set(clientes);
                    // Notifica el éxito al listener
                    onSuccessListener.onSuccess(documentReference);
                })
                .addOnFailureListener(onFailureListener); // Notifica el fallo al listener
    }

    // Método para leer todos los Clientes de Firestore
    public  void readClientes(OnSuccessListener<QuerySnapshot> onSuccessListener, OnFailureListener onFailureListener) {
           db.collection("clientes").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Crea una lista para almacenar los productos
                    List<Clientes> cliente = new ArrayList<>();
                    // Itera sobre los documentos obtenidos
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Clientes clientes = document.toObject(Clientes.class);
                        if (clientes != null) {
                            clientes.setId(document.getId());  // Asigna el ID del documento);
                            cliente.add(clientes); // Añade el producto a la lista
                        }
                    }
                    // Pasa la lista de productos al listener de éxito
                    onSuccessListener.onSuccess(queryDocumentSnapshots);
                })
                .addOnFailureListener(onFailureListener); // Notifica el fallo al listener
    }



    // Método para actualizar un producto existente en Firestore
    public void updateCliente(Clientes cliente, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        // Actualiza el documento del cliente por su ID
        db.collection("clientes").document(cliente.getId()).set(cliente)
                .addOnSuccessListener(onSuccessListener) // Notifica el éxito al listener
                .addOnFailureListener(onFailureListener); // Notifica el fallo al listener
    }


    // Método para eliminar un producto de Firestore
    public void deleteCliente(String clienteId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
            // Elimina el documento del producto por su ID
        db.collection("clientes").document(clienteId).delete()
                .addOnSuccessListener(onSuccessListener) // Notifica el éxito al listener
                .addOnFailureListener(onFailureListener); // Notifica el fallo al listener
    }
}
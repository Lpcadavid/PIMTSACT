package com.example.santaellafinal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.santaellafinal.data.adapter.ProductAdapter;
import com.example.santaellafinal.data.dao.ProductDao;
import com.example.santaellafinal.data.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ProductActivity extends AppCompatActivity {

    private Button btnCrearProducto, btnLeerProductos, btnActualizarProducto, btnEliminarProducto, btnUpload;
    private RecyclerView recyclerViewPro;
    private ProductDao productDao;
    private ProductAdapter productAdapter;
    private EditText etNombre, etPrecio, etCategoria, etCantidad, etFecha;
    private Product selectedProduct; // Producto seleccionado para actualizar o eliminar

    private String imageUrl = null; // Aquí guardaremos la URL de la imagen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Inicializa los botones y vistas
        btnCrearProducto = findViewById(R.id.btnCrearProducto);
        btnLeerProductos = findViewById(R.id.btnLeerProductos);
        btnActualizarProducto = findViewById(R.id.btnActualizarProducto);
        btnEliminarProducto = findViewById(R.id.btnEliminarProducto);
        recyclerViewPro = findViewById(R.id.recyclerViewPro);
        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etCategoria = findViewById(R.id.etCategoria);
        etCantidad = findViewById(R.id.etCantidad);
        etFecha = findViewById(R.id.etFecha);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        productDao = new ProductDao(db);

        recyclerViewPro.setLayoutManager(new LinearLayoutManager(this));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        btnUpload = findViewById(R.id.btnUpload);

        // Register the photo picker
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);

                // Upload the image to Firebase Storage
                StorageReference fileRef = storageRef.child("images/" + UUID.randomUUID().toString() + ".jpg");

                UploadTask uploadTask = fileRef.putFile(uri);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    fileRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        imageUrl = uri1.toString();
                        Log.d("ImageUpload", "Image URL: " + imageUrl);
                    }).addOnFailureListener(e -> {
                        Log.e("ImageUpload", "Failed to get download URL", e);
                    });
                }).addOnFailureListener(e -> {
                    Log.e("PhotoPicker", "Upload failed", e);
                });
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        btnUpload.setOnClickListener(view -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

        // Agregar un listener para el campo de fecha, mostrando el calendario
        etFecha.setOnClickListener(view -> {
            // Obtener la fecha actual
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Crear el DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(ProductActivity.this, (view1, selectedYear, selectedMonth, selectedDay) -> {
                // Establecer la fecha seleccionada en el campo de texto
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                etFecha.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.show();
        });

        // Crear Producto
        btnCrearProducto.setOnClickListener(view -> {
            if (validarCampos()) {
                Product product = new Product();
                product.setNombre(etNombre.getText().toString());
                product.setPrecio(etPrecio.getText().toString());
                product.setCategoria(etCategoria.getText().toString());
                product.setCantidad(etCantidad.getText().toString());
                product.setFecha(etFecha.getText().toString());

                if (imageUrl != null) {
                    product.setImageUrl(imageUrl); // Set the image URL to the product
                }

                // Create the product in Firestore with the image URL
                productDao.createProduct(product, documentReference -> {
                    product.setId(documentReference.getId());
                    Toast.makeText(ProductActivity.this, "Producto creado", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    refreshProductList();
                }, e -> {
                    Toast.makeText(ProductActivity.this, "Error al crear producto", Toast.LENGTH_SHORT).show();
                });
            }
        });

        // Configuración del adaptador
        productAdapter = new ProductAdapter(new ArrayList<>(), product -> {
            selectedProduct = product;
            etNombre.setText(product.getNombre());
            etPrecio.setText(product.getPrecio());
            etCategoria.setText(product.getCategoria());
            etCantidad.setText(product.getCantidad());
            etFecha.setText(product.getFecha());
        });
        recyclerViewPro.setAdapter(productAdapter);

        btnLeerProductos.setOnClickListener(view -> refreshProductList());

        btnActualizarProducto.setOnClickListener(view -> {
            if (selectedProduct != null && selectedProduct.getId() != null) {
                if (validarCampos()) {
                    selectedProduct.setNombre(etNombre.getText().toString());
                    selectedProduct.setPrecio(etPrecio.getText().toString());
                    selectedProduct.setCategoria(etCategoria.getText().toString());
                    selectedProduct.setCantidad(etCantidad.getText().toString());
                    selectedProduct.setFecha(etFecha.getText().toString());

                    if (imageUrl != null) {
                        selectedProduct.setImageUrl(imageUrl); // Actualizamos la URL de la imagen
                    }

                    productDao.updateProduct(selectedProduct, aVoid -> {
                        Toast.makeText(ProductActivity.this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        refreshProductList();
                    }, e -> {
                        Toast.makeText(ProductActivity.this, "Error al actualizar producto", Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                Toast.makeText(ProductActivity.this, "Selecciona un producto válido", Toast.LENGTH_SHORT).show();
            }
        });

        btnEliminarProducto.setOnClickListener(view -> {
            if (selectedProduct != null && selectedProduct.getId() != null) {
                productDao.deleteProduct(selectedProduct.getId(), aVoid -> {
                    Toast.makeText(ProductActivity.this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    refreshProductList();
                }, e -> {
                    Toast.makeText(ProductActivity.this, "Error al eliminar producto", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(ProductActivity.this, "Selecciona un producto válido para eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para validar que todos los campos estén llenos
    private boolean validarCampos() {
        if (etNombre.getText().toString().isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPrecio.getText().toString().isEmpty()) {
            Toast.makeText(this, "El precio es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCategoria.getText().toString().isEmpty()) {
            Toast.makeText(this, "La categoría es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCantidad.getText().toString().isEmpty()) {
            Toast.makeText(this, "La cantidad es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etFecha.getText().toString().isEmpty()) {
            Toast.makeText(this, "La fecha es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void refreshProductList() {
        productDao.readProducts(queryDocumentSnapshots -> {
            List<Product> products = queryDocumentSnapshots.toObjects(Product.class);
            productAdapter.setProducts(products);
        }, e -> {
            Toast.makeText(ProductActivity.this, "Error al leer productos", Toast.LENGTH_SHORT).show();
        });
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etPrecio.setText("");
        etCategoria.setText("");
        etCantidad.setText("");
        etFecha.setText("");
        imageUrl = null;  // Limpiar la URL de la imagen
        selectedProduct = null;
    }
}

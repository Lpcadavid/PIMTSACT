package com.example.santaellafinal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.santaellafinal.data.adapter.ClienteAdapter;
import com.example.santaellafinal.data.dao.ClienteDao;
import com.example.santaellafinal.data.model.Clientes;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClienteActivity extends AppCompatActivity {
    private Button btnCrearCliente, btnLeerCliente, btnActualizarCliente, btnEliminarCliente;
    private RecyclerView recyclerViewCli;
    private ClienteDao clienteDao;
    private ClienteAdapter clienteAdapter;
    private EditText etNombreCliente, etEmail, etEdad, etCiudad;
    private Clientes selectedCliente; // Cliente seleccionado para actualizar o eliminar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        btnCrearCliente = findViewById(R.id.btnCrearCliente);
        btnLeerCliente = findViewById(R.id.btnLeerCliente);
        btnActualizarCliente = findViewById(R.id.btnActualizarCliente);
        btnEliminarCliente = findViewById(R.id.btnEliminarCliente);
        recyclerViewCli = findViewById(R.id.recyclerViewCli);
        etNombreCliente = findViewById(R.id.etNombreCliente);
        etEmail = findViewById(R.id.etEmail);
        etEdad = findViewById(R.id.etEdad);
        etCiudad = findViewById(R.id.etCiudad);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        clienteDao = new ClienteDao(db);

        recyclerViewCli.setLayoutManager(new LinearLayoutManager(this));

        clienteAdapter = new ClienteAdapter(new ArrayList<>(), cliente -> {
            selectedCliente = cliente;
            etNombreCliente.setText(cliente.getName());
            etEmail.setText(cliente.getEmail());
            etEdad.setText(cliente.getEdad());
            etCiudad.setText(cliente.getCiudad());
        });
        recyclerViewCli.setAdapter(clienteAdapter);

        // Crear Cliente
        btnCrearCliente.setOnClickListener(view -> {
            if (validarCampos()) {
                Clientes clientes = new Clientes();
                clientes.setName(etNombreCliente.getText().toString());
                clientes.setEmail(etEmail.getText().toString());
                clientes.setEdad(etEdad.getText().toString());
                clientes.setCiudad(etCiudad.getText().toString());

                clienteDao.createCliente(clientes, documentReference -> {
                    clientes.setId(documentReference.getId());
                    Toast.makeText(ClienteActivity.this, "Cliente creado", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    refreshClientesList();
                }, e -> {
                    Toast.makeText(ClienteActivity.this, "Error al crear Cliente", Toast.LENGTH_SHORT).show();
                });
            }
        });

        // Leer Clientes
        btnLeerCliente.setOnClickListener(view -> refreshClientesList());

        // Actualizar Cliente
        btnActualizarCliente.setOnClickListener(view -> {
            if (selectedCliente != null && selectedCliente.getId() != null && !selectedCliente.getId().isEmpty()) {
                if (validarCampos()) {
                    selectedCliente.setName(etNombreCliente.getText().toString());
                    selectedCliente.setEmail(etEmail.getText().toString());
                    selectedCliente.setEdad(etEdad.getText().toString());
                    selectedCliente.setCiudad(etCiudad.getText().toString());

                    clienteDao.updateCliente(selectedCliente, aVoid -> {
                        Toast.makeText(ClienteActivity.this, "Cliente actualizado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        refreshClientesList();
                    }, e -> {
                        Toast.makeText(ClienteActivity.this, "Error al actualizar el Cliente", Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                Toast.makeText(ClienteActivity.this, "Selecciona un Cliente válido para actualizar", Toast.LENGTH_SHORT).show();
            }
        });

        // Eliminar Cliente
        btnEliminarCliente.setOnClickListener(view -> {
            if (selectedCliente != null && selectedCliente.getId() != null && !selectedCliente.getId().isEmpty()) {
                clienteDao.deleteCliente(selectedCliente.getId(), aVoid -> {
                    Toast.makeText(ClienteActivity.this, "Cliente eliminado", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    refreshClientesList();
                }, e -> {
                    Toast.makeText(ClienteActivity.this, "Error al eliminar el Cliente", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(ClienteActivity.this, "Selecciona un Cliente válido para eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para validar que todos los campos estén llenos
    private boolean validarCampos() {
        if (etNombreCliente.getText().toString().isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "El correo electrónico es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etEdad.getText().toString().isEmpty()) {
            Toast.makeText(this, "La edad es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCiudad.getText().toString().isEmpty()) {
            Toast.makeText(this, "La ciudad es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void refreshClientesList() {
        clienteDao.readClientes(queryDocumentSnapshots -> {
            List<Clientes> clientes = queryDocumentSnapshots.toObjects(Clientes.class);
            clienteAdapter.setClientes(clientes);
        }, e -> {
            Toast.makeText(ClienteActivity.this, "Error al leer Clientes", Toast.LENGTH_SHORT).show();
        });
    }

    private void limpiarCampos() {
        etNombreCliente.setText("");
        etEmail.setText("");
        etEdad.setText("");
        etCiudad.setText("");
        selectedCliente = null;
    }
}

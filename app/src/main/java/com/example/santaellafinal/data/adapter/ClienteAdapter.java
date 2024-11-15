package com.example.santaellafinal.data.adapter;

// Importaciones necesarias para el funcionamiento del adaptador
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.santaellafinal.R;
import com.example.santaellafinal.data.model.Clientes;

import java.util.List;

// Adaptador para mostrar una lista de Clientes en un RecyclerView
public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {


    private List<Clientes> clientesList;

    private OnClienteClickListener listener;


    public interface OnClienteClickListener {
        void onClienteClick(Clientes clientes);

    }

    public ClienteAdapter(List<Clientes> clientesList, OnClienteClickListener listener) {
        this.clientesList = clientesList;
        this.listener = listener;
    }

    // Crea nuevas vistas (invocado por el layout manager)
    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clientes, parent, false);
        // Devuelve un nuevo objeto ViewHolder con la vista inflada
        return new ClienteViewHolder(itemView);
    }

    // Reemplaza el contenido de una vista (invocado por el layout manager)
    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Clientes clientes = clientesList.get(position);
        holder.bind(clientes, listener);
    }


    @Override
    public int getItemCount() {
        return clientesList.size();
    }

   public void setClientes(List<Clientes> clientes) {
        this.clientesList = clientes;
        notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
   }

   class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView clientesName, clientesEmail, clientesEdad, clientesCiudad;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializa los TextViews utilizando findViewById
            clientesName = itemView.findViewById(R.id.clientesName);
            clientesEmail = itemView.findViewById(R.id.clientesEmail);
            clientesEdad = itemView.findViewById(R.id.clientesEdad);
            clientesCiudad = itemView.findViewById(R.id.clientesCiudad);
        }

        public void bind(final Clientes clientes, final OnClienteClickListener listener) {

            clientesName.setText(clientes.getName());
            clientesEmail.setText(clientes.getEmail());
            clientesEdad.setText(clientes.getEdad());
            clientesCiudad.setText(clientes.getCiudad());


            // Configura un listener para el clic en el item
            itemView.setOnClickListener(v -> {
                listener.onClienteClick(clientes); // Llama al método de la interfaz para manejar el clic
            });


        }
    }
}
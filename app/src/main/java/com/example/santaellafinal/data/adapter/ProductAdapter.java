package com.example.santaellafinal.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.santaellafinal.R;
import com.example.santaellafinal.data.model.Product;

import java.util.List;

// Adaptador para mostrar una lista de productos en un RecyclerView
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnProductClickListener listener;

    // Interfaz para definir el comportamiento al hacer clic en un producto
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    // Constructor del adaptador que recibe la lista de productos y un listener
    // Usado cuando se necesita manejar el clic en un producto
    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    // Constructor del adaptador sin listener
    // Usado para cuando solo queremos mostrar productos sin manejar clics (por ejemplo, en CatalogoActivity)
    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
        this.listener = null; // No manejamos clics aquí
    }

    // Crea nuevas vistas (invocado por el layout manager)
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el diseño del item del producto
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    // Reemplaza el contenido de una vista (invocado por el layout manager)
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Obtiene el producto en la posición dada
        Product product = productList.get(position);
        holder.bind(product, listener);
    }

    // Devuelve el tamaño de la lista de productos
    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Método para actualizar la lista de productos y notificar al adaptador
    public void setProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
    }

    // Clase interna que representa el ViewHolder para cada producto
    class ProductViewHolder extends RecyclerView.ViewHolder {

        // Referencias a los TextViews para mostrar el nombre y el precio
        TextView textViewNombre, textViewPrecio, textViewCategoria, textViewCantidad, textViewFecha;
        ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializa los TextViews utilizando findViewById
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewPrecio = itemView.findViewById(R.id.textViewPrecio);
            textViewCategoria = itemView.findViewById(R.id.textViewCategoria);
            textViewCantidad = itemView.findViewById(R.id.textViewCantidad);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);

            // Inicializa el ImageView para cargar la imagen
            productImage = itemView.findViewById(R.id.product_image);
        }

        // Método para vincular los datos del producto a la vista
        public void bind(final Product product, final OnProductClickListener listener) {
            // Establece el nombre, precio, etc., del producto en los TextViews
            textViewNombre.setText(product.getNombre());
            textViewPrecio.setText(product.getPrecio());
            textViewCategoria.setText(product.getCategoria());
            textViewCantidad.setText(product.getCantidad());
            textViewFecha.setText(product.getFecha());

            // Usar Glide para cargar la imagen en el ImageView
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())  // El contexto del item
                        .load(product.getImageUrl())  // La URL de la imagen
                        .placeholder(R.drawable.santaellalogo)  // Imagen que se muestra mientras carga
                        .error(R.drawable.santaellalogo)  // Imagen de error si falla la carga
                        .into(productImage);  // Cargar en el ImageView
            }

            // Si hay un listener, lo configuramos para manejar el clic
            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onProductClick(product));
            }
        }
    }
}

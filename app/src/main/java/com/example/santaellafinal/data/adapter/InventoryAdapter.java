package com.example.santaellafinal.data.adapter;
import android.content.Context;
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

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<Product> productList;
    private Context context;

    public InventoryAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getNombre());
        holder.productCategory.setText("Categoría: " + product.getCategoria());
        holder.productPrice.setText("Precio: " + product.getPrecio());
        holder.productQuantity.setText("Cantidad: " + product.getCantidad());
        holder.productDate.setText("Fecha: " + product.getFecha());

        // Cargar la imagen usando Glide
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.santaellalogo)  // Añade una imagen placeholder
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productCategory, productPrice, productQuantity, productDate;
        ImageView productImage;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productCategory = itemView.findViewById(R.id.productCategory);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productDate = itemView.findViewById(R.id.productDate);
            productImage = itemView.findViewById(R.id.productImage);
        }
    }
}

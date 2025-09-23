package com.example.easyspace.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.easyspace.R;
import com.example.easyspace.models.Categoria;
import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {
    private Context context;
    private List<Categoria> categorias;

    public CategoriaAdapter(Context context, List<Categoria> categorias) {
        this.context = context;
        this.categorias = categorias;
    }

    public void updateData(List<Categoria> newCategorias) {
        this.categorias = newCategorias;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        Categoria categoria = categorias.get(position);
        holder.textViewCategoria.setText(categoria.getNome());
        holder.imageViewCategoria.setImageResource(categoria.getIconResId());
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCategoria;
        TextView textViewCategoria;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCategoria = itemView.findViewById(R.id.imageViewCategoria);
            textViewCategoria = itemView.findViewById(R.id.textViewCategoria);
        }
    }
}

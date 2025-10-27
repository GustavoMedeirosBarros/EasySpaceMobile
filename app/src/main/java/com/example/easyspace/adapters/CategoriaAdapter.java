package com.example.easyspace.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.easyspace.R;
import com.example.easyspace.models.Categoria;
import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    private Context context;
    private List<Categoria> categorias;
    private OnCategoryClickListener listener;
    private int selectedPosition = -1;

    public interface OnCategoryClickListener {
        void onCategoryClick(Categoria categoria);
    }

    public CategoriaAdapter(Context context, List<Categoria> categorias) {
        this.context = context;
        this.categorias = categorias;
    }

    public CategoriaAdapter(Context context, List<Categoria> categorias, OnCategoryClickListener listener) {
        this.context = context;
        this.categorias = categorias;
        this.listener = listener;
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

        if (holder.getAdapterPosition() == selectedPosition) {
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setScaleX(1.1f);
            holder.itemView.setScaleY(1.1f);
        } else {
            holder.itemView.setAlpha(0.7f);
            holder.itemView.setScaleX(1.0f);
            holder.itemView.setScaleY(1.0f);
        }

        holder.itemView.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition == RecyclerView.NO_POSITION) return;

            int previousPosition = selectedPosition;
            if (selectedPosition == clickedPosition) {
                selectedPosition = -1;
                if (listener != null) {
                    listener.onCategoryClick(null);
                }
            } else {
                selectedPosition = clickedPosition;
                if (listener != null) {
                    listener.onCategoryClick(categorias.get(clickedPosition));
                }
            }
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return categorias != null ? categorias.size() : 0;
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

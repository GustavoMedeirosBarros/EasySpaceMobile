package com.example.easyspace.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.R;
import com.example.easyspace.models.Categoria;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {

    private Context context;
    private List<Categoria> categoriasList;
    private OnCategoriaClickListener clickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnCategoriaClickListener {
        void onCategoriaClick(Categoria categoria);
    }

    public CategoriaAdapter(Context context, List<Categoria> categoriasList, OnCategoriaClickListener clickListener) {
        this.context = context;
        this.categoriasList = categoriasList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categoria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Categoria categoria = categoriasList.get(position);
        holder.textViewNome.setText(categoria.getNome());


        holder.imageViewIcone.setImageResource(categoria.getIconeResId());

        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            holder.textViewNome.setTextColor(ContextCompat.getColor(context, R.color.primary_dark));
            holder.imageViewIcone.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_dark)));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.background_card));
            holder.textViewNome.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            holder.imageViewIcone.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.icon_tint_unselected)));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            if (selectedPosition == position) {
                selectedPosition = RecyclerView.NO_POSITION;
                notifyItemChanged(previousSelected);
            } else {
                selectedPosition = holder.getAdapterPosition();
                if (previousSelected != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousSelected);
                }
                notifyItemChanged(selectedPosition);
            }
            clickListener.onCategoriaClick(categoria);
        });
    }

    @Override
    public int getItemCount() {
        return categoriasList.size();
    }

    public void clearSelection() {
        int previousSelected = selectedPosition;
        selectedPosition = RecyclerView.NO_POSITION;
        if (previousSelected != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelected);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView imageViewIcone;
        TextView textViewNome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewCategoria);
            imageViewIcone = itemView.findViewById(R.id.imageViewIcone);
            textViewNome = itemView.findViewById(R.id.textViewNome);
        }
    }
}
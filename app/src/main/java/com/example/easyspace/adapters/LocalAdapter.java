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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.easyspace.R;
import com.example.easyspace.models.Local;

import java.util.ArrayList;
import java.util.List;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalViewHolder> {

    private Context context;
    private List<Local> locais;

    public LocalAdapter(Context context) {
        this.context = context;
        this.locais = new ArrayList<>();
    }

    public void updateData(List<Local> newLocais) {
        this.locais.clear();
        this.locais.addAll(newLocais);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local, parent, false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
        Local local = locais.get(position);

        holder.textViewNome.setText(local.getNome());
        holder.textViewDescricao.setText(local.getDescricao());
        holder.textViewPreco.setText(String.format("R$ %.2f/hora", local.getPreco()));
        holder.textViewLocalizacao.setText(local.getLocalizacao());
        holder.textViewRating.setText(String.format("%.1f", local.getRating()));

        // Carregar imagem com Glide
        if (local.getImagemUrl() != null && !local.getImagemUrl().isEmpty()) {
            Glide.with(context)
                    .load(local.getImagemUrl())
                    .placeholder(R.drawable.ic_default_space) // Imagem enquanto carrega
                    .error(R.drawable.ic_default_space) // Imagem se der erro
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(holder.imageViewLocal);
        } else {
            holder.imageViewLocal.setImageResource(R.drawable.ic_default_space);
        }
    }

    @Override
    public int getItemCount() {
        return locais.size();
    }

    static class LocalViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewLocal;
        TextView textViewNome, textViewDescricao, textViewPreco, textViewLocalizacao, textViewRating;

        public LocalViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewLocal = itemView.findViewById(R.id.imageViewLocal);
            textViewNome = itemView.findViewById(R.id.textViewNome);
            textViewDescricao = itemView.findViewById(R.id.textViewDescricao);
            textViewPreco = itemView.findViewById(R.id.textViewPreco);
            textViewLocalizacao = itemView.findViewById(R.id.textViewLocalizacao);
            textViewRating = itemView.findViewById(R.id.textViewRating);
        }
    }
}

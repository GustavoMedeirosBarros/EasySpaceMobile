package com.example.easyspace.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.easyspace.LocalDetailActivity;
import com.example.easyspace.R;
import com.example.easyspace.models.Local;
import com.example.easyspace.utils.FirebaseManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalVerticalAdapter extends RecyclerView.Adapter<LocalVerticalAdapter.LocalViewHolder> {

    private Context context;
    private List<Local> locais;
    private FirebaseManager firebaseManager;
    private Set<String> favoritesSet;

    public LocalVerticalAdapter(Context context, List<Local> locais) {
        this.context = context;
        this.locais = locais;
        this.firebaseManager = new FirebaseManager();
        this.favoritesSet = new HashSet<>();
        loadFavorites();
    }

    private void loadFavorites() {
        if (!firebaseManager.isLoggedIn()) return;

        firebaseManager.getUserFavorites(new FirebaseManager.FavoritesCallback() {
            @Override
            public void onSuccess(List<Local> favorites) {
                favoritesSet.clear();
                for (Local local : favorites) {
                    if (local.getId() != null) {
                        favoritesSet.add(local.getId());
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local_vertical, parent, false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
        Local local = locais.get(position);

        holder.textViewNome.setText(local.getNome());
        holder.textViewEndereco.setText(local.getEndereco());
        holder.textViewPreco.setText(String.format("R$ %.0f", local.getPreco()));
        holder.textViewAvaliacao.setText(local.getRatingFormatado());
        holder.textViewCategoria.setText(local.getCategoria());
        holder.textViewTipoLocacao.setText(local.getTipoLocacao().equals("hora") ? " / hora" : " / dia");

        if (local.getImageUrl() != null && !local.getImageUrl().isEmpty()) {
            String imageUrl = local.getImageUrl();

            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_default_space)
                        .error(R.drawable.ic_default_space)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(holder.imageViewLocal);
            } else {
                try {
                    byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                    Glide.with(context)
                            .load(decodedString)
                            .placeholder(R.drawable.ic_default_space)
                            .error(R.drawable.ic_default_space)
                            .centerCrop()
                            .into(holder.imageViewLocal);
                } catch (Exception e) {
                    holder.imageViewLocal.setImageResource(R.drawable.ic_default_space);
                }
            }
        } else {
            holder.imageViewLocal.setImageResource(R.drawable.ic_default_space);
        }

        boolean isFavorite = local.getId() != null && favoritesSet.contains(local.getId());
        holder.buttonFavorite.setImageResource(isFavorite ?
                R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);

        holder.buttonFavorite.setOnClickListener(v -> {
            if (!firebaseManager.isLoggedIn()) {
                Toast.makeText(context, "Faça login para adicionar favoritos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (local.getId() == null) return;

            boolean currentlyFavorite = favoritesSet.contains(local.getId());
            if (currentlyFavorite) {
                firebaseManager.removeFromFavorites(local.getId(), new FirebaseManager.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        favoritesSet.remove(local.getId());
                        notifyItemChanged(holder.getAdapterPosition());
                        Toast.makeText(context, "Removido dos favoritos", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(context, "Erro ao remover", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                firebaseManager.addToFavorites(local.getId(), new FirebaseManager.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        favoritesSet.add(local.getId());
                        notifyItemChanged(holder.getAdapterPosition());
                        Toast.makeText(context, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(context, "Erro ao adicionar", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LocalDetailActivity.class);
            intent.putExtra("local", local);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return locais != null ? locais.size() : 0;
    }

    public void updateData(List<Local> newLocais) {
        this.locais = newLocais;
        notifyDataSetChanged();
    }

    public static class LocalViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewLocal;
        ImageButton buttonFavorite;
        TextView textViewNome, textViewEndereco, textViewPreco, textViewAvaliacao, textViewCategoria, textViewTipoLocacao;

        public LocalViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewLocal = itemView.findViewById(R.id.imageViewLocal);
            buttonFavorite = itemView.findViewById(R.id.buttonFavorite);
            textViewNome = itemView.findViewById(R.id.textViewNome);
            textViewEndereco = itemView.findViewById(R.id.textViewEndereco);
            textViewPreco = itemView.findViewById(R.id.textViewPreco);
            textViewAvaliacao = itemView.findViewById(R.id.textViewAvaliacao);
            textViewCategoria = itemView.findViewById(R.id.textViewCategoria);
            textViewTipoLocacao = itemView.findViewById(R.id.textViewTipoLocacao);
        }
    }
}
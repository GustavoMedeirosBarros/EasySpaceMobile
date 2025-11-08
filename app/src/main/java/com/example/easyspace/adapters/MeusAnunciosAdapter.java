package com.example.easyspace.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.easyspace.R;
import com.example.easyspace.models.Local;
import java.util.List;

public class MeusAnunciosAdapter extends RecyclerView.Adapter<MeusAnunciosAdapter.AnuncioViewHolder> {

    private Context context;
    private List<Local> localList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Local local);
        void onEditClick(Local local);
        void onDeleteClick(Local local);
    }

    public MeusAnunciosAdapter(Context context, List<Local> localList, OnItemClickListener listener) {
        this.context = context;
        this.localList = localList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meu_anuncio, parent, false);
        return new AnuncioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnuncioViewHolder holder, int position) {
        Local local = localList.get(position);
        holder.bind(local, listener);
    }

    @Override
    public int getItemCount() {
        return localList.size();
    }

    public void updateData(List<Local> newLocalList) {
        this.localList.clear();
        this.localList.addAll(newLocalList);
        notifyDataSetChanged();
    }

    class AnuncioViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewLocal;
        TextView textViewNome, textViewCategoria, textViewPreco;
        ImageButton buttonEdit, buttonDelete;

        AnuncioViewHolder(View itemView) {
            super(itemView);
            imageViewLocal = itemView.findViewById(R.id.imageViewLocal);
            textViewNome = itemView.findViewById(R.id.textViewNome);
            textViewCategoria = itemView.findViewById(R.id.textViewCategoria);
            textViewPreco = itemView.findViewById(R.id.textViewPreco);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        void bind(Local local, OnItemClickListener listener) {
            textViewNome.setText(local.getNome());
            textViewCategoria.setText(local.getCategoria());
            textViewPreco.setText(local.getPrecoFormatado());

            String imageUrl = local.getImageUrl();

            if (imageUrl != null && !imageUrl.isEmpty()) {
                if (imageUrl.startsWith("http") || imageUrl.startsWith("https://")) {
                    Glide.with(context).load(imageUrl).into(imageViewLocal);
                } else {
                    try {
                        byte[] decodedString = android.util.Base64.decode(imageUrl, android.util.Base64.DEFAULT);
                        android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        Glide.with(context).load(decodedByte).into(imageViewLocal);
                    } catch (Exception e) {
                        Glide.with(context).load(R.drawable.ic_default_space).into(imageViewLocal);
                    }
                }
            } else {
                Glide.with(context).load(R.drawable.ic_default_space).into(imageViewLocal);
            }


            itemView.setOnClickListener(v -> listener.onItemClick(local));
            buttonEdit.setOnClickListener(v -> listener.onEditClick(local));
            buttonDelete.setOnClickListener(v -> listener.onDeleteClick(local));
        }
    }
}
package com.example.easyspace.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.easyspace.LocalDetailActivity;
import com.example.easyspace.R;
import com.example.easyspace.models.Reserva;
import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    private Context context;
    private List<Reserva> reservasList;

    public ReservaAdapter(Context context, List<Reserva> reservasList) {
        this.context = context;
        this.reservasList = reservasList;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reserva, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = reservasList.get(position);
        if (reserva == null) return;

        holder.textViewNomeLocal.setText(reserva.getLocalNome());
        holder.textViewDatas.setText(reserva.getDatasFormatadas());
        holder.textViewStatus.setText(reserva.getStatusFormatado());
        holder.textViewPreco.setText(reserva.getPrecoTotalFormatado());

        if ("confirmed".equals(reserva.getStatus())) {
            holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.success));
        } else if ("pending".equals(reserva.getStatus())) {
            holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.primary_dark));
        } else {
            holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.error));
        }

        String imageUrl = reserva.getLocalImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http") || imageUrl.startsWith("https")) {
                Glide.with(context).load(imageUrl).centerCrop().into(holder.imageViewLocal);
            } else {
                try {
                    byte[] decodedString = android.util.Base64.decode(imageUrl, android.util.Base64.DEFAULT);
                    Glide.with(context).load(decodedString).centerCrop().into(holder.imageViewLocal);
                } catch (Exception e) {
                    holder.imageViewLocal.setImageResource(R.drawable.ic_default_space);
                }
            }
        } else {
            holder.imageViewLocal.setImageResource(R.drawable.ic_default_space);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LocalDetailActivity.class);
            intent.putExtra("localId", reserva.getLocalId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reservasList != null ? reservasList.size() : 0;
    }

    public void updateData(List<Reserva> novasReservas) {
        this.reservasList.clear();
        this.reservasList.addAll(novasReservas);
        notifyDataSetChanged();
    }

    public static class ReservaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewLocal;
        TextView textViewNomeLocal, textViewDatas, textViewStatus, textViewPreco;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewLocal = itemView.findViewById(R.id.imageViewLocal);
            textViewNomeLocal = itemView.findViewById(R.id.textViewNomeLocal);
            textViewDatas = itemView.findViewById(R.id.textViewDatas);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewPreco = itemView.findViewById(R.id.textViewPreco);
        }
    }
}
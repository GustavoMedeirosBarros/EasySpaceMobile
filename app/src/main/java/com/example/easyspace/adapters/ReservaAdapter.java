package com.example.easyspace.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyspace.LocalDetailActivity;
import com.example.easyspace.R;
import com.example.easyspace.models.Reserva;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

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
        holder.textViewPeriodo.setText(reserva.getDatasFormatadas());
        holder.textViewPrecoTotal.setText(reserva.getPrecoTotalFormatado());

        String status = reserva.getStatus();
        holder.chipStatus.setText(getStatusTraduzido(status));

        int colorId;
        if ("confirmed".equals(status)) {
            colorId = R.color.success;
        } else if ("pending".equals(status)) {
            colorId = R.color.primary;
        } else if ("cancelled".equals(status)) {
            colorId = R.color.error;
        } else {
            colorId = R.color.text_secondary;
        }
        holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, colorId)));

        String imageUrl = reserva.getLocalImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http")) {
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

        holder.buttonCancelar.setOnClickListener(v -> {
            Toast.makeText(context, "Cancelar reserva: " + reserva.getLocalNome(), Toast.LENGTH_SHORT).show();
        });
    }

    private String getStatusTraduzido(String status) {
        if (status == null) return "Desconhecido";
        switch (status) {
            case "confirmed": return "Confirmada";
            case "pending": return "Pendente";
            case "cancelled": return "Cancelada";
            case "completed": return "Concluída";
            default: return status;
        }
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
        TextView textViewNomeLocal, textViewPeriodo, textViewPrecoTotal;
        Chip chipStatus;
        MaterialButton buttonCancelar;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewLocal = itemView.findViewById(R.id.imageViewLocal);
            textViewNomeLocal = itemView.findViewById(R.id.textViewNomeLocal);
            textViewPeriodo = itemView.findViewById(R.id.textViewPeriodo);
            textViewPrecoTotal = itemView.findViewById(R.id.textViewPrecoTotal);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            buttonCancelar = itemView.findViewById(R.id.buttonCancelar);
        }
    }
}
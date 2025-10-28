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
import com.example.easyspace.models.Reserva;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    private Context context;
    private List<Reserva> reservas;
    private SimpleDateFormat dateFormat;
    private NumberFormat currencyFormat;

    public ReservaAdapter(Context context, List<Reserva> reservas) {
        this.context = context;
        this.reservas = reservas;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reserva, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = reservas.get(position);

        holder.textViewLocalNome.setText(reserva.getLocalNome());
        holder.textViewValor.setText(currencyFormat.format(reserva.getValorTotal()));

        if (reserva.getDataInicio() != null) {
            holder.textViewDataInicio.setText("Início: " + dateFormat.format(reserva.getDataInicio()));
        }

        if (reserva.getDataFim() != null) {
            holder.textViewDataFim.setText("Fim: " + dateFormat.format(reserva.getDataFim()));
        }

        holder.textViewPessoas.setText(reserva.getQuantidadePessoas() + " pessoas");

        holder.chipStatus.setText(getStatusText(reserva.getStatus()));
        holder.chipStatus.setChipBackgroundColorResource(getStatusColor(reserva.getStatus()));

        if (reserva.getLocalImageUrl() != null && !reserva.getLocalImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(reserva.getLocalImageUrl())
                    .centerCrop()
                        .placeholder(R.drawable.ic_placeholder)
                    .into(holder.imageViewLocal);
        }
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    public void updateData(List<Reserva> newReservas) {
        this.reservas = newReservas;
        notifyDataSetChanged();
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pendente":
                return "Pendente";
            case "confirmada":
                return "Confirmada";
            case "cancelada":
                return "Cancelada";
            case "concluida":
                return "Concluída";
            default:
                return status;
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "pendente":
                return R.color.primary;
            case "confirmada":
                return R.color.success;
            case "cancelada":
                return R.color.error;
            case "concluida":
                return R.color.text_secondary;
            default:
                return R.color.text_secondary;
        }
    }

    static class ReservaViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView imageViewLocal;
        TextView textViewLocalNome;
        TextView textViewDataInicio;
        TextView textViewDataFim;
        TextView textViewValor;
        TextView textViewPessoas;
        Chip chipStatus;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            imageViewLocal = itemView.findViewById(R.id.imageViewLocal);
            textViewLocalNome = itemView.findViewById(R.id.textViewLocalNome);
            textViewDataInicio = itemView.findViewById(R.id.textViewDataInicio);
            textViewDataFim = itemView.findViewById(R.id.textViewDataFim);
            textViewValor = itemView.findViewById(R.id.textViewValor);
            textViewPessoas = itemView.findViewById(R.id.textViewPessoas);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
}

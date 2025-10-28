package com.example.easyspace;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.adapters.NotificationAdapter;
import com.example.easyspace.models.Notification;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerViewNotifications;
    private TextView textViewEmptyState;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadNotifications();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        textViewEmptyState = findViewById(R.id.textViewEmptyState);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Notificações");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(this, new ArrayList<>());
        recyclerViewNotifications.setAdapter(adapter);
    }

    private void loadNotifications() {
        List<Notification> notifications = getSampleNotifications();

        if (notifications.isEmpty()) {
            textViewEmptyState.setVisibility(View.VISIBLE);
            recyclerViewNotifications.setVisibility(View.GONE);
        } else {
            textViewEmptyState.setVisibility(View.GONE);
            recyclerViewNotifications.setVisibility(View.VISIBLE);
            adapter.updateData(notifications);
        }
    }

    private List<Notification> getSampleNotifications() {
        List<Notification> notifications = new ArrayList<>();

        notifications.add(new Notification(
                "Nova Reserva",
                "Você recebeu uma nova reserva para Escritório Premium Centro",
                "booking",
                System.currentTimeMillis() - 3600000,
                false
        ));

        notifications.add(new Notification(
                "Avaliação Recebida",
                "Seu espaço recebeu uma avaliação de 5 estrelas!",
                "review",
                System.currentTimeMillis() - 7200000,
                false
        ));

        notifications.add(new Notification(
                "Mensagem Nova",
                "João Silva enviou uma mensagem sobre Sala de Reunião Tech Hub",
                "message",
                System.currentTimeMillis() - 86400000,
                true
        ));

        return notifications;
    }
}

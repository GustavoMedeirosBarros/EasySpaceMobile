package com.example.easyspace;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.adapters.NotificationAdapter;
import com.example.easyspace.models.Notification;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerViewNotifications;
    private LinearLayout layoutEmptyState; // Alterado de TextView
    private NotificationAdapter adapter;
    private FirebaseManager firebaseManager;
    private List<Notification> notificationList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications); // Usa o novo layout

        firebaseManager = new FirebaseManager();
        notificationList = new ArrayList<>();

        initViews();
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        layoutEmptyState = findViewById(R.id.layoutEmptyState); // ID do LinearLayout
        progressBar = findViewById(R.id.progressBar); // ID do ProgressBar
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
        adapter = new NotificationAdapter(this, notificationList);
        recyclerViewNotifications.setAdapter(adapter);
    }

    private void loadNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
        recyclerViewNotifications.setVisibility(View.GONE);

        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "Faça login para ver suas notificações", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            checkEmptyState();
            return;
        }

        firebaseManager.getUserNotifications(userId, new FirebaseManager.NotificationsCallback() {
            @Override
            public void onSuccess(List<Notification> notifications) {
                progressBar.setVisibility(View.GONE);
                notificationList.clear();
                notificationList.addAll(notifications);
                adapter.updateData(notificationList); // Atualiza o adapter
                checkEmptyState();
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NotificationsActivity.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
                checkEmptyState();
            }
        });
    }

    private void checkEmptyState() {
        if (notificationList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerViewNotifications.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerViewNotifications.setVisibility(View.VISIBLE);
        }
    }

    // O método getSampleNotifications() não é mais necessário e pode ser removido
}
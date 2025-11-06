package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MessagesActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerViewMessages;
    private LinearLayout layoutEmptyState;
    private TextView textViewEmptyMessage;
    private BottomNavigationView bottomNavigation;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        firebaseManager = new FirebaseManager();
        initViews();
        setupToolbar();
        setupBottomNavigation();
        checkLoginAndLoadMessages();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        textViewEmptyMessage = findViewById(R.id.textViewEmptyMessage);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mensagens");
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_messages);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            } else if (itemId == R.id.nav_messages) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                if (firebaseManager.isLoggedIn()) {
                    startActivity(new Intent(this, ProfileActivity.class));
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                return true;
            }

            return false;
        });
    }

    private void checkLoginAndLoadMessages() {
        if (!firebaseManager.isLoggedIn()) {
            showEmptyState("Faça login para ver suas mensagens");
            return;
        }

        showEmptyState("Nenhuma mensagem ainda");
    }

    private void showEmptyState(String message) {
        layoutEmptyState.setVisibility(View.VISIBLE);
        recyclerViewMessages.setVisibility(View.GONE);
        textViewEmptyMessage.setText(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_messages);
    }
}

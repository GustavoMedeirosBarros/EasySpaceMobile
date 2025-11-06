package com.example.easyspace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.adapters.MeusAnunciosAdapter; // Import o novo adapter
import com.example.easyspace.models.Local;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton; // Import correto do MaterialButton
import java.util.ArrayList;
import java.util.List;

public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MeusAnunciosAdapter adapter; // Usar o novo adapter
    private List<Local> localList;
    private FirebaseManager firebaseManager;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private MaterialButton buttonNovoAnuncio;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios); // Usa o novo layout

        firebaseManager = new FirebaseManager();
        localList = new ArrayList<>();

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnuncios();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewMeusAnuncios);
        progressBar = findViewById(R.id.progressBar);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        buttonNovoAnuncio = findViewById(R.id.buttonNovoAnuncio);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Meus Anúncios");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configura os cliques do adapter
        MeusAnunciosAdapter.OnItemClickListener listener = new MeusAnunciosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Local local) {
                Intent intent = new Intent(MeusAnunciosActivity.this, LocalDetailActivity.class);
                intent.putExtra("local", local); // Passa o objeto local
                startActivity(intent);
            }

            @Override
            public void onEditClick(Local local) {
                // Lógica para editar (precisa da Activity CriarAnuncio)
                Intent intent = new Intent(MeusAnunciosActivity.this, CriarAnuncioActivity.class);
                intent.putExtra("localIdToEdit", local.getId()); // Passa o ID para edição
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Local local) {
                showDeleteConfirmation(local);
            }
        };

        adapter = new MeusAnunciosAdapter(this, localList, listener);
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        buttonNovoAnuncio.setOnClickListener(v ->
                startActivity(new Intent(this, CriarAnuncioActivity.class)));
    }

    private void loadAnuncios() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show();
            checkEmptyState();
            return;
        }

        // Usa o método correto que foi criado no FirebaseManager
        firebaseManager.getLocaisByUserId(userId, new FirebaseManager.LocaisCallback() {
            @Override
            public void onSuccess(List<Local> locais) {
                progressBar.setVisibility(View.GONE);
                localList.clear();
                localList.addAll(locais);
                adapter.updateData(locais); // Atualiza o adapter
                checkEmptyState();
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MeusAnunciosActivity.this, "Erro ao carregar anúncios", Toast.LENGTH_SHORT).show();
                checkEmptyState();
            }
        });
    }

    private void checkEmptyState() {
        if (localList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteConfirmation(Local local) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Anúncio")
                .setMessage("Tem certeza que deseja excluir o anúncio \"" + local.getNome() + "\"? Esta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> deleteLocal(local))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteLocal(Local local) {
        progressBar.setVisibility(View.VISIBLE);
        // Usa o método correto do FirebaseManager
        firebaseManager.deleteLocal(local.getId(), new FirebaseManager.TaskCallback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MeusAnunciosActivity.this, "Anúncio excluído", Toast.LENGTH_SHORT).show();
                loadAnuncios(); // Recarrega a lista
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MeusAnunciosActivity.this, "Erro ao excluir: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
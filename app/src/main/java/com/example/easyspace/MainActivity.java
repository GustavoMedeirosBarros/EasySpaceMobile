package com.example.easyspace;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspace.adapters.CategoriaAdapter;
import com.example.easyspace.adapters.LocalAdapter;
import com.example.easyspace.models.Categoria;
import com.example.easyspace.models.Local;
import com.example.easyspace.utils.FirebaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements CategoriaAdapter.OnCategoriaClickListener {

    private static final String MP_PUBLIC_KEY = "TEST-27e91433-dae1-40d1-9b26-155234ec887f";
    private BottomNavigationView bottomNavigation;
    private FirebaseManager firebaseManager;
    private RecyclerView recyclerViewCategorias, recyclerViewLocais;
    private RecyclerView recyclerViewRecentes, recyclerViewPopulares, recyclerViewMelhoresAvaliacoes;
    private CategoriaAdapter categoriaAdapter;
    private LocalAdapter locaisAdapter, recentesAdapter, popularesAdapter, melhoresAvaliacoesAdapter;
    private List<Categoria> categoriasList;
    private List<Local> locaisList;
    private List<Local> todosLocaisFiltrados;
    private SearchView searchView;
    private MaterialButton buttonFiltros;
    private ProgressBar progressBar;
    private TextView textViewEmptyLocais;

    private TextView textViewVerTodosDestaques, textViewVerTodosRecentes;
    private TextView textViewVerTodosPopulares, textViewVerTodosAvaliados;

    private String currentCategory = null;
    private double currentMinPreco = 0;
    private double currentMaxPreco = 500;
    private int currentMinCapacidade = 0;
    private List<String> currentComodidades = new ArrayList<>();
    private List<Local> allLocais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        firebaseManager = new FirebaseManager();
        initViews();
        setupBottomNavigation();
        setupCategorias();
        setupLocais();
        setupListeners();

        loadLocais();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_home);
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        recyclerViewCategorias = findViewById(R.id.recyclerViewCategorias);
        recyclerViewLocais = findViewById(R.id.recyclerViewLocais);
        recyclerViewRecentes = findViewById(R.id.recyclerViewRecentes);
        recyclerViewPopulares = findViewById(R.id.recyclerViewPopulares);
        recyclerViewMelhoresAvaliacoes = findViewById(R.id.recyclerViewMelhoresAvaliacoes);
        searchView = findViewById(R.id.searchView);
        buttonFiltros = findViewById(R.id.buttonFiltros);
        progressBar = findViewById(R.id.progressBar);
        textViewEmptyLocais = findViewById(R.id.textViewEmptyLocais);

        textViewVerTodosDestaques = findViewById(R.id.textViewVerTodosDestaques);
        textViewVerTodosRecentes = findViewById(R.id.textViewVerTodosRecentes);
        textViewVerTodosPopulares = findViewById(R.id.textViewVerTodosPopulares);
        textViewVerTodosAvaliados = findViewById(R.id.textViewVerTodosAvaliados);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
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

    private void setupCategorias() {
        recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoriasList = new ArrayList<>();
        categoriasList.add(new Categoria("Escritório", R.drawable.ic_office));
        categoriasList.add(new Categoria("Coworking", R.drawable.ic_coworking));
        categoriasList.add(new Categoria("Reunião", R.drawable.ic_meeting_room));
        categoriasList.add(new Categoria("Estúdio", R.drawable.ic_studio));
        categoriasList.add(new Categoria("Auditório", R.drawable.ic_auditorium));
        categoriasList.add(new Categoria("Treinamento", R.drawable.ic_training_room));

        categoriaAdapter = new CategoriaAdapter(this, categoriasList, this);
        recyclerViewCategorias.setAdapter(categoriaAdapter);
    }

    private void setupLocais() {
        locaisList = new ArrayList<>();
        todosLocaisFiltrados = new ArrayList<>();
        allLocais = new ArrayList<>();

        recyclerViewLocais.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        locaisAdapter = new LocalAdapter(this, new ArrayList<>());
        recyclerViewLocais.setAdapter(locaisAdapter);

        recyclerViewRecentes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recentesAdapter = new LocalAdapter(this, new ArrayList<>());
        recyclerViewRecentes.setAdapter(recentesAdapter);

        recyclerViewPopulares.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popularesAdapter = new LocalAdapter(this, new ArrayList<>());
        recyclerViewPopulares.setAdapter(popularesAdapter);

        recyclerViewMelhoresAvaliacoes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        melhoresAvaliacoesAdapter = new LocalAdapter(this, new ArrayList<>());
        recyclerViewMelhoresAvaliacoes.setAdapter(melhoresAvaliacoesAdapter);
    }

    private void setupListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterLocaisList(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterLocaisList(newText);
                return true;
            }
        });

        buttonFiltros.setOnClickListener(v -> showFilterDialog());

        textViewVerTodosDestaques.setOnClickListener(v -> openLocalList("destaques", "Espaços em Destaque"));
        textViewVerTodosRecentes.setOnClickListener(v -> openLocalList("recentes", "Recentemente Adicionados"));
        textViewVerTodosPopulares.setOnClickListener(v -> openLocalList("populares", "Mais Procurados"));
        textViewVerTodosAvaliados.setOnClickListener(v -> openLocalList("avaliados", "Melhores Avaliações"));
    }


    private void openLocalList(String filterType, String filterTitle) {
        Intent intent = new Intent(this, LocalListActivity.class);
        intent.putExtra("FILTER_TYPE", filterType);
        intent.putExtra("FILTER_TITLE", filterTitle);
        startActivity(intent);
    }

    @Override
    public void onCategoriaClick(Categoria categoria) {
        if (currentCategory != null && currentCategory.equals(categoria.getNome())) {
            currentCategory = null;
        } else {
            currentCategory = categoria.getNome();
        }
        loadLocais();
    }

    private void showFilterDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filtros, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setView(dialogView);

        RangeSlider rangeSliderPreco = dialogView.findViewById(R.id.rangeSliderPreco);
        TextInputEditText editTextCapacidadeMin = dialogView.findViewById(R.id.editTextCapacidadeMin);
        CheckBox checkWifi = dialogView.findViewById(R.id.checkWifiFiltro);
        CheckBox checkArCondicionado = dialogView.findViewById(R.id.checkArCondicionadoFiltro);
        CheckBox checkCafe = dialogView.findViewById(R.id.checkCafeFiltro);
        CheckBox checkProjetor = dialogView.findViewById(R.id.checkProjetorFiltro);
        CheckBox checkEstacionamento = dialogView.findViewById(R.id.checkEstacionamentoFiltro);
        CheckBox checkAcessibilidade = dialogView.findViewById(R.id.checkAcessibilidadeFiltro);
        MaterialButton buttonLimparFiltros = dialogView.findViewById(R.id.buttonLimparFiltros);
        MaterialButton buttonAplicarFiltros = dialogView.findViewById(R.id.buttonAplicarFiltros);

        rangeSliderPreco.setValues( (float) currentMinPreco, (float) currentMaxPreco );
        if (currentMinCapacidade > 0) {
            editTextCapacidadeMin.setText(String.valueOf(currentMinCapacidade));
        }
        checkWifi.setChecked(currentComodidades.contains("Wi-Fi"));
        checkArCondicionado.setChecked(currentComodidades.contains("Ar Condicionado"));
        checkCafe.setChecked(currentComodidades.contains("Café"));
        checkProjetor.setChecked(currentComodidades.contains("Projetor"));
        checkEstacionamento.setChecked(currentComodidades.contains("Estacionamento"));
        checkAcessibilidade.setChecked(currentComodidades.contains("Acessibilidade"));

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        buttonLimparFiltros.setOnClickListener(v -> {
            currentCategory = null;
            currentMinPreco = 0;
            currentMaxPreco = 500;
            currentMinCapacidade = 0;
            currentComodidades.clear();
            categoriaAdapter.clearSelection();
            loadLocais();
            dialog.dismiss();
        });

        buttonAplicarFiltros.setOnClickListener(v -> {
            currentMinPreco = rangeSliderPreco.getValues().get(0);
            currentMaxPreco = rangeSliderPreco.getValues().get(1);

            try {
                String capStr = editTextCapacidadeMin.getText().toString();
                currentMinCapacidade = capStr.isEmpty() ? 0 : Integer.parseInt(capStr);
            } catch (NumberFormatException e) {
                currentMinCapacidade = 0;
            }

            currentComodidades.clear();
            if (checkWifi.isChecked()) currentComodidades.add("Wi-Fi");
            if (checkArCondicionado.isChecked()) currentComodidades.add("Ar Condicionado");
            if (checkCafe.isChecked()) currentComodidades.add("Café");
            if (checkProjetor.isChecked()) currentComodidades.add("Projetor");
            if (checkEstacionamento.isChecked()) currentComodidades.add("Estacionamento");
            if (checkAcessibilidade.isChecked()) currentComodidades.add("Acessibilidade");

            loadLocais();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void loadLocais() {
        progressBar.setVisibility(View.VISIBLE);
        textViewEmptyLocais.setVisibility(View.GONE);
        recyclerViewLocais.setVisibility(View.GONE);

        firebaseManager.getLocaisByFilter(currentCategory, currentMinPreco, currentMaxPreco, new FirebaseManager.LocaisCallback() {
            @Override
            public void onSuccess(List<Local> locais) {
                progressBar.setVisibility(View.GONE);

                allLocais = new ArrayList<>(locais);

                List<Local> locaisFiltradosCliente = applyClientSideFilters(allLocais);

                locaisAdapter.updateData(locaisFiltradosCliente);

                updateSortedLists(locaisFiltradosCliente);

                checkEmptyState(locaisFiltradosCliente);
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Erro ao carregar locais: " + error, Toast.LENGTH_SHORT).show();
                checkEmptyState(new ArrayList<>());
            }
        });
    }

    private void updateSortedLists(List<Local> baseList) {
        if (baseList == null || baseList.isEmpty()) {
            recentesAdapter.updateData(new ArrayList<>());
            popularesAdapter.updateData(new ArrayList<>());
            melhoresAvaliacoesAdapter.updateData(new ArrayList<>());
            return;
        }

        List<Local> recentes = new ArrayList<>(baseList);
        Collections.sort(recentes, (l1, l2) -> Long.compare(l2.getTimestamp(), l1.getTimestamp()));
        recentesAdapter.updateData(recentes);

        List<Local> populares = new ArrayList<>(baseList);
        Collections.sort(populares, (l1, l2) -> Integer.compare(l2.getViewCount(), l1.getViewCount()));
        popularesAdapter.updateData(populares);

        List<Local> melhoresAvaliacoes = new ArrayList<>(baseList);
        Collections.sort(melhoresAvaliacoes, (l1, l2) -> Double.compare(l2.getRating(), l1.getRating()));
        melhoresAvaliacoesAdapter.updateData(melhoresAvaliacoes);
    }

    private List<Local> applyClientSideFilters(List<Local> locais) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return locais.stream()
                    .filter(local -> local.getCapacidade() >= currentMinCapacidade)
                    .filter(local -> local.getComodidades() != null && local.getComodidades().containsAll(currentComodidades))
                    .collect(Collectors.toList());
        } else {
            List<Local> filteredList = new ArrayList<>();
            for (Local local : locais) {
                boolean capacityMatch = local.getCapacidade() >= currentMinCapacidade;
                boolean amenitiesMatch = local.getComodidades() != null && local.getComodidades().containsAll(currentComodidades);

                if (capacityMatch && amenitiesMatch) {
                    filteredList.add(local);
                }
            }
            return filteredList;
        }
    }

    private void filterLocaisList(String query) {
        List<Local> listaFiltrada = new ArrayList<>();
        String queryLower = query.toLowerCase();

        List<Local> baseList = applyClientSideFilters(allLocais);

        for (Local local : baseList) {
            if (local.getNome().toLowerCase().contains(queryLower) ||
                    local.getEndereco().toLowerCase().contains(queryLower)) {
                listaFiltrada.add(local);
            }
        }

        locaisAdapter.updateData(listaFiltrada);
        updateSortedLists(listaFiltrada);
        checkEmptyState(listaFiltrada);
    }

    private void checkEmptyState(List<Local> lista) {
        if (lista.isEmpty()) {
            textViewEmptyLocais.setVisibility(View.VISIBLE);
            recyclerViewLocais.setVisibility(View.GONE);
        } else {
            textViewEmptyLocais.setVisibility(View.GONE);
            recyclerViewLocais.setVisibility(View.VISIBLE);
        }
    }
}
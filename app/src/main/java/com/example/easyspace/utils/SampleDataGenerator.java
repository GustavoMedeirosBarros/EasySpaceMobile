package com.example.easyspace.utils;

import com.example.easyspace.models.Local;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class SampleDataGenerator {

    private static final String[] OFFICE_NAMES = {
            "Escritório Premium Centro",
            "Sala Executiva Vista Mar",
            "Espaço Corporativo Moderno",
            "Office Space Paulista"
    };

    private static final String[] MEETING_ROOM_NAMES = {
            "Sala de Reunião Tech Hub",
            "Meeting Room Innovation",
            "Sala Conferência Premium",
            "Espaço Reunião Criativa"
    };

    private static final String[] COWORKING_NAMES = {
            "Coworking Space Downtown",
            "Hub Colaborativo",
            "Espaço Compartilhado Premium",
            "Cowork Innovation Center"
    };

    private static final String[] AUDITORIUM_NAMES = {
            "Auditório Corporate Center",
            "Espaço Eventos Premium",
            "Auditório Tech Conference"
    };

    private static final String[] ADDRESSES = {
            "Av. Paulista, 1000 - São Paulo, SP",
            "Rua Augusta, 500 - São Paulo, SP",
            "Av. Faria Lima, 2000 - São Paulo, SP",
            "Rua Oscar Freire, 300 - São Paulo, SP",
            "Av. Brigadeiro Faria Lima, 1500 - São Paulo, SP",
            "Rua dos Pinheiros, 800 - São Paulo, SP",
            "Av. Rebouças, 1200 - São Paulo, SP",
            "Rua Haddock Lobo, 600 - São Paulo, SP"
    };

    private static final String[] DESCRIPTIONS = {
            "Espaço moderno e bem equipado, ideal para reuniões e trabalho colaborativo. Ambiente climatizado com internet de alta velocidade.",
            "Local amplo e confortável, perfeito para eventos corporativos e apresentações. Infraestrutura completa.",
            "Ambiente profissional com design contemporâneo. Localização privilegiada e fácil acesso.",
            "Espaço versátil e funcional, equipado com tecnologia de ponta. Ideal para produtividade."
    };

    private static final String[] IMAGE_URLS = {
            "https://images.unsplash.com/photo-1497366216548-37526070297c?w=800",
            "https://images.unsplash.com/photo-1497366811353-6870744d04b2?w=800",
            "https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800",
            "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=800",
            "https://images.unsplash.com/photo-1431540015161-0bf868a2d407?w=800",
            "https://images.unsplash.com/photo-1542744173-8e7e53415bb0?w=800",
            "https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=800",
            "https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=800"
    };

    private static final List<String> AMENITIES = Arrays.asList(
            "Wi-Fi", "Ar Condicionado", "Café", "Projetor", "Estacionamento", "Acessibilidade"
    );

    public static void generateAndSaveSampleData(GenerateCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            callback.onError("Usuário não autenticado");
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        List<Local> sampleLocations = generateSampleLocations(userId);

        saveSampleLocations(sampleLocations, callback);
    }

    private static List<Local> generateSampleLocations(String proprietarioId) {
        List<Local> locations = new ArrayList<>();
        Random random = new Random();
        int imageIndex = 0;

        for (String name : OFFICE_NAMES) {
            Local local = createLocal(
                    name,
                    "Escritório",
                    proprietarioId,
                    random,
                    IMAGE_URLS[imageIndex % IMAGE_URLS.length]
            );
            locations.add(local);
            imageIndex++;
        }

        for (String name : MEETING_ROOM_NAMES) {
            Local local = createLocal(
                    name,
                    "Sala de Reunião",
                    proprietarioId,
                    random,
                    IMAGE_URLS[imageIndex % IMAGE_URLS.length]
            );
            local.setCapacidade(8 + random.nextInt(12));
            locations.add(local);
            imageIndex++;
        }

        for (String name : COWORKING_NAMES) {
            Local local = createLocal(
                    name,
                    "Coworking",
                    proprietarioId,
                    random,
                    IMAGE_URLS[imageIndex % IMAGE_URLS.length]
            );
            local.setCapacidade(20 + random.nextInt(30));
            locations.add(local);
            imageIndex++;
        }

        for (String name : AUDITORIUM_NAMES) {
            Local local = createLocal(
                    name,
                    "Auditório",
                    proprietarioId,
                    random,
                    IMAGE_URLS[imageIndex % IMAGE_URLS.length]
            );
            local.setCapacidade(50 + random.nextInt(100));
            local.setPreco(200 + random.nextInt(300));
            locations.add(local);
            imageIndex++;
        }

        return locations;
    }

    private static Local createLocal(String name, String categoria, String proprietarioId,
                                     Random random, String imageUrl) {
        Local local = new Local();
        local.setNome(name);
        local.setCategoria(categoria);
        local.setProprietarioId(proprietarioId);
        local.setEndereco(ADDRESSES[random.nextInt(ADDRESSES.length)]);
        local.setDescricao(DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)]);
        local.setImageUrl(imageUrl);
        local.setPreco(50 + random.nextInt(150));
        local.setRating(3.5 + random.nextDouble() * 1.5);
        local.setCapacidade(4 + random.nextInt(16));
        local.setHorarioFuncionamento("08:00 - 18:00");
        local.setTipoLocacao("hora");
        local.setViewCount(random.nextInt(100));
        local.setTimestamp(System.currentTimeMillis() - random.nextInt(30) * 24 * 60 * 60 * 1000L);

        List<String> selectedAmenities = new ArrayList<>();
        int amenityCount = 3 + random.nextInt(4);
        List<String> availableAmenities = new ArrayList<>(AMENITIES);
        for (int i = 0; i < amenityCount && !availableAmenities.isEmpty(); i++) {
            int index = random.nextInt(availableAmenities.size());
            selectedAmenities.add(availableAmenities.remove(index));
        }
        local.setComodidades(selectedAmenities);

        return local;
    }

    private static void saveSampleLocations(List<Local> locations, GenerateCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final int[] savedCount = {0};
        final int totalCount = locations.size();

        for (Local local : locations) {
            db.collection("locais")
                    .add(local)
                    .addOnSuccessListener(documentReference -> {
                        documentReference.update("id", documentReference.getId());
                        savedCount[0]++;
                        if (savedCount[0] == totalCount) {
                            callback.onSuccess(totalCount);
                        }
                    })
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        }
    }

    public interface GenerateCallback {
        void onSuccess(int count);
        void onError(String message);
    }
}

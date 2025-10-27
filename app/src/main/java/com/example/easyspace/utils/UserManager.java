package com.example.easyspace.utils;

import com.example.easyspace.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserManager {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public UserManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void registerUser(Usuario usuario, String senha, Runnable onSuccess, Runnable onError) {
        auth.createUserWithEmailAndPassword(usuario.getEmail(), senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = auth.getCurrentUser().getUid();
                        usuario.setId(uid);
                        db.collection("users").document(uid)
                                .set(usuario)
                                .addOnSuccessListener(aVoid -> onSuccess.run())
                                .addOnFailureListener(e -> onError.run());
                    } else {
                        onError.run();
                    }
                });
    }

    public void login(String email, String senha, Runnable onSuccess, Runnable onError) {
        auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) onSuccess.run();
                    else onError.run();
                });
    }

    public void logout() {
        auth.signOut();
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public String getUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }
}

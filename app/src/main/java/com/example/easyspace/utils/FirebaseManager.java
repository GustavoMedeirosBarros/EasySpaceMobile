package com.example.easyspace.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.easyspace.R;
import com.example.easyspace.models.Local;
import com.example.easyspace.models.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;

    public FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public interface AuthCallback {
        void onSuccess(String userId);
        void onFailure(String error);
    }

    public interface LocalCallback {
        void onSuccess();
        void onError(String mensagem);
    }

    public interface UserCallback {
        void onSuccess(Usuario usuario);
        void onFailure(String error);
    }

    public interface UserDataCallback {
        void onSuccess(Map<String, Object> userData);
        void onFailure(String error);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface ProfileCompleteCallback {
        void onResult(boolean isComplete);
    }

    public interface FavoritesCallback {
        void onSuccess(List<Local> favorites);
        void onFailure(String error);
    }

    public interface FavoriteStatusCallback {
        void onResult(boolean isFavorite);
    }

    public void configureGoogleSignIn(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public void signInWithGoogle(Task<GoogleSignInAccount> task, AuthCallback callback) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(authTask -> {
                            if (authTask.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    checkAndCreateUserProfile(user, callback);
                                }
                            } else {
                                callback.onFailure(authTask.getException() != null ?
                                        authTask.getException().getMessage() : "Erro ao autenticar com Google");
                            }
                        });
            }
        } catch (ApiException e) {
            callback.onFailure("Erro ao processar login do Google: " + e.getMessage());
        }
    }

    private void checkAndCreateUserProfile(FirebaseUser firebaseUser, AuthCallback callback) {
        String userId = firebaseUser.getUid();
        db.collection("usuarios").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (!document.exists()) {
                            Usuario usuario = new Usuario();
                            usuario.setId(userId);
                            usuario.setNome(firebaseUser.getDisplayName());
                            usuario.setEmail(firebaseUser.getEmail());
                            usuario.setFotoUrl(firebaseUser.getPhotoUrl() != null ?
                                    firebaseUser.getPhotoUrl().toString() : "");
                            usuario.setPessoaFisica(true);

                            db.collection("usuarios").document(userId)
                                    .set(usuario)
                                    .addOnSuccessListener(aVoid -> callback.onSuccess(userId))
                                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                        } else {
                            callback.onSuccess(userId);
                        }
                    } else {
                        callback.onFailure(task.getException() != null ?
                                task.getException().getMessage() : "Erro ao verificar perfil");
                    }
                });
    }

    public void registerUser(String nome, String email, String senha, String telefone,
                             String documento, String cep, String cidade, String estado,
                             boolean isPessoaFisica, String endereco, String complemento,
                             String bairro, String dataNascimento, String genero,
                             AuthCallback callback) {

        auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            Usuario usuario = new Usuario();
                            usuario.setId(userId);
                            usuario.setNome(nome);
                            usuario.setEmail(email);
                            usuario.setTelefone(telefone);
                            usuario.setDocumento(documento);
                            usuario.setCep(cep);
                            usuario.setCidade(cidade);
                            usuario.setEstado(estado);
                            usuario.setPessoaFisica(isPessoaFisica);
                            usuario.setEndereco(endereco);
                            usuario.setComplemento(complemento);
                            usuario.setBairro(bairro);
                            usuario.setDataNascimento(dataNascimento);
                            usuario.setGenero(genero);
                            usuario.setFotoUrl("");

                            db.collection("usuarios").document(userId)
                                    .set(usuario)
                                    .addOnSuccessListener(aVoid -> callback.onSuccess(userId))
                                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                        }
                    } else {
                        callback.onFailure(task.getException() != null ?
                                task.getException().getMessage() : "Erro ao registrar usuário");
                    }
                });
    }

    public void loginUser(String email, String senha, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            callback.onSuccess(user.getUid());
                        } else {
                            callback.onFailure("Erro ao obter dados do usuário");
                        }
                    } else {
                        callback.onFailure(task.getException() != null ?
                                task.getException().getMessage() : "Erro ao fazer login");
                    }
                });
    }

    public void getUserData(String userId, UserCallback callback) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Usuario usuario = document.toObject(Usuario.class);
                            callback.onSuccess(usuario);
                        } else {
                            callback.onFailure("Usuário não encontrado");
                        }
                    } else {
                        callback.onFailure(task.getException() != null ?
                                task.getException().getMessage() : "Erro ao obter dados");
                    }
                });
    }

    public void getCurrentUserData(UserDataCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("Usuário não autenticado");
            return;
        }

        db.collection("usuarios").document(currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> userData = document.getData();
                            callback.onSuccess(userData);
                        } else {
                            callback.onFailure("Dados do usuário não encontrados");
                        }
                    } else {
                        callback.onFailure(task.getException() != null ?
                                task.getException().getMessage() : "Erro ao obter dados");
                    }
                });
    }

    public void salvarLocal(Local local, LocalCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("Usuário não autenticado");
            return;
        }

        local.setProprietarioId(user.getUid());

        db.collection("locais")
                .add(local)
                .addOnSuccessListener(documentReference -> {
                    local.setId(documentReference.getId());
                    documentReference.update("id", documentReference.getId())
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateUserProfile(String userId, Usuario usuario, AuthCallback callback) {
        db.collection("usuarios").document(userId)
                .set(usuario)
                .addOnSuccessListener(aVoid -> callback.onSuccess(userId))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateUserData(String userId, Map<String, Object> data, UpdateCallback callback) {
        db.collection("usuarios").document(userId)
                .update(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void isProfileComplete(String userId, ProfileCompleteCallback callback) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Boolean profileComplete = document.getBoolean("profileComplete");
                            callback.onResult(profileComplete != null && profileComplete);
                        } else {
                            callback.onResult(false);
                        }
                    } else {
                        callback.onResult(false);
                    }
                });
    }

    public void addToFavorites(String localId, UpdateCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuário não autenticado");
            return;
        }

        String userId = user.getUid();
        db.collection("usuarios").document(userId)
                .update("favoritos", FieldValue.arrayUnion(localId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void removeFromFavorites(String localId, UpdateCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuário não autenticado");
            return;
        }

        String userId = user.getUid();
        db.collection("usuarios").document(userId)
                .update("favoritos", FieldValue.arrayRemove(localId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void isFavorite(String localId, FavoriteStatusCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onResult(false);
            return;
        }

        String userId = user.getUid();
        db.collection("usuarios").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<String> favoritos = (List<String>) document.get("favoritos");
                            callback.onResult(favoritos != null && favoritos.contains(localId));
                        } else {
                            callback.onResult(false);
                        }
                    } else {
                        callback.onResult(false);
                    }
                });
    }

    public void getUserFavorites(FavoritesCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuário não autenticado");
            return;
        }

        String userId = user.getUid();
        db.collection("usuarios").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<String> favoritoIds = (List<String>) document.get("favoritos");

                            if (favoritoIds == null || favoritoIds.isEmpty()) {
                                callback.onSuccess(new ArrayList<>());
                                return;
                            }

                            db.collection("locais")
                                    .whereIn("id", favoritoIds)
                                    .get()
                                    .addOnCompleteListener(locaisTask -> {
                                        if (locaisTask.isSuccessful()) {
                                            List<Local> favoriteLocais = new ArrayList<>();
                                            for (DocumentSnapshot localDoc : locaisTask.getResult()) {
                                                Local local = localDoc.toObject(Local.class);
                                                if (local != null) {
                                                    favoriteLocais.add(local);
                                                }
                                            }
                                            callback.onSuccess(favoriteLocais);
                                        } else {
                                            callback.onFailure("Erro ao carregar favoritos");
                                        }
                                    });
                        } else {
                            callback.onSuccess(new ArrayList<>());
                        }
                    } else {
                        callback.onFailure(task.getException() != null ?
                                task.getException().getMessage() : "Erro ao obter favoritos");
                    }
                });
    }


    public void logout() {
        auth.signOut();
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
        }
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
}

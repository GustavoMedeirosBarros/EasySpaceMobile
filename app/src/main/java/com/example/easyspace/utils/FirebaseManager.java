package com.example.easyspace.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.easyspace.CompleteProfileActivity;
import com.example.easyspace.MainActivity;
import com.example.easyspace.R;
import com.example.easyspace.models.Local;
import com.example.easyspace.models.Notification;
import com.example.easyspace.models.Reserva;
import com.example.easyspace.models.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseManager {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;
    private static final String USERS_COLLECTION = "usuarios";
    private static final String LOCAIS_COLLECTION = "locais";
    private static final String NOTIFICATIONS_COLLECTION = "notifications";
    private static final String RESERVAS_COLLECTION = "reservas";

    public interface AuthCallback { void onSuccess(String userId); void onFailure(String error); }
    public interface LocalCallback { void onSuccess(Local local); void onFailure(String error); }
    public interface LocaisCallback { void onSuccess(List<Local> locais); void onFailure(String error); }
    public interface UserCallback { void onSuccess(Usuario usuario); void onFailure(String error); }
    public interface UserDataCallback { void onSuccess(Map<String, Object> userData); void onFailure(String error); }
    public interface UpdateCallback { void onSuccess(); void onFailure(String error); }
    public interface TaskCallback { void onSuccess(); void onFailure(String error); }
    public interface ProfileCompleteCallback { void onResult(boolean isComplete); }
    public interface FavoritesCallback { void onSuccess(List<Local> favorites); void onFailure(String error); }
    public interface FavoriteStatusCallback { void onResult(boolean isFavorite); }
    public interface CountCallback { void onSuccess(int count); void onFailure(String error); }
    public interface NotificationsCallback { void onSuccess(List<Notification> notifications); void onFailure(String error); }
    public interface ReservasCallback { void onSuccess(List<Reserva> reservas); void onFailure(String error); }
    public interface PreferenciaCallback { void onSuccess(String preferenceId); void onFailure(String error); }


    public FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void configureGoogleSignIn(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }
    public GoogleSignInClient getGoogleSignInClient() { return googleSignInClient; }

    public void signInWithGoogle(Task<GoogleSignInAccount> task, AuthCallback callback) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(authTask -> {
                            if (authTask.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) checkAndCreateUserProfile(user, callback);
                            } else {
                                callback.onFailure(authTask.getException() != null ? authTask.getException().getMessage() : "Erro ao autenticar com Google");
                            }
                        });
            } else {
                callback.onFailure("Conta Google não encontrada");
            }
        } catch (ApiException e) {
            callback.onFailure("Erro ao processar login do Google: " + e.getMessage());
        }
    }

    private void checkAndCreateUserProfile(FirebaseUser firebaseUser, AuthCallback callback) {
        String userId = firebaseUser.getUid();
        db.collection(USERS_COLLECTION).document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(userId);
                    usuario.setNome(firebaseUser.getDisplayName());
                    usuario.setEmail(firebaseUser.getEmail());
                    usuario.setFotoUrl(firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : "");
                    usuario.setPessoaFisica(true);
                    usuario.setProfileComplete(false);
                    db.collection(USERS_COLLECTION).document(userId).set(usuario)
                            .addOnSuccessListener(aVoid -> callback.onSuccess(userId))
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                } else {
                    callback.onSuccess(userId);
                }
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Erro ao verificar perfil");
            }
        });
    }

    public void registerUser(String nome, String email, String senha, String telefone,
                             String documento, String cep, String cidade, String estado,
                             boolean isPessoaFisica, String endereco, String complemento,
                             String bairro, String dataNascimento, String genero,
                             AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nome)
                            .build();
                    firebaseUser.updateProfile(profileUpdates);

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
                    usuario.setProfileComplete(true);


                    db.collection(USERS_COLLECTION).document(userId).set(usuario)
                            .addOnSuccessListener(aVoid -> callback.onSuccess(userId))
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                }
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Erro ao registrar usuário");
            }
        });
    }

    public void loginUser(String email, String senha, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) callback.onSuccess(user.getUid());
                else callback.onFailure("Erro ao obter dados do usuário");
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Erro ao fazer login");
            }
        });
    }

    public void getUserData(String userId, UserCallback callback) {
        db.collection(USERS_COLLECTION).document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Usuario usuario = document.toObject(Usuario.class);
                    if (usuario != null) {
                        callback.onSuccess(usuario);
                    } else {
                        callback.onFailure("Erro ao mapear dados do usuário");
                    }
                }
                else callback.onFailure("Usuário não encontrado");
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Erro ao obter dados");
            }
        });
    }

    public void updateUserData(String userId, Map<String, Object> data, UpdateCallback callback) {
        db.collection(USERS_COLLECTION).document(userId).update(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateUserProfile(String userId, Usuario usuario, AuthCallback callback) {
        if (userId == null || usuario == null) {
            if (callback != null) callback.onFailure("Dados inválidos");
            return;
        }

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null && !Objects.equals(firebaseUser.getDisplayName(), usuario.getNome())) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(usuario.getNome())
                    .build();
            firebaseUser.updateProfile(profileUpdates);
        }

        db.collection(USERS_COLLECTION).document(userId)
                .set(usuario)
                .addOnSuccessListener(aVoid -> callback.onSuccess(userId))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void isProfileComplete(String userId, ProfileCompleteCallback callback) {
        db.collection(USERS_COLLECTION).document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Boolean profileComplete = document.getBoolean("profileComplete");
                    callback.onResult(profileComplete != null && profileComplete);
                } else callback.onResult(false);
            } else callback.onResult(false);
        });
    }

    public void addToFavorites(String localId, UpdateCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuário não autenticado");
            return;
        }
        String userId = user.getUid();
        db.collection(USERS_COLLECTION).document(userId).update("favoritos", FieldValue.arrayUnion(localId))
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
        db.collection(USERS_COLLECTION).document(userId).update("favoritos", FieldValue.arrayRemove(localId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void isFavorite(String localId, FavoriteStatusCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || localId == null) {
            callback.onResult(false);
            return;
        }
        String userId = user.getUid();
        db.collection(USERS_COLLECTION).document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> favoritos = (List<String>) document.get("favoritos");
                    callback.onResult(favoritos != null && favoritos.contains(localId));
                } else callback.onResult(false);
            } else callback.onResult(false);
        });
    }

    public void getUserFavorites(FavoritesCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuário não autenticado");
            return;
        }
        String userId = user.getUid();
        db.collection(USERS_COLLECTION).document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> favoritoIds = (List<String>) document.get("favoritos");
                    if (favoritoIds == null || favoritoIds.isEmpty()) {
                        callback.onSuccess(new ArrayList<>());
                        return;
                    }

                    if (favoritoIds.size() > 30) {
                        List<String> subList = favoritoIds.subList(0, 30);
                        db.collection(LOCAIS_COLLECTION).whereIn("id", subList).get().addOnCompleteListener(locaisTask -> {
                        });
                    }

                    db.collection(LOCAIS_COLLECTION).whereIn("id", favoritoIds).get().addOnCompleteListener(locaisTask -> {
                        if (locaisTask.isSuccessful()) {
                            List<Local> favoriteLocais = new ArrayList<>();
                            for (DocumentSnapshot localDoc : locaisTask.getResult()) {
                                Local local = localDoc.toObject(Local.class);
                                if (local != null) favoriteLocais.add(local);
                            }
                            callback.onSuccess(favoriteLocais);
                        } else callback.onFailure("Erro ao carregar favoritos");
                    });
                } else callback.onSuccess(new ArrayList<>());
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Erro ao obter favoritos");
            }
        });
    }

    public void salvarLocal(Local local, TaskCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("Usuário não autenticado");
            return;
        }
        local.setProprietarioId(user.getUid());

        DocumentReference localRef = db.collection(LOCAIS_COLLECTION).document();
        local.setId(localRef.getId());

        localRef.set(local).addOnSuccessListener(aVoid -> {
            callback.onSuccess();
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getLocalById(String localId, LocalCallback callback) {
        db.collection(LOCAIS_COLLECTION).document(localId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) callback.onSuccess(documentSnapshot.toObject(Local.class));
            else callback.onFailure("Local não encontrado");
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getLocaisByUserId(String userId, LocaisCallback callback) {
        db.collection(LOCAIS_COLLECTION).whereEqualTo("proprietarioId", userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Local> locais = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                locais.add(document.toObject(Local.class));
            }
            callback.onSuccess(locais);
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void deleteLocal(String localId, TaskCallback callback) {
        db.collection(LOCAIS_COLLECTION).document(localId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getLocaisByFilter(String categoria, double minPreco, double maxPreco, LocaisCallback callback) {
        Query query = db.collection(LOCAIS_COLLECTION);

        if (categoria != null && !categoria.isEmpty()) {
            query = query.whereEqualTo("categoria", categoria);
        }

        if (minPreco > 0) {
            query = query.orderBy("preco").whereGreaterThanOrEqualTo("preco", minPreco);
        }
        if (maxPreco < 500) {
            if (minPreco <= 0) {
                query = query.orderBy("preco");
            }
            query = query.whereLessThanOrEqualTo("preco", maxPreco);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Local> locais = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                locais.add(document.toObject(Local.class));
            }
            callback.onSuccess(locais);
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getLocaisOrdenados(String sortByField, LocaisCallback callback) {
        Query.Direction direction = Query.Direction.DESCENDING;

        db.collection(LOCAIS_COLLECTION)
                .orderBy(sortByField, direction)
                .limit(50)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Local> locais = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        locais.add(document.toObject(Local.class));
                    }
                    callback.onSuccess(locais);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    public void salvarReserva(Reserva reserva, TaskCallback callback) {
        if (!isLoggedIn()) {
            if (callback != null) callback.onFailure("Usuário não logado");
            return;
        }

        String reservaId = db.collection(RESERVAS_COLLECTION).document().getId();
        reserva.setId(reservaId);

        db.collection(RESERVAS_COLLECTION).document(reservaId)
                .set(reserva)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e.getMessage());
                });
    }

    public void updateReservaStatus(String reservaId, String status, TaskCallback callback) {
        if (reservaId == null) {
            if (callback != null) callback.onFailure("ID da reserva nulo");
            return;
        }
        db.collection(RESERVAS_COLLECTION).document(reservaId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e.getMessage());
                });
    }

    public void deleteReserva(String reservaId, TaskCallback callback) {
        if (reservaId == null) {
            if (callback != null) callback.onFailure("ID da reserva nulo");
            return;
        }
        db.collection(RESERVAS_COLLECTION).document(reservaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e.getMessage());
                });
    }

    public void getUserReservas(ReservasCallback callback) {
        if (!isLoggedIn()) {
            if (callback != null) callback.onFailure("Usuário não logado");
            return;
        }
        String userId = getCurrentUserId();

        db.collection(RESERVAS_COLLECTION)
                .whereEqualTo("usuarioId", userId)
                .orderBy("dataInicio", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Reserva> reservas = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Reserva reserva = document.toObject(Reserva.class);
                        if (reserva != null) {
                            reservas.add(reserva);
                        }
                    }
                    if (callback != null) callback.onSuccess(reservas);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e.getMessage());
                });
    }

    public void getUserListingCount(String userId, CountCallback callback) {
        db.collection(LOCAIS_COLLECTION).whereEqualTo("proprietarioId", userId).get()
                .addOnSuccessListener(q -> callback.onSuccess(q.size()))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
    public void getUserReservationCount(String userId, CountCallback callback) {
        db.collection(RESERVAS_COLLECTION).whereEqualTo("usuarioId", userId).get()
                .addOnSuccessListener(q -> callback.onSuccess(q.size()))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateFcmToken(String token) {
        if (isLoggedIn()) {
            String userId = getCurrentUserId();
            if (userId == null) return;
            db.collection(USERS_COLLECTION).document(userId).update("fcmToken", token)
                    .addOnSuccessListener(aVoid -> Log.d("FirebaseManager", "FCM Token updated"))
                    .addOnFailureListener(e -> Log.e("FirebaseManager", "Error updating FCM Token", e));
        }
    }

    public void getUserNotifications(String userId, NotificationsCallback callback) {
        db.collection(USERS_COLLECTION).document(userId).collection(NOTIFICATIONS_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(q -> {
                    List<Notification> notifications = new ArrayList<>();
                    for (DocumentSnapshot doc : q.getDocuments()) {
                        Notification n = doc.toObject(Notification.class);
                        if (n != null) {
                            n.setId(doc.getId());
                            notifications.add(n);
                        }
                    }
                    callback.onSuccess(notifications);
                }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void sendInAppNotification(String userId, String title, String message, TaskCallback callback) {
        if (userId == null) {
            if (callback != null) callback.onFailure("ID de usuário nulo");
            return;
        }

        String id = db.collection(USERS_COLLECTION).document(userId).collection(NOTIFICATIONS_COLLECTION).document().getId();

        Notification n = new Notification(title, message, "booking", System.currentTimeMillis(), false);
        n.setId(id);

        db.collection(USERS_COLLECTION).document(userId).collection(NOTIFICATIONS_COLLECTION).document(id).set(n)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e.getMessage());
                });
    }

    public void logout() {
        auth.signOut();
        if (googleSignInClient != null) googleSignInClient.signOut();
    }

    public boolean isLoggedIn() { return auth.getCurrentUser() != null; }

    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public FirebaseUser getCurrentUser() { return auth.getCurrentUser(); }

    public void atualizarStatusReserva(String reservaId, String status, TaskCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("dataAtualizacao", FieldValue.serverTimestamp());

        db.collection("reservas")
                .document(reservaId)
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
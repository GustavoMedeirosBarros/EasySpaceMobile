package com.example.easyspace.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class UserManager {
    private static final String PREF_NAME = "EasySpacePrefs";
    private static final String KEY_USERS = "users";
    private static final String KEY_CURRENT_USER = "current_user";

    private SharedPreferences sharedPreferences;
    private Context context;

    public UserManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean registerUser(String nome, String email, String senha, String telefone,
                                String documento, String cep, String cidade, String estado, boolean isPessoaFisica) {
        try {
            if (emailExists(email)) {
                return false;
            }

            JSONObject user = new JSONObject();
            user.put("nome", nome);
            user.put("email", email);
            user.put("senha", senha);
            user.put("telefone", telefone);
            user.put("documento", documento);
            user.put("cep", cep);
            user.put("cidade", cidade);
            user.put("estado", estado);
            user.put("isPessoaFisica", isPessoaFisica);

            JSONArray users = getUsersArray();
            users.put(user);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USERS, users.toString());
            editor.putString(KEY_CURRENT_USER, email);
            editor.apply();

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginUser(String email, String senha) {
        try {
            JSONArray users = getUsersArray();

            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                if (user.getString("email").equals(email) &&
                        user.getString("senha").equals(senha)) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_CURRENT_USER, email);
                    editor.apply();

                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_CURRENT_USER);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_CURRENT_USER);
    }

    public JSONObject getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }

        String currentUserEmail = sharedPreferences.getString(KEY_CURRENT_USER, "");

        try {
            JSONArray users = getUsersArray();
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                if (user.getString("email").equals(currentUserEmail)) {
                    return user;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean emailExists(String email) {
        try {
            JSONArray users = getUsersArray();
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                if (user.getString("email").equals(email)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private JSONArray getUsersArray() {
        String usersString = sharedPreferences.getString(KEY_USERS, "[]");
        try {
            return new JSONArray(usersString);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }
}

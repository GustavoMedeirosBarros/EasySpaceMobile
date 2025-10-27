package com.example.easyspace.utils;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ValidationUtils {

    public interface CepCallback {
        void onSuccess(String endereco, String bairro, String cidade, String estado);
        void onError(String message);
    }

    public static class PasswordStrength {
        public boolean isValid;
        public String message;

        public PasswordStrength(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
    }

    public static PasswordStrength validatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return new PasswordStrength(false, "Senha é obrigatória");
        }

        if (password.length() < 8) {
            return new PasswordStrength(false, "Senha deve ter pelo menos 8 caracteres");
        }

        if (password.length() > 128) {
            return new PasswordStrength(false, "Senha muito longa (máximo 128 caracteres)");
        }

        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        if (!hasUppercase) {
            return new PasswordStrength(false, "Senha deve conter pelo menos uma letra maiúscula");
        }

        if (!hasLowercase) {
            return new PasswordStrength(false, "Senha deve conter pelo menos uma letra minúscula");
        }

        if (!hasDigit) {
            return new PasswordStrength(false, "Senha deve conter pelo menos um número");
        }

        if (!hasSpecial) {
            return new PasswordStrength(false, "Senha deve conter pelo menos um caractere especial");
        }

        return new PasswordStrength(true, "Senha forte");
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        name = name.trim();

        if (name.length() < 2 || name.length() > 100) {
            return false;
        }

        Pattern namePattern = Pattern.compile("^[a-zA-ZÀ-ÿ\\s'-]+$");
        return namePattern.matcher(name).matches();
    }

    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }

        input = input.trim();

        input = input.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("/", "&#x2F;");

        return input;
    }

    public static boolean isValidAddressNumber(String number) {
        if (number == null || number.trim().isEmpty()) {
            return false;
        }

        number = number.trim();

        Pattern numberPattern = Pattern.compile("^[0-9A-Za-z\\-]+$");
        return numberPattern.matcher(number).matches() && number.length() <= 10;
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        email = email.trim().toLowerCase();

        if (email.length() > 254) {
            return false;
        }

        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailPattern);
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        return cleanPhone.length() >= 10 && cleanPhone.length() <= 11;
    }

    public static boolean isValidCPF(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return false;
        }

        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Integer.parseInt(String.valueOf(cpf.charAt(i))) * (10 - i);
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) primeiroDigito = 0;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Integer.parseInt(String.valueOf(cpf.charAt(i))) * (11 - i);
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) segundoDigito = 0;

            return cpf.charAt(9) == Character.forDigit(primeiroDigito, 10) &&
                    cpf.charAt(10) == Character.forDigit(segundoDigito, 10);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidCNPJ(String cnpj) {
        if (cnpj == null || cnpj.isEmpty()) {
            return false;
        }

        cnpj = cnpj.replaceAll("[^0-9]", "");

        if (cnpj.length() != 14) {
            return false;
        }

        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        try {
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += Integer.parseInt(String.valueOf(cnpj.charAt(i))) * pesos1[i];
            }
            int primeiroDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += Integer.parseInt(String.valueOf(cnpj.charAt(i))) * pesos2[i];
            }
            int segundoDigito = soma % 11 < 2 ? 0 : 11 - (soma % 11);

            return cnpj.charAt(12) == Character.forDigit(primeiroDigito, 10) &&
                    cnpj.charAt(13) == Character.forDigit(segundoDigito, 10);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidCEP(String cep) {
        if (cep == null || cep.isEmpty()) {
            return false;
        }
        String cleanCep = cep.replaceAll("[^0-9]", "");
        return cleanCep.length() == 8;
    }

    public static void buscarCEP(String cep, CepCallback callback) {
        String cleanCep = cep.replaceAll("[^0-9]", "");

        if (cleanCep.length() != 8) {
            callback.onError("CEP inválido");
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL("https://viacep.com.br/ws/" + cleanCep + "/json/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());

                    if (jsonResponse.has("erro")) {
                        handler.post(() -> callback.onError("CEP não encontrado"));
                    } else {
                        String endereco = jsonResponse.optString("logradouro", "");
                        String bairro = jsonResponse.optString("bairro", "");
                        String cidade = jsonResponse.optString("localidade", "");
                        String estado = jsonResponse.optString("uf", "");

                        handler.post(() -> callback.onSuccess(endereco, bairro, cidade, estado));
                    }
                } else {
                    handler.post(() -> callback.onError("Erro ao buscar CEP"));
                }

                connection.disconnect();
            } catch (Exception e) {
                handler.post(() -> callback.onError("Erro de conexão: " + e.getMessage()));
            }
        });
    }
}

package com.example.easyspace.utils;

import android.os.AsyncTask;
import android.util.Patterns;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ValidationUtils {

    public interface CEPCallback {
        void onSuccess(String city, String state);
        void onError(String message);
    }

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;

        // Remove todos os caracteres não numéricos
        String cleanPhone = phone.replaceAll("[^0-9]", "");

        // Verifica se tem 10 ou 11 dígitos (com DDD)
        if (cleanPhone.length() != 10 && cleanPhone.length() != 11) {
            return false;
        }

        // Verifica se o DDD é válido (11 a 99)
        if (cleanPhone.length() >= 2) {
            int ddd = Integer.parseInt(cleanPhone.substring(0, 2));
            if (ddd < 11 || ddd > 99) {
                return false;
            }
        }

        // Se tem 11 dígitos, o terceiro deve ser 9 (celular)
        if (cleanPhone.length() == 11) {
            return cleanPhone.charAt(2) == '9';
        }

        return true;
    }

    public static boolean isValidCEP(String cep) {
        if (cep == null) return false;
        String cleanCEP = cep.replaceAll("[^0-9]", "");
        return cleanCEP.length() == 8;
    }

    public static boolean isValidCPF(String cpf) {
        if (cpf == null) return false;

        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) return false;

        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) return false;

        // Calcula o primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;

        // Verifica o primeiro dígito
        if (Character.getNumericValue(cpf.charAt(9)) != firstDigit) return false;

        // Calcula o segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;

        // Verifica o segundo dígito
        return Character.getNumericValue(cpf.charAt(10)) == secondDigit;
    }

    public static boolean isValidCNPJ(String cnpj) {
        if (cnpj == null) return false;

        // Remove caracteres não numéricos
        cnpj = cnpj.replaceAll("[^0-9]", "");

        // Verifica se tem 14 dígitos
        if (cnpj.length() != 14) return false;

        // Verifica se todos os dígitos são iguais
        if (cnpj.matches("(\\d)\\1{13}")) return false;

        // Calcula o primeiro dígito verificador
        int[] weight1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += Character.getNumericValue(cnpj.charAt(i)) * weight1[i];
        }
        int firstDigit = sum % 11;
        firstDigit = firstDigit < 2 ? 0 : 11 - firstDigit;

        // Verifica o primeiro dígito
        if (Character.getNumericValue(cnpj.charAt(12)) != firstDigit) return false;

        // Calcula o segundo dígito verificador
        int[] weight2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += Character.getNumericValue(cnpj.charAt(i)) * weight2[i];
        }
        int secondDigit = sum % 11;
        secondDigit = secondDigit < 2 ? 0 : 11 - secondDigit;

        // Verifica o segundo dígito
        return Character.getNumericValue(cnpj.charAt(13)) == secondDigit;
    }

    public static void buscarCEP(String cep, CEPCallback callback) {
        new CEPTask(callback).execute(cep);
    }

    private static class CEPTask extends AsyncTask<String, Void, CEPResult> {
        private CEPCallback callback;

        public CEPTask(CEPCallback callback) {
            this.callback = callback;
        }

        @Override
        protected CEPResult doInBackground(String... params) {
            String cepClean = params[0].replaceAll("[^0-9]", "");

            // Tenta primeiro com ViaCEP (HTTPS)
            CEPResult result = tryViaCEP(cepClean);
            if (result.success) {
                return result;
            }

            // Se falhar, tenta com API alternativa
            result = tryAwesomeAPI(cepClean);
            if (result.success) {
                return result;
            }

            // Se ambas falharem, retorna erro
            return new CEPResult(false, "Não foi possível buscar o CEP", null, null);
        }

        private CEPResult tryViaCEP(String cep) {
            try {
                String urlString = "https://viacep.com.br/ws/" + cep + "/json/";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("User-Agent", "EasySpace-Android/1.0");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    connection.disconnect();

                    JSONObject json = new JSONObject(response.toString());
                    if (!json.has("erro")) {
                        String city = json.getString("localidade");
                        String state = json.getString("uf");
                        return new CEPResult(true, null, city, state);
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new CEPResult(false, "Erro ViaCEP", null, null);
        }

        private CEPResult tryAwesomeAPI(String cep) {
            try {
                String urlString = "https://cep.awesomeapi.com.br/json/" + cep;
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("User-Agent", "EasySpace-Android/1.0");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    connection.disconnect();

                    JSONObject json = new JSONObject(response.toString());
                    if (json.has("city") && json.has("state")) {
                        String city = json.getString("city");
                        String state = json.getString("state");
                        return new CEPResult(true, null, city, state);
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new CEPResult(false, "Erro API alternativa", null, null);
        }

        @Override
        protected void onPostExecute(CEPResult result) {
            if (result.success) {
                callback.onSuccess(result.city, result.state);
            } else {
                callback.onError(result.error);
            }
        }
    }

    private static class CEPResult {
        boolean success;
        String error;
        String city;
        String state;

        public CEPResult(boolean success, String error, String city, String state) {
            this.success = success;
            this.error = error;
            this.city = city;
            this.state = state;
        }
    }
}
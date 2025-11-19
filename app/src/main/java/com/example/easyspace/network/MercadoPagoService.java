package com.example.easyspace.network;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MercadoPagoService {
    @POST("v1/card_tokens")
    Call<JsonObject> createCardToken(
            @Query("public_key") String publicKey,
            @Body JsonObject cardData
    );


    @POST("api/process_payment")
    Call<JsonObject> processPayment(@Body JsonObject paymentData);
}
package de.team5.sopra.backend.service;

import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ApiRecipeService {
    private final OkHttpClient client;

    @Value("${spring.sendgrid.api-key}")
    private String apiKey;

    @Value("${api.host}")
    private String apiHost;

    public ApiRecipeService() {
        this.client = new OkHttpClient();
    }

    @PostConstruct
    public void logConfig() {
        System.out.println("API Key: " + (apiKey != null ? "geladen" : "nicht gesetzt"));
        System.out.println("API Host: " + apiHost);
    }

    public String searchRecipes(String query) throws IOException {
        //API_URL with Query parameter
        String url = "https://gustar-io-deutsche-rezepte.p.rapidapi.com/search_api?text=" + query;

        // Create a query
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", apiKey)
                .addHeader("x-rapidapi-host", apiHost)
                .build();

        // Execute query and await response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected HTTP-Statuscode: " + response.code());
            }
            return response.body() != null ? response.body().string() : null;
        }
    }


}

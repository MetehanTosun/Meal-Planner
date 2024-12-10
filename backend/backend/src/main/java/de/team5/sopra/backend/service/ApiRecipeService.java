package de.team5.sopra.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.team5.sopra.backend.dto.ApiRecipeDTO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public List<ApiRecipeDTO> searchRecipes(String query, String diet) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("https://gustar-io-deutsche-rezepte.p.rapidapi.com/search_api?text=")
                .append(query);

        if (diet != null && !diet.isEmpty()) {
            urlBuilder.append("&diet=").append(diet);
        }

        // Request erstellen
        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .get()
                .addHeader("x-rapidapi-key", apiKey)
                .addHeader("x-rapidapi-host", apiHost)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected HTTP-Statuscode: " + response.code());
            }

            String responseBody = response.body() != null ? response.body().string() : null;

            return mapToDTOs(responseBody);
        }
    }


    private List<ApiRecipeDTO> mapToDTOs(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootArray = objectMapper.readTree(responseBody);

        List<ApiRecipeDTO> recipes = new ArrayList<>();

        if (rootArray.isArray()) {
            for (int i = 0; i < Math.min(5, rootArray.size()); i++) {
                JsonNode recipeNode = rootArray.get(i);

                // Ingredients as Strings
                List<String> ingredients = new ArrayList<>();
                recipeNode.get("ingredients").forEach(ingredientNode -> {
                    String name = ingredientNode.get("name").asText();
                    String amount = ingredientNode.has("amount") ? ingredientNode.get("amount").asText() : "";
                    String unit = ingredientNode.has("unit") ? ingredientNode.get("unit").asText() : "";
                    ingredients.add(amount + " " + unit + " " + name);
                });

                List<String> diet = new ArrayList<>();
                if (recipeNode.has("diet")) {
                    recipeNode.get("diet").forEach(dietNode -> diet.add(dietNode.asText()));
                }

                String nutrition = recipeNode.has("nutrition") && recipeNode.get("nutrition").has("kcal")
                        ? recipeNode.get("nutrition").get("kcal").asText() + " kcal"
                        : "N/A kcal";


                ApiRecipeDTO dto = new ApiRecipeDTO(
                        recipeNode.get("title").asText(),
                        ingredients,
                        recipeNode.get("totalTime").asDouble(),
                        nutrition,
                        diet
                );

                recipes.add(dto);
            }
        }

        return recipes;
    }
}

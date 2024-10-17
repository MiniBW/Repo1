import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

public class LanguageServiceExample {
    private static final String ENDPOINT = "https://joana.cognitiveservices.azure.com/";
    private static final String API_KEY = "e25334bf601e4aca88ab82d8279128ed";
    
    public static void main(String[] args) throws Exception {
        String text = "Eu gosto muito deste lugar!";
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("documents", new Object[]{
            Map.of("language", "pt", "id", "1", "text", text)
        });
        
        String requestJson = new Gson().toJson(requestBody);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(ENDPOINT + "/language/:analyze-text?api-version=2022-05-01&kind=SentimentAnalysis"))
            .header("Ocp-Apim-Subscription-Key", API_KEY)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestJson))
            .build();
        
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Resposta da API: " + response.body());
    }
}

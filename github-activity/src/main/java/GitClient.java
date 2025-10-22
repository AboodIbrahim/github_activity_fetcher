import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GitClient {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {


        System.out.println("Welcome to GitClient!");
        System.out.println("Enter a valid username as follows: java GitClient <username>");

        if (args.length != 1) {
            System.out.println("Usage: java GitClient <username>");
            return;
        }
        GitClient cli = new GitClient();
        cli.getActivity(args[0]);

    }

    private void getActivity (String username) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(new URI("https://api.github.com/users/"+username+"/events"))
                .header("accept", "application/vnd.github+json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200){
            System.out.println("ERROR!");
        } else {
            System.out.println(response.statusCode());
            System.out.println(response.body());
            System.out.println("Done!");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body());

            for (JsonNode event : node) {
                switch (event.get("type").asText()){
                    case "PushEvent":
                        System.out.println("- Pushed a commit to "+event.get("repo").get("name"));
                        break;
                    case "CreateEvent":
                        System.out.println("- Created repo: "+event.get("repo").get("name"));
                        break;
                    case "WatchEvent":
                        System.out.println("- Starred repo: "+event.get("repo").get("name"));
                        break;
                    case "IssuesEvent":
                        System.out.println("- "+event.get("action")+" issue for "+event.get("repo").get("name"));
                        break;
                    case "ForkEvent":
                        System.out.println("- Forked repo: "+event.get("repo").get("name"));
                        break;

                }
            }

        }
    }
}







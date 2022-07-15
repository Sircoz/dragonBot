package plugins;

import dragonBot.MessagePostedPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ChuckNorris  implements MessagePostedPlugin {

    @Override
    public List<String> getCommands() {
        List<String> result = new ArrayList<>();
        result.add("!chucknorris");
        return result;
    }

    @Override
    public void doCommand(DiscordApi api, MessageCreateEvent event, String command, String arguments) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://matchilling-chuck-norris-jokes-v1.p.rapidapi.com/jokes/random"))
                .header("accept", "application/json")
                .header("x-rapidapi-host", System.getenv("chuck_norris_host"))
                .header("x-rapidapi-key", System.getenv("chuck_norris_api_key"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject object = (JSONObject) new JSONParser().parse(response.body());
            String value = object.get("value").toString();
            event.getChannel().sendMessage(value);
        } catch (Exception e) {
            event.getChannel().sendMessage("An error has occurred while getting the fact");
        }
    }

    @Override
    public String getHelpText(String command) {
        return "!chucknorris -> tells you something about Chuck Norris";
    }
}

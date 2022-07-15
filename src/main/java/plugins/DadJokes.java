package plugins;


import dragonBot.MessagePostedPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.simple.JSONArray;
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

public class DadJokes implements MessagePostedPlugin {
    @Override
    public List<String> getCommands() {
        List<String> result = new ArrayList<>();
        result.add("!dadjoke");
        return result;
    }

    @Override
    public void doCommand(DiscordApi api, MessageCreateEvent event, String command, String arguments) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dad-jokes.p.rapidapi.com/random/joke"))
                .header("x-rapidapi-host", System.getenv("dadjokes_api_host"))
                .header("x-rapidapi-key", System.getenv("dadjokes_api_key"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();


        try {

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject object = (JSONObject) new JSONParser().parse(response.body());
            JSONArray body = (JSONArray) object.get("body");
            JSONObject body0 = (JSONObject) body.get(0);
            String setup = body0.get("setup").toString();
            String punchline = body0.get("punchline").toString();
            event.getChannel().sendMessage(setup);
            event.getChannel().sendMessage(punchline);

        } catch (IOException | ParseException | InterruptedException e) {
            event.getChannel().sendMessage("An error has occurred while thinking of a joke");
        }
    }

    @Override
    public String getHelpText(String command) {
        return "Usage: !dadjoke -> tells you a joke";
    }
}

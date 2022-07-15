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
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Translate implements MessagePostedPlugin {
    @Override
    public List<String> getCommands() {
        List<String> result = new ArrayList<>();
        result.add("!translate");
        result.add("!translatedetect");
        result.add("!translatelanguages");
        return result;
    }

    @Override
    public void doCommand(DiscordApi api, MessageCreateEvent event, String command, String arguments) {
        if(command.equals("!translate")) {
            String[] parts = null;
            String source = null;
            String target = null;
            String message = null;
            try {
                parts = arguments.split(" ", 3);
                source = URLEncoder.encode(parts[0], StandardCharsets.UTF_8);
                target = URLEncoder.encode(parts[1], StandardCharsets.UTF_8);
                message = URLEncoder.encode(parts[2], StandardCharsets.UTF_8);
            } catch (Exception e) {
                event.getChannel().sendMessage("wrong format");
                event.getChannel().sendMessage(getHelpText(command));
                return;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2"))
                    .header("content-type", "application/x-www-form-urlencoded")
                    .header("accept-encoding", "application/gzip")
                    .header("x-rapidapi-host", System.getenv("translate_api_host"))
                    .header("x-rapidapi-key", System.getenv("translate_api_key"))
                    .method("POST", HttpRequest.BodyPublishers.ofString("source=" + source + "&target=" + target + "&q=" + message))
                    .build();
            HttpResponse<String> response = null;
            try {
                response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject object = (JSONObject) new JSONParser().parse(response.body());
                JSONObject data = (JSONObject) object.get("data");
                JSONArray translations = (JSONArray) data.get("translations");
                JSONObject translation = (JSONObject) translations.get(0);
                String translatedText = translation.get("translatedText").toString();
                event.getChannel().sendMessage(translatedText);
            } catch (Exception e) {
                event.getChannel().sendMessage("An error has occurred while getting the translation");
            }
        } else if (command.equals("!translatedetect")) {
            if(arguments == "") {
                event.getChannel().sendMessage("You need to provide a message");
                getHelpText(command);
                return;
            }
            String message = URLEncoder.encode(arguments, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2/detect"))
                    .header("content-type", "application/x-www-form-urlencoded")
                    .header("accept-encoding", "application/gzip")
                    .header("x-rapidapi-host", System.getenv("translate_api_host"))
                    .header("x-rapidapi-key", System.getenv("translate_api_key"))
                    .method("POST", HttpRequest.BodyPublishers.ofString("q=" + message))
                    .build();
            try {
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject object = (JSONObject) new JSONParser().parse(response.body());
                JSONObject data = (JSONObject) object.get("data");
                JSONArray detectionss = (JSONArray) data.get("detections");
                JSONArray detections = (JSONArray) detectionss.get(0);
                JSONObject detection = (JSONObject) detections.get(0);
                String language = detection.get("language").toString();
                event.getChannel().sendMessage(language);
            } catch (Exception e) {
                event.getChannel().sendMessage("I don't know this language or you have a mistake");
            }
        } else if (command.equals("!translatelanguages")) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2/languages"))
                    .header("accept-encoding", "application/gzip")
                    .header("x-rapidapi-host", System.getenv("translate_api_host"))
                    .header("x-rapidapi-key", System.getenv("translate_api_key"))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            try {
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject object = (JSONObject) new JSONParser().parse(response.body());
                JSONObject data = (JSONObject) object.get("data");
                JSONArray languages = (JSONArray) data.get("languages");

                StringBuilder sb = new StringBuilder();
                for (Object language : languages) {
                    JSONObject lan = (JSONObject) language;
                    sb.append(lan.get("language").toString());
                    sb.append("\n");
                }
                event.getChannel().sendMessage(sb.toString());
            } catch (Exception e) {
                event.getChannel().sendMessage("Something went wrong");
            }
        }

    }

    @Override
    public String getHelpText(String command) {
        if (command.equals("!translate")) {
            return "Usage: !translate <source_language> <target_language> <message> -> translates your message." +
                    "\nFor languages shortcodes see https://rapidapi.com/googlecloud/api/google-translate1/details";
        } else if (command.equals("!translatedetect")) {
            return "Usage: !translatedetect <message> -> gets the language of the message.";
        } else {
            return "Usage: !translatelanguages <message> -> gets all the languages supported.";
        }
    }
}

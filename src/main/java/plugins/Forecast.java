package plugins;

import dragonBot.MessagePostedPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Forecast  implements MessagePostedPlugin {

    @Override
    public List<String> getCommands() {
        List<String> result = new ArrayList<>();
        //result.add("!forecast");
        result.add("!jsonforecast");
        return result;
    }

    @Override
    public void doCommand(DiscordApi api, MessageCreateEvent event, String command, String arguments) {
        
        String[] parts = null;
        String city = null;
        String days = null;

        try {
            parts = arguments.split(" ", 3);
            city = URLEncoder.encode(parts[0], StandardCharsets.UTF_8);
            days = URLEncoder.encode(parts[1], StandardCharsets.UTF_8);
        } catch (Exception e) {
            event.getChannel().sendMessage("wrong format");
            event.getChannel().sendMessage(getHelpText(command));
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
		.uri(URI.create("https://weatherapi-com.p.rapidapi.com/forecast.json?q=" + city + "&days=" + days))
		.header("X-RapidAPI-Host", System.getenv("forecast_api_host"))
		.header("X-RapidAPI-Key", System.getenv("forecast_api_key"))
		.method("GET", HttpRequest.BodyPublishers.noBody())
		.build();
        HttpResponse<String> response;
        if(command.equals("!jsonforecast")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try {
                response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject object = (JSONObject) new JSONParser().parse(response.body());

                JSONObject location = (JSONObject) object.get("location");
                location.remove("localtime_epoch");
                location.remove("lon");
                location.remove("lat");
                location.remove("tz_id");
                event.getChannel().sendMessage("location: " + gson.toJson(location));

                JSONObject current = (JSONObject) object.get("current");
                current.remove("last_updated");
                current.remove("wind_degree");
                current.remove("last_updated_epoch");
                current.remove("is_day");
                current.remove("feelslike_f");
                current.remove("gust_mph");
                current.remove("temp_f");
                current.remove("condition");
                current.remove("wind_mph");
                current.remove("vis_miles");
                current.remove("pressure_in");
                event.getChannel().sendMessage("current: " + gson.toJson(current));

                JSONObject forecast = (JSONObject) object.get("forecast");
                JSONArray forecastday = (JSONArray) forecast.get("forecastday");
                for (Object x : forecastday) {
                    JSONObject xCopy = (JSONObject) x;
                    xCopy.remove("hour");
                    xCopy.remove("date_epoch");
                    JSONObject day = (JSONObject) xCopy.get("day");
                    day.remove("avgtemp_f");
                    day.remove("maxtemp_f");
                    day.remove("mintemp_f");
                    day.remove("condition");
                    day.remove("maxwind_mph");
                }
                event.getChannel().sendMessage(gson.toJson(forecast));
            } catch (Exception e) {
                event.getChannel().sendMessage("An error has occurred while getting the translation");
            }
        }
        //TODO nicer forecast
    }

    @Override
    public String getHelpText(String command) {
        if (command.equals("forecast")){ 
            return "!forecast <location> <days> -> gives the forecast for the provided location. The location can be a city, a postcode or a <lat>,<long> pair";
        } else {
            return "!jsonforecast <location> <days> -> gives the forecast for the provided location." + 
                " The location can be a city, a postcode or a <lat>,<long> pair. The answer is raw json data.";
        }
        
    }

    /*
    public static void main(String[] args) {
        String[] parts = null;
        String city = null;
        String days = null;

        try {
            parts = "London 1".split(" ", 3);
            city = URLEncoder.encode(parts[0], StandardCharsets.UTF_8);
            days = URLEncoder.encode(parts[1], StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("1");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
		.uri(URI.create("https://weatherapi-com.p.rapidapi.com/forecast.json?q=" + city + "&days=" + days))
		.header("X-RapidAPI-Host", "")
		.header("X-RapidAPI-Key", "")
		.method("GET", HttpRequest.BodyPublishers.noBody())
		.build();
        HttpResponse<String> response;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject object = (JSONObject) new JSONParser().parse(response.body());

            //get location
            JSONObject location = (JSONObject) object.get("location");


            System.out.println(gson.toJson(object));
        } catch (Exception e) {
            System.out.println("2");
        }
    }*/
}
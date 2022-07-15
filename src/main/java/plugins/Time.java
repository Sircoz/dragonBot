package plugins;

import dragonBot.MessagePostedPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Time implements MessagePostedPlugin {

    private final List<String> importantLocations = new ArrayList<>(
            Arrays.asList("Europe/London", "Europe/Bucharest")
    );

    @Override
    public List<String> getCommands() {
        List<String> result = new ArrayList<>();
        result.add("!time");
        return result;
    }

    @Override
    public void doCommand(DiscordApi api, MessageCreateEvent event, String command, String arguments) {
        if(arguments == "") {
            for (String locationId : importantLocations) {
                sendDate(event, locationId);
            }
        } else {
            try {
                sendDate(event, arguments);
            } catch (Exception e) {
                event.getChannel().sendMessage("I don't know this place. See https://www.joda.org/joda-time/timezones.html for all the places.");
            }
        }
    }

    private void sendDate(MessageCreateEvent event, String arguments) {
        DateTimeZone zone = DateTimeZone.forID(arguments);
        LocalTime localTime = new LocalTime(zone);
        LocalDate localDate = new LocalDate(zone);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("LocalTime : ");
        stringBuilder.append(localTime.toString());
        stringBuilder.append("\nlocalDate : ");
        stringBuilder.append(localDate.toString());
        stringBuilder.append("\nDateTimeZone : ");
        stringBuilder.append(zone.toString());
        stringBuilder.append("\n\n");

        event.getChannel().sendMessage(stringBuilder.toString());
    }

    @Override
    public String getHelpText(String command) {
        return "Tells you the time.\n" +
                "!time -> tells time in London uk\n" +
                "!time <place> -> tells you the time in that zone. Generally the format is <Continent>/<City>";
    }

    public static void main(String[] args) {
        DateTimeFormatter dateFormat = DateTimeFormat
                .forPattern("G,C,Y,x,w,e,E,Y,D,M,d,a,K,h,H,k,m,s,S,z,Z");

        String dob = "2002-01-15";
        DateTimeZone zone = DateTimeZone.forID("Europe/London");
        LocalTime localTime = new LocalTime(zone);
        LocalDate localDate = new LocalDate(zone);
        DateTime dateTime = new DateTime(zone);
        LocalDateTime localDateTime = new LocalDateTime(zone);
        DateTimeZone dateTimeZone = zone;

        System.out
                .println("dateFormatr : " + dateFormat.print(localDateTime));
        System.out.println("LocalTime : " + localTime.toString());
        System.out.println("localDate : " + localDate.toString());
        System.out.println("dateTime : " + dateTime.toString());
        System.out.println("localDateTime : " + localDateTime.toString());
        System.out.println("DateTimeZone : " + dateTimeZone.toString());
    }
}

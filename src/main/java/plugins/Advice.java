package plugins;

import dragonBot.MessagePostedPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Advice implements MessagePostedPlugin {
    @Override
    public List<String> getCommands() {
        List<String> result = new ArrayList<>();
        result.add("!advice");
        return result;
    }

    @Override
    public void doCommand(DiscordApi session, MessageCreateEvent event, String command, String arguments) {
        try{
            InputStream in = getClass().getResourceAsStream("/advices.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            try {
                List<String> advices = new ArrayList<>();
                String line = reader.readLine();

                while (line != null) {
                    advices.add(line);
                    line = reader.readLine();
                }

                Random rd = new Random();
                String advice = advices.get(rd.nextInt(advices.toArray().length - 1));
                event.getChannel().sendMessage(advice);
            } finally {
                reader.close();
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            System.out.println(e.getMessage());
            event.getChannel().sendMessage("It seems I have lost my advices");
        }
    }

    @Override
    public String getHelpText(String command) {
        return "Always pass on what you have learned.";
    }
}

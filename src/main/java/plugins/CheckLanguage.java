package plugins;

import dragonBot.MessagePostedPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.List;

public class CheckLanguage implements MessagePostedPlugin {
    @Override
    public List<String> getCommands() {
        List<String> result = new ArrayList<>();
        result.add("!checksupportedlanguages");
        result.add("!checklanguage");
        return result;
    }

    @Override
    public void doCommand(DiscordApi api, MessageCreateEvent event, String command, String arguments) {

    }

    @Override
    public String getHelpText(String command) {
        if(command.equals("!checklanguage")) {
            return "Usage: !checklanguage <language> <text> -> checks your writing";
        } else {
            return "Usage: !checklanguage -> tells you the supported languages";
        }
    }
}

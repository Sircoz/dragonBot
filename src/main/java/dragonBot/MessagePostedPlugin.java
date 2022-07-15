package dragonBot;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.List;

public interface MessagePostedPlugin {

    List<String> getCommands();
    void doCommand(DiscordApi api, MessageCreateEvent event, String command, String arguments);
    String getHelpText(String command);

}

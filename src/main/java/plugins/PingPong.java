package plugins;

import dragonBot.MessagePostedPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.List;

public class PingPong implements MessagePostedPlugin {
    @Override
    public List<String> getCommands() {
        List<String> result = new ArrayList<>();
        result.add("!ping");
        return result;
    }

    @Override
    public void doCommand(DiscordApi api, MessageCreateEvent event, String command, String arguments) {
        event.getChannel().sendMessage("!Pong");
    }

    @Override
    public String getHelpText(String command) {
        return "If you type !ping the bot replies with Pong!";
    }
}

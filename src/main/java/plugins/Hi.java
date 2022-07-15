package plugins;

import dragonBot.MessagePostedPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.List;

public class Hi implements MessagePostedPlugin {
    @Override
    public List<String> getCommands() {
        List<String> result = new ArrayList<>();
        result.add("!hi");
        result.add("!hello");
        return result;
    }

    @Override
    public void doCommand(DiscordApi api, MessageCreateEvent event, String command, String arguments) {
        event.getMessage().getUserAuthor().ifPresent((user -> event.getChannel().sendMessage("Hi " + user.getName())));
    }

    @Override
    public String getHelpText(String command) {
        return "Says hi to you";
    }
}

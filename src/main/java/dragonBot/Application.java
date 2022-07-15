package dragonBot;

import java.util.*;
import javax.security.auth.login.LoginException;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;

public class Application {

    public static void main(String[] args){
        String token = System.getenv("discord_token");
        System.out.println("before api");
        DiscordApi api = (DiscordApi)(new DiscordApiBuilder()).setToken(token).login().join();
        System.out.println(api);
        Collection<TextChannel> channels = api.getTextChannelsByName("deployed-dev");

        if (!channels.isEmpty()) {
            for (TextChannel channel : channels) {
                channel.sendMessage("Redeployed");
            }
        }

        Map<String, MessagePostedPlugin> pluginMap = createPluginMap(PluginList.messagePostedPlugins);

        api.addMessageCreateListener((event) -> {

            //verify that the bot doesn't answer to its own messages
            Optional<User> sender = event.getMessage().getUserAuthor();
            if(sender.isPresent()) {
                User user = sender.get();
                if(user.getName().equals("Dragonbot")) {
                    return;
                }
            }

            String message = event.getMessageContent();

            if(message.startsWith("!")) {
                String[] parts = message.split(" ", 2);
                String command = parts[0];
                if(command.equals("!help")) {
                    if(parts.length == 1) {
                        StringBuilder sb = new StringBuilder();
                        for (String comm : pluginMap.keySet()) {
                            sb.append(comm);
                            sb.append("\n");
                        }
                        event.getChannel().sendMessage(sb.toString());
                    } else {
                        MessagePostedPlugin plugin = pluginMap.get("!" + parts[1]);
                        if(plugin != null) {
                            event.getChannel().sendMessage(plugin.getHelpText(parts[1]));
                        }
                    }
                }
                MessagePostedPlugin plugin = pluginMap.get(command);
                if(plugin != null) {
                    if(parts.length == 1) {
                        plugin.doCommand(api, event, command, "");
                    } else {
                        plugin.doCommand(api, event, command, parts[1]);
                    }

                }
            }

            if (event.getMessageContent().equalsIgnoreCase("!close")) {
                event.getChannel().sendMessage("Bye!");

                api.disconnect();
            }

        });
    }

    private static Map<String, MessagePostedPlugin> createPluginMap(MessagePostedPlugin[] messagePostedPlugins) {
        Map<String, MessagePostedPlugin> pluginMap = new HashMap<>();
        for (MessagePostedPlugin plugin : messagePostedPlugins) {
            for (String command : plugin.getCommands()) {
                pluginMap.put(command, plugin);
            }
        }
        return pluginMap;
    }
}

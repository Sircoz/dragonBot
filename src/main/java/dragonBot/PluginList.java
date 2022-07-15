package dragonBot;

import plugins.*;

public class PluginList {
    public static final MessagePostedPlugin[] messagePostedPlugins = {
            new PingPong(),
            new Hi(),
            new Advice(),
            new Wow(),
            new Time(),
            new DadJokes(),
            new ChuckNorris(),
            new Translate(),
            new Forecast()
    };
}

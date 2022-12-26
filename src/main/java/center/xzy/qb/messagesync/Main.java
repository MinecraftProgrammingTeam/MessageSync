package center.xzy.qb.messagesync;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import center.xzy.qb.messagesync.events.customEventHandler;
import org.bukkit.plugin.Plugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        // config.yml
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        Plugin plugin = getPlugin(Main.class);
        // check config
        Integer qn = plugin.getConfig().getInt("qn");
        String uuid = plugin.getConfig().getString("uuid");
        if ((uuid == null || uuid.length() == 0) || (qn == 0)){
            getLogger().warning(ChatColor.RED + "Please fill the config in ./plugin/MessageSync/config.yml and reload the server!");
        }else{
            // register events
            getServer().getPluginManager().registerEvents(new customEventHandler(), this);
            getLogger().info(ChatColor.GREEN + "Enabled Message Sync Plugin for PBF!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().warning(ChatColor.RED + "Disabled Message Sync for PBF!");
    }
}

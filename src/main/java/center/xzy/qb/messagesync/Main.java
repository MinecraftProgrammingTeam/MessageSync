package center.xzy.qb.messagesync;

import center.xzy.qb.messagesync.executor.*;
import center.xzy.qb.messagesync.commands.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import center.xzy.qb.messagesync.events.*;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public final class Main extends JavaPlugin {
    public static boolean pluginStatus = false;
    public static Main instance;
    public static Map<String, List<String>> LoginData = new HashMap<>();
    public static Connection dbConn;
    static {
        try {
            dbConn = DriverManager.getConnection("jdbc:sqlite:"+System.getProperty("user.dir")+"/plugins/MessageSync/user.db");
            initDatabase(dbConn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        // config.yml
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        Plugin plugin = getPlugin(Main.class);

        // reg commands
        getCommand("ms").setExecutor(new CommandHandler());
        getCommand("say").setExecutor(new sayHandler());

        // check config
        int qn = plugin.getConfig().getInt("qn");
        String uuid = plugin.getConfig().getString("uuid");
        if ((uuid == null || uuid.length() == 0) || (qn == 0)){
            getLogger().warning(ChatColor.RED + "Please fill the config in ./plugin/MessageSync/config.yml and reload the server!");
        }else{
            // register events
            getServer().getPluginManager().registerEvents(new customEventHandler(), this);
            getServer().getPluginManager().registerEvents(new inventoryEventHandler(), this);
            pluginStatus = true;
            getLogger().info(ChatColor.GREEN + "Enabled Message Sync Plugin for PBF!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().warning(ChatColor.RED + "Disabled Message Sync for PBF!");
    }

    public static void initDatabase(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("create table if not exists `password`(`id` string, `password` string, `time` string)");
    }

    public static ItemStack NewItem(Material type, String DisplayName, List<String> Lores){
        ItemStack myItem = new ItemStack(type);
        ItemMeta im = myItem.getItemMeta();
        im.setDisplayName(DisplayName);
        im.setLore(Lores);
        myItem.setItemMeta(im);
        return myItem;
    }

    public static ItemStack NewItem(Material type, String DisplayName){
        return NewItem(type, DisplayName, Arrays.asList());
    }
}
package center.xzy.qb.messagesync;

import center.xzy.qb.messagesync.events.customEventHandler;
import center.xzy.qb.messagesync.events.inventoryEventHandler;
import center.xzy.qb.messagesync.executor.CommandHandler;
import center.xzy.qb.messagesync.socket.SocketClient;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public final class Main extends JavaPlugin {
    public static boolean pluginStatus = true;
    public static Main instance;
    public static Map<String, List<String>> LoginData = new HashMap<>();  // 登录储存的玩家数据
    public static Map<String, String> gmData = new HashMap<>();  // 玩家游戏模式数据
    public static Map<String, List<String>> regData = new HashMap<>();  // 注册（二次输入密码）储存的玩家数据
    public static Map<String, String> regIpData = new HashMap<>();  // PreLogin储存的登录IP数据
    public static Connection dbConn;  // 数据库连接，在`onEnable`方法中初始化
    public static SocketClient socket;

    @Override
    public void onEnable() {
        instance = this;

        // Plugin startup logic
        // config.yml
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // 初始化Sqlite数据库
        try {
            dbConn = DriverManager.getConnection("jdbc:sqlite:"+System.getProperty("user.dir")+"/plugins/MessageSync/user.db");
            initDatabase(dbConn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // reg commands
        Objects.requireNonNull(getCommand("ms")).setExecutor(new CommandHandler());

        // register events
        getServer().getPluginManager().registerEvents(new customEventHandler(), this);
        getServer().getPluginManager().registerEvents(new inventoryEventHandler(), this);

        // check TitleManager
        // TODO connect TitleManager

        // WebSocket
        if (getConfig().getBoolean("enable-socket")){
            reconnectSocket();
        }

        // log information
        getLogger().info(ChatColor.GREEN + "More info on " + ChatColor.BLUE + "https://github.com/MinecraftProgrammingTeam/MessageSync");
        getLogger().info(ChatColor.GREEN + "Enabled MessageSync Plugin for PBF!");
    }

    public static void reconnectSocket(){
        try {
            socket = new SocketClient(instance.getConfig().getString("socket-uri"));
            socket.connect();
            instance.getLogger().info(ChatColor.GREEN + "已连接WebSocket服务器");
        } catch (URISyntaxException e) {
            instance.getLogger().warning(ChatColor.RED + "连接WebSocket服务器失败：" + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            dbConn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (getConfig().getBoolean("enable-socket")) {
            socket.close();
        }
        getLogger().warning(ChatColor.RED + "Disabled Message Sync for PBF!");
    }

    public static void initDatabase(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("create table if not exists `ms_users`(`id` string PRIMARY KEY, `password` string, `time` string, `ip` string)");
    }

    public static ItemStack NewItem(Material type, String DisplayName, List<String> Lores){
        ItemStack myItem = new ItemStack(type);
        ItemMeta im = myItem.getItemMeta();
        if (im != null) {
            im.setDisplayName(DisplayName);
            im.setLore(Lores);
        }
        myItem.setItemMeta(im);
        return myItem;
    }

    public static ItemStack NewItem(Material type, String DisplayName){
        return NewItem(type, DisplayName, Collections.emptyList());
    }

    public static String MD5(String key){
        char[] hexDigests = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            byte[] in = key.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            messageDigest.update(in);
            // 获得密文
            byte[] md = messageDigest.digest();
            // 将密文转换成16进制字符串形式
            int j = md.length;
            char[] str = new char[j*2];
            int k = 0;
            for (byte b : md) {
                str[k++] = hexDigests[b >>> 4 & 0xf];
                str[k++] = hexDigests[b & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("md5加密失败",e);
        }
    }
}
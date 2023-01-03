package center.xzy.qb.messagesync;

import center.xzy.qb.messagesync.executor.*;
import center.xzy.qb.messagesync.commands.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import center.xzy.qb.messagesync.events.*;
import org.bukkit.plugin.Plugin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    public static boolean pluginStatus = true;
    public static Main instance;
    public static Map<String, List<String>> LoginData = new HashMap<>();  // 登录储存的玩家数据
    public static Map<String, String> gmData = new HashMap<>();  // 玩家游戏模式数据
    public static Map<String, List<String>> regData = new HashMap<>();  // 注册（二次输入密码）储存的玩家数据
    public static Map<String, String> regIpData = new HashMap<>();  // prelogin储存的登录IP数据
    public static Connection dbConn;  // 数据库连接，在`onEnable`方法中初始化

    @Override
    public void onEnable() {
        instance = this;
        try {
            dbConn = DriverManager.getConnection("jdbc:sqlite:"+System.getProperty("user.dir")+"/plugins/MessageSync/user.db");
            initDatabase(dbConn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Plugin startup logic
        // config.yml
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        Plugin plugin = getPlugin(Main.class);

        // reg commands
        getCommand("ms").setExecutor(new CommandHandler());
        getCommand("say").setExecutor(new sayHandler());

        // register events
        getServer().getPluginManager().registerEvents(new customEventHandler(), this);
        getServer().getPluginManager().registerEvents(new inventoryEventHandler(), this);

        // check TitleManager
        // more code...

        // log information
        getLogger().info(ChatColor.GREEN + "More info on " + ChatColor.BLUE + "https://minept.top");
        getLogger().info(ChatColor.GREEN + "Enabled MessageSync Plugin for PBF!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            dbConn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        getLogger().warning(ChatColor.RED + "Disabled Message Sync for PBF!");
    }

    public static void initDatabase(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("create table if not exists `password`(`id` string PRIMARY KEY, `password` string, `time` string, `ip` string)");
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

    public static String MD5(String key){
        char hexDigests[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
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
            for (int i = 0; i < j; i++) {
                byte b = md[i];
                str[k++] = hexDigests[b >>> 4 & 0xf];  // >>> 无符号右移。这里将字节b右移4位，低位抛弃，就等于是高4位于0xf做与运算。4位最多表示15。
                str[k++] = hexDigests[b & 0xf]; //用 1字节=8位，与0xf与运算，高4位必为0，就得到了低四位的数。
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("md5加密失败",e);
        }
    }
}
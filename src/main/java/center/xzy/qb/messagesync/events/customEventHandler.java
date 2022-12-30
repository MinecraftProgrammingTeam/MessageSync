package center.xzy.qb.messagesync.events;

import center.xzy.qb.messagesync.scheduler.PlayerLogin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import center.xzy.qb.messagesync.Main;
import org.bukkit.scheduler.BukkitRunnable;
import org.sqlite.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class customEventHandler implements Listener {
    Plugin plugin = Main.getPlugin(Main.class);

    @EventHandler
    public void CloseInventory(InventoryCloseEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.GREEN + "欢迎来到" + plugin.getConfig().getString("server-name") + "服务器 " + ChatColor.BLUE + "请登录") || event.getView().getTitle().equalsIgnoreCase(ChatColor.GREEN + "欢迎来到" + plugin.getConfig().getString("server-name") + "服务器 " + ChatColor.BLUE + "注册（第一遍输入密码，第二遍确认密码）")) {
            if (Main.regData.containsKey(event.getPlayer().getName()) || Main.LoginData.containsKey(event.getPlayer().getName())) {
                event.getPlayer().setInvulnerable(false);
                event.getPlayer().sendMessage(ChatColor.RED + "请注意，您未完成登录/注册，请发送指令" + ChatColor.GREEN + "/ms login" + ChatColor.RED + "进行登录/注册，否则您将被踢出服务器！");
            }
        }
    }

    @EventHandler
    public void PlayerChat(AsyncPlayerChatEvent event) {
        sendRequest("<" + event.getPlayer().getName() + "> " + event.getMessage());
    }

    @EventHandler
    public void PlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (Main.regIpData.containsKey(event.getName())){
            Main.regIpData.replace(event.getName(), event.getAddress().toString());
        } else {
            Main.regIpData.put(event.getName(), event.getAddress().toString());
        }
    }

    @EventHandler
    public void PlayerLogin(PlayerJoinEvent event) {
//        String Message = "玩家<" + event.getName() + ">已从" + event.getAddress() + "登录服务器";
        sendRequest(event.getJoinMessage());

        if (plugin.getConfig().getBoolean("login-verify")) {
            String title = null;
            int timeout = 0;
            try{
                Statement statement = Main.dbConn.createStatement();
                ResultSet rs = statement.executeQuery("select * from `password` where `id`='" + event.getPlayer().getName() + "'");
                boolean flag = false;
                while(rs.next()) {
                    flag = true;
                }
                if (!flag) {
                    // 注册
                    timeout = plugin.getConfig().getInt("reg-allow-tick");
                    title = ChatColor.GREEN + "欢迎来到" + plugin.getConfig().getString("server-name") + "服务器 " + ChatColor.BLUE + "注册（第一遍输入密码，第二遍确认密码）";
                } else {
                    // 登录
                    timeout = plugin.getConfig().getInt("login-allow-tick");
                    title = ChatColor.GREEN + "欢迎来到" + plugin.getConfig().getString("server-name") + "服务器 " + ChatColor.BLUE + "请登录";
                }
            } catch (SQLException e){
                throw new RuntimeException(e);
            }

            PlayerLogin playerLogin = new PlayerLogin();
            playerLogin.setPlayer(event.getPlayer());
            playerLogin.runTaskLater(Main.instance, timeout);
            openInv(event.getPlayer(), title);
        }
    }

    public static void openInv(Player player, String title) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.CHEST, title);

        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码1"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "1"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "2"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "2"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "3"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "3"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "3"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码2"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码3"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码7"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码4"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码5"));

        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码6"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "4"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "4"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "4"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "4"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "5"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "5"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "5"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "5"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "5"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "6"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "6"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "6"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "6"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "6"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "6"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "0"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码8"));
        inv.addItem(Main.NewItem(Material.GREEN_STAINED_GLASS, "确认"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码9"));
        inv.addItem(Main.NewItem(Material.RED_STAINED_GLASS, "清空"));

        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码11"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "7"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "7"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "7"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "7"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "7"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "7"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "7"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "8"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "8"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "8"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "8"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "8"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "8"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "8"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "8"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "9"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "9"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "9"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "9"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "9"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "9"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "9"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "9"));
        inv.addItem(Main.NewItem(Material.ORANGE_STAINED_GLASS, "9"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码12"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码13"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码10"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码14"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码15"));

        player.closeInventory();
        player.openInventory(inv);
        player.setInvulnerable(true);

        if (Main.LoginData.containsKey(player.getName())){
            Main.LoginData.replace(player.getName(), new ArrayList<>());
        } else {
            Main.LoginData.put(player.getName(), new ArrayList<>());
        }
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event) {
        sendRequest(event.getQuitMessage());
    }

    @EventHandler
    public void PlayerRespawn(PlayerRespawnEvent event) {
        sendRequest("玩家<" + event.getPlayer().getName() + ">重生在了(" + event.getRespawnLocation().getX() + "," + event.getRespawnLocation().getY() + "," + event.getRespawnLocation().getZ() + ")");
    }

    @EventHandler
    public void EntityDeath(PlayerDeathEvent event) {
        sendRequest(event.getDeathMessage());
    }

    public String regReplace(String content,String pattern,String newString){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        return m.replaceAll(newString);
    }

    public void sendRequest(String msg) {
        if (Main.pluginStatus && plugin.getConfig().getBoolean("message-sync")) {
            // 去除颜色代码
            String pattern1 = "§[a-z0-9]?";
            String pattern2 = "&[a-z0-9]?";
            msg = regReplace(msg, pattern1, "");
            msg = regReplace(msg, pattern2, "");

            // 首先抓取异常并处理
            String returnString = "1";
            try {
                // 拼接url
                Integer qn = plugin.getConfig().getInt("qn");
                String uuid = plugin.getConfig().getString("uuid");
                String urlP = plugin.getConfig().getString("message-report-url") + "/MCServer?msg=" + URLEncoder.encode(msg, "UTF-8") + "&qn=" + qn + "&uuid=" + uuid;
                if ((uuid == null || uuid.length() == 0) || (qn == 0)){
                    Main.instance.getLogger().warning(ChatColor.RED + "Please fill the config in ./plugin/MessageSync/config.yml and reload the server!");
                    return ;
                }

                // 1  创建URL对象,接收用户传递访问地址对象链接
                URL url = new URL(urlP);

                // 2 打开用户传递URL参数地址
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                connect.setRequestProperty("User-agent", "	Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");

                // 3 设置HTTP请求的一些参数信息
                connect.setRequestMethod("GET"); // 参数必须大写
                connect.connect();


                // 4 获取URL请求到的数据，并创建数据流接收
                InputStream isString = connect.getInputStream();

                // 5 构建一个字符流缓冲对象,承载URL读取到的数据
                BufferedReader isRead = new BufferedReader(new InputStreamReader(isString));

                // 6 输出打印获取到的文件流
                String str = "";
                while ((str = isRead.readLine()) != null) {
                    str = new String(str.getBytes(), "UTF-8"); //解决中文乱码问题
                    //          System.out.println("文件解析打印：");
                    //          System.out.println(str);
                    returnString = str;
                }

                // 7 关闭流
                isString.close();
                connect.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

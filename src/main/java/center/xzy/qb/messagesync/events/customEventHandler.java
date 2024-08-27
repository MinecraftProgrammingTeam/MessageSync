package center.xzy.qb.messagesync.events;

import center.xzy.qb.messagesync.Main;
import center.xzy.qb.messagesync.scheduler.PlayerLogin;
import com.alibaba.fastjson.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String message = event.getMessage();
        if (plugin.getConfig().getBoolean("sync-flag-enable")){
            if (message.startsWith(Objects.requireNonNull(plugin.getConfig().getString("sync-flag")))){
                if (message.length() != 0) {
                    message = message.substring(1);
                    event.setMessage(message);
                }
            } else {
                return;
            }
        }
        sendRequest("<" + event.getPlayer().getName() + "> " + message);
    }

    @EventHandler
    public void PlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (Main.regData.containsKey(event.getPlayer().getName()) || Main.LoginData.containsKey(event.getPlayer().getName())){
            if (!event.getMessage().equals("/ms login")){
                event.setMessage(" ");
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "在通过登录/注册之前不可使用指令！");
            }
        }
    }

    @EventHandler
    public void PlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getName().startsWith(Objects.requireNonNull(plugin.getConfig().getString("login-ignore-prefix")))) {
            return;
        }
        if (Main.regIpData.containsKey(event.getName())){
            Main.regIpData.replace(event.getName(), event.getAddress().toString());
        } else {
            Main.regIpData.put(event.getName(), event.getAddress().toString());
        }
    }

    @EventHandler
    public void PlayerLogin(PlayerJoinEvent event) {
        if (event.getPlayer().getName().startsWith(Objects.requireNonNull(plugin.getConfig().getString("login-ignore-prefix")))) {
            return;
        }

//        String Message = "玩家<" + event.getName() + ">已从" + event.getAddress() + "登录服务器";
        sendRequest(event.getJoinMessage());

        if (plugin.getConfig().getBoolean("login-verify")) {
            String title;
            int timeout;
            try{
                Statement statement = Main.dbConn.createStatement();
                ResultSet rs = statement.executeQuery("select * from `ms_users` where `id`='" + event.getPlayer().getName() + "'");
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

            if (timeout != 0) {
                PlayerLogin playerLogin = new PlayerLogin();
                playerLogin.setPlayer(event.getPlayer());
                playerLogin.runTaskLater(Main.getPlugin(Main.class), timeout);
            }
            Player player = event.getPlayer();
            if (Main.gmData.containsKey(player.getName())){
                Main.gmData.replace(player.getName(), player.getGameMode().toString());
            } else {
                Main.gmData.put(player.getName(), player.getGameMode().toString());
            }
            Main.instance.getLogger().warning(player.getGameMode().toString());
            player.setGameMode(GameMode.ADVENTURE);
            openInv(event.getPlayer(), title);
        }
    }

    public static void addPwdBtnItem(Inventory inv, Integer title, Material material){
        int iter = title==0?1:title;
        for(int i=0; i<iter; i++){
            inv.addItem(Main.NewItem(material, title.toString()));
        }
    }

    public static void openInv(Player player, String title) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.CHEST, title);

        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码1"));
        addPwdBtnItem(inv, 1, Material.ORANGE_STAINED_GLASS);
        addPwdBtnItem(inv, 2, Material.ORANGE_STAINED_GLASS);
        addPwdBtnItem(inv, 3, Material.ORANGE_STAINED_GLASS);
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码2"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码3"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码7"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码4"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码5"));

        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码6"));
        addPwdBtnItem(inv, 4, Material.ORANGE_STAINED_GLASS);
        addPwdBtnItem(inv, 5, Material.ORANGE_STAINED_GLASS);
        addPwdBtnItem(inv, 6, Material.ORANGE_STAINED_GLASS);
        addPwdBtnItem(inv, 0, Material.ORANGE_STAINED_GLASS);
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码8"));
        inv.addItem(Main.NewItem(Material.GREEN_STAINED_GLASS, "确认"));
        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码9"));
        inv.addItem(Main.NewItem(Material.RED_STAINED_GLASS, "清空"));

        inv.addItem(Main.NewItem(Material.WHITE_STAINED_GLASS, "请输入密码11"));
        addPwdBtnItem(inv, 7, Material.ORANGE_STAINED_GLASS);
        addPwdBtnItem(inv, 8, Material.ORANGE_STAINED_GLASS);
        addPwdBtnItem(inv, 9, Material.ORANGE_STAINED_GLASS);
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

    public String parseMsg(String msg) throws UnsupportedEncodingException {
        // 去除颜色代码
        String pattern1 = "§[a-z0-9]?";
        String pattern2 = "&[a-z0-9]?";
        msg = regReplace(msg, pattern1, "");
        msg = regReplace(msg, pattern2, "");

        // 拼接url
        return msg;
    }

    public void sendRequest(String msg){
        if (Main.pluginStatus && plugin.getConfig().getBoolean("enable-socket")) {
            try {
                msg = parseMsg(msg);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            if (Main.socket.isClosed()){
                Main.instance.getLogger().warning(ChatColor.RED + "WebSocket连接已关闭，无法发送消息！");
                return;
            }

            Integer qn = plugin.getConfig().getInt("sync-qn");
            JSONObject data = new JSONObject();
            data.put("msg", msg);
            data.put("qn", qn);
            Main.socket.csend("server_message", data);
        }
    }
}

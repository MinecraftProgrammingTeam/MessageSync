package center.xzy.qb.messagesync.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import center.xzy.qb.messagesync.Main;
import org.sqlite.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class inventoryEventHandler implements Listener {
    Plugin plugin = Main.getPlugin(Main.class);
    public static Map<Integer, Integer> pdMap = new HashMap<>();
    static{
        pdMap.put(1, 1);
        pdMap.put(2, 2);
        pdMap.put(3, 3);
        pdMap.put(10, 4);
        pdMap.put(11, 5);
        pdMap.put(12, 6);
        pdMap.put(19, 7);
        pdMap.put(20, 8);
        pdMap.put(21, 9);
        pdMap.put(13, 0);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws SQLException {
        if (event.getWhoClicked() instanceof Player == false) { return;}
        Player p = (Player)event.getWhoClicked();
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.GREEN + "欢迎来到" + plugin.getConfig().getString("server-name") + "服务器 " + ChatColor.BLUE + "请登录") ) {
            List<String> LoginData = Main.LoginData.get(p.getName());
            switch (event.getRawSlot()){
                case 15:
                    // 确认
                    Main.instance.getLogger().info(LoginData.toString());

                    Statement statement = Main.dbConn.createStatement();
                    ResultSet rs = statement.executeQuery("select * from `password` where `id`='" + p.getName() + "'");
                    boolean flag = false;
                    String currentPassword = null;
                    String password = StringUtils.join(LoginData, "");
                    while(rs.next()) {
                        flag = true;
                        currentPassword = rs.getString("password");
                    }
                    if (!flag) {
                        // 注册
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String dateString = sdf.format(date);
                        statement.executeUpdate("insert into password values('" + p.getName() + "', '" + password + "', '" + dateString + "')");
                        p.closeInventory();
                        p.sendMessage(ChatColor.GREEN + "注册成功！");
                    } else {
                        Main.instance.getLogger().info(password);
                        Main.instance.getLogger().info(currentPassword);
                        if (password.equals(currentPassword)) {
                            // 登录成功
                            p.closeInventory();
                            p.sendMessage(ChatColor.GREEN + "登陆成功！");
                        } else {
                            // 密码错误
                            p.closeInventory();
                            p.kickPlayer(ChatColor.RED + "密码错误！");
                        }
                    }
                    break;
                case 17:
                    // 清空
                    Main.LoginData.replace(p.getName(), new ArrayList<>());
                    break;
                default:
                    Integer slot = pdMap.get(event.getRawSlot());
                    LoginData.add(String.valueOf(slot));
                    Main.LoginData.replace(p.getName(), LoginData);
                    break;
            }
            event.setCancelled(true);
        }
    }
}
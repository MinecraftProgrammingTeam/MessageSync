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

    private void cleanData(Player p) {
        Main.LoginData.remove(p.getName());
        Main.regData.remove(p.getName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws SQLException {
        if (event.getWhoClicked() instanceof Player == false) { return;}
        Player p = (Player)event.getWhoClicked();
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.GREEN + "欢迎来到" + plugin.getConfig().getString("server-name") + "服务器 " + ChatColor.BLUE + "请登录") || event.getView().getTitle().equalsIgnoreCase(ChatColor.GREEN + "欢迎来到" + plugin.getConfig().getString("server-name") + "服务器 " + ChatColor.BLUE + "注册（第一遍输入密码，第二遍确认密码）")) {
            List<String> LoginData = Main.LoginData.get(p.getName());
            switch (event.getRawSlot()){
                case 15:
                    // 确认
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
                        if (Main.regData.containsKey(p.getName())) {
                            List<String> regData = Main.regData.get(p.getName());
                            String repeatPassword = StringUtils.join(regData, "");
                            if (repeatPassword.equals(password)) {
                                String ip = null;
                                if (Main.regIpData.containsKey(p.getName())) {
                                    ip = Main.regIpData.get(p.getName());
                                } else {
                                    p.kickPlayer("没有ID的PreLogin数据，请重试！");
                                    return ;
                                }

                                rs = statement.executeQuery("select * from `password` where `ip`='" + ip + "'");
                                int ipCount = 0;
                                while(rs.next()) {
                                    ipCount++;
                                }

                                if (ipCount >= plugin.getConfig().getInt("reg-ip-count") && plugin.getConfig().getInt("reg-ip-count") != 0) {
                                    p.kickPlayer(plugin.getConfig().getString("reg-ip-max-fail-msg"));
                                    return ;
                                }

                                // 写入数据库
                                Date date = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                String dateString = sdf.format(date);
                                statement.executeUpdate("insert into password values('" + p.getName() + "', '" + Main.MD5(password) + "', '" + dateString + "', '" + ip + "')");
                                cleanData(p);
                                p.closeInventory();
                                p.setInvulnerable(false);
                                p.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("reg-success-msg"));
                            } else {
                                p.setInvulnerable(false);
                                cleanData(p);
                                p.kickPlayer(ChatColor.RED + "重复密码与密码不一致，请重试！");
                            }
                        } else {
                            Main.regData.put(p.getName(), new ArrayList<>());
                        }
                    } else {
                        if (Main.MD5(password).equals(currentPassword)) {
                            // 登录成功
                            cleanData(p);
                            p.closeInventory();
                            p.setInvulnerable(false);
                            p.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("login-success-msg"));
                        } else {
                            // 密码错误
                            cleanData(p);
                            p.closeInventory();
                            p.setInvulnerable(false);
                            p.kickPlayer(ChatColor.RED + plugin.getConfig().getString("login-fail-msg"));
                        }
                    }
                    break;
                case 17:
                    // 清空
                    Main.LoginData.replace(p.getName(), new ArrayList<>());
                    break;
                default:
                    // 输入数字
                    if (!pdMap.containsKey(event.getRawSlot())){
                        return ;
                    }
                    Integer slot = pdMap.get(event.getRawSlot());
                    if (Main.regData.containsKey(p.getName())) {
                        List<String> regData = Main.regData.get(p.getName());
                        regData.add(String.valueOf(slot));
                        Main.regData.replace(p.getName(), regData);
                    } else {
                        LoginData.add(String.valueOf(slot));
                        Main.LoginData.replace(p.getName(), LoginData);
                    }
                    break;
            }
            event.setCancelled(true);
        }
    }
}
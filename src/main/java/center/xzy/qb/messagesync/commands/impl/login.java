package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import center.xzy.qb.messagesync.scheduler.PlayerLogin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import center.xzy.qb.messagesync.commands.ICommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import center.xzy.qb.messagesync.events.customEventHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class login extends ICommand {
    Plugin plugin = Main.getPlugin(Main.class);

    public login(){
        super("login", "", "打开登录GUI界面 - 登陆系统");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        if (plugin.getConfig().getBoolean("login-verify")) {
            String title = null;
            int timeout = 0;
            try{
                Statement statement = Main.dbConn.createStatement();
                ResultSet rs = statement.executeQuery("select * from `password` where `id`='" + sender.getName() + "'");
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

//            PlayerLogin playerLogin = new PlayerLogin();
//            playerLogin.setPlayer((Player) sender);
//            playerLogin.runTaskLater(Main.instance, timeout);
            customEventHandler.openInv((Player) sender, title);
        }
        return true;
    }

    public String permission(){
        return "messagesync.ms.login";
    }
}

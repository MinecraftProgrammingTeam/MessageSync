package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import center.xzy.qb.messagesync.commands.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class useradd extends ICommand {
    public useradd(){
        super("useradd", "<玩家ID> <密码>", "添加/修改玩家密码 - 登陆系统");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        try {
            String userID = args[0],
                    password = args[1];
            Statement statement = Main.dbConn.createStatement();
            ResultSet rs = statement.executeQuery("select * from `ms_users` where `id`='" + userID + "'");
            boolean flag = false;
            while(rs.next()) {
                flag = true;
            }

            if (!flag) {
                // 新建
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String dateString = sdf.format(date);

                statement.executeUpdate("INSERT INTO `ms_users` (`password`, `id`, `time`) VALUES ('" + Main.MD5(password) + "', '" + userID + "', '" + dateString + "')");
                sender.sendMessage(ChatColor.GREEN + "已新建用户！");
            } else {
                // 已存在
                statement.executeUpdate("UPDATE `ms_users` SET `password`='" + Main.MD5(password) + "' WHERE `id`='" + userID + "'");
                sender.sendMessage(ChatColor.GREEN + "已修改密码！");
            }
        } catch (SQLException e) {
            Main.instance.getLogger().warning(e.getMessage());
            return false;
        }
        return true;
    }

    public String permission(){
        return "messagesync.ms.useradd";
    }
}

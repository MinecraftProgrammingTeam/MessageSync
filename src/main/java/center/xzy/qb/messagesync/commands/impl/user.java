package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import center.xzy.qb.messagesync.commands.ICommand;
import org.sqlite.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class user extends ICommand {
    public user(){
        super("user", "<玩家ID> <密码>", "添加/修改玩家密码 - 登陆系统");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        try {
            String userID = args[0],
                    password = args[1];
            Statement statement = Main.dbConn.createStatement();
            ResultSet rs = statement.executeQuery("select * from `password` where `id`='" + userID + "'");
            boolean flag = false;
            while(rs.next()) {
                flag = true;
            }

            if (!flag) {
                // 新建
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String dateString = sdf.format(date);

                statement.executeUpdate("INSERT `password` (`password`, `id`, `time`) VALUES ('" + Main.MD5(password) + "', '" + userID + "', '" + dateString + "')");
                sender.sendMessage(ChatColor.GREEN + "已新建用户！");
            } else {
                // 已存在
                statement.executeUpdate("UPDATE `password` SET `password`='" + Main.MD5(password) + "' WHERE `id`='" + userID + "'");
                sender.sendMessage(ChatColor.GREEN + "已修改密码！");
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public String permission(){
        return "messagesync.ms.user";
    }
}

package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import center.xzy.qb.messagesync.commands.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class userlist extends ICommand {
    public userlist(){
        super("userlist", "", "列出玩家 - 登陆系统");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        try {
            Statement statement = Main.dbConn.createStatement();
            ResultSet rs = statement.executeQuery("select * from `ms_users`");
            while(rs.next()) {
                sender.sendMessage(ChatColor.BLUE + "> 玩家ID: " + rs.getString("id"));
                sender.sendMessage(ChatColor.BLUE + "  注册时间: " + rs.getString("time"));
                sender.sendMessage(ChatColor.BLUE + "  注册IP: " + rs.getString("ip"));
            }

        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public String permission(){
        return "messagesync.ms.userlist";
    }
}

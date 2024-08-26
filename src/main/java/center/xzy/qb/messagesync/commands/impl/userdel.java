package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import center.xzy.qb.messagesync.commands.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.sql.Statement;

public class userdel extends ICommand {
    public userdel(){
        super("userdel", "<玩家ID>", "删除玩家信息 - 登陆系统");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        try {
            String userID = args[0];
            Statement statement = Main.dbConn.createStatement();
            statement.executeUpdate("DELETE FROM `ms_users` WHERE `id`='" + userID + "'");
            sender.sendMessage(ChatColor.GREEN + "已删除用户！");
        } catch (SQLException e) {
            Main.instance.getLogger().warning(e.getMessage());
            return false;
        }
        return true;
    }

    public String permission(){
        return "messagesync.ms.userdel";
    }
}

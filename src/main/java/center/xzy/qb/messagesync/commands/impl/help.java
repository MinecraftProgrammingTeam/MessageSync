package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import center.xzy.qb.messagesync.commands.ICommand;

public class help extends ICommand {
    public help(){
        super("disable", "", "启用插件");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.BLUE + "Help!!!");
        return true;
    }

    public String permission(){
        return "messagesync.disable";
    }
}

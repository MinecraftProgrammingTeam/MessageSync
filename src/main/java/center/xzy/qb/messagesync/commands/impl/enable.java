package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import center.xzy.qb.messagesync.commands.ICommand;

public class enable extends ICommand {
    public enable(){
        super("enable", "", "启用插件");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        Main.pluginStatus = true;
        Main.instance.getLogger().info(ChatColor.GREEN + "Enabled Message Sync for PBF!");
        sender.sendMessage(ChatColor.GREEN + "Enabled Message Sync for PBF!");

        return true;
    }

    public String permission(){
        return "messagesync.enable";
    }
}

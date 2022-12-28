package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import center.xzy.qb.messagesync.commands.ICommand;

public class disable extends ICommand {
    public disable(){
        super("disable", "", "启用插件");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        Main.pluginStatus = false;
        Main.instance.getLogger().info(ChatColor.RED + "Disabled Message Sync for PBF!");
        sender.sendMessage(ChatColor.RED + "Disabled Message Sync for PBF!");
        return true;
}

    public String permission(){
        return "messagesync.disable";
    }
}

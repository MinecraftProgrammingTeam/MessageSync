package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import center.xzy.qb.messagesync.commands.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class say extends ICommand {
    public say(){
        super("say", "<内容>", "说");
    }
    Plugin plugin = Main.getPlugin(Main.class);

    public boolean onCommand(CommandSender sender, String[] args) {
        StringBuilder msg = new StringBuilder();
        for (String arg : args) {
            msg.append(arg).append(" ");
        }
        if (sender instanceof Player){
            Main.instance.getServer().broadcastMessage(ChatColor.YELLOW + "<" + sender.getName() + "> " + ChatColor.RESET + msg);
        }else {
            Main.instance.getServer().broadcastMessage(ChatColor.YELLOW + plugin.getConfig().getString("say-prefix") + " " + ChatColor.RESET + msg);
        }
        return true;
    }

    public String permission(){
        return "messagesync.ms.say";
    }
}

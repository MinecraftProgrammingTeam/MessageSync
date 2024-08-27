package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import center.xzy.qb.messagesync.commands.ICommand;
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
            Main.instance.getServer().broadcastMessage("<" + sender.getName() + "> " + msg);
        }else {
            Main.instance.getServer().broadcastMessage(plugin.getConfig().getString("say-prefix") + " " + msg);
        }
        return true;
    }

    public String permission(){
        return "messagesync.ms.say";
    }
}

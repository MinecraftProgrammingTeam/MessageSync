package center.xzy.qb.messagesync.executor;

import center.xzy.qb.messagesync.Main;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

public class sayHandler implements CommandExecutor {
    Plugin plugin = Main.getPlugin(Main.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String msg = "";
        for (int i = 0; i < args.length; i++) {
            msg += args[i] +" ";
        }
        if (sender instanceof Player){
            Bukkit.broadcastMessage("<" + sender.getName() + "> " + msg);
        }else {
            Bukkit.broadcastMessage(plugin.getConfig().getString("say-prefix") + " " + msg);
        }
        return true;
    }
}
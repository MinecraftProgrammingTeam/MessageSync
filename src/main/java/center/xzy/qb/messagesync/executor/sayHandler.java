package center.xzy.qb.messagesync.executor;

import center.xzy.qb.messagesync.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class sayHandler implements CommandExecutor {
    Plugin plugin = Main.getPlugin(Main.class);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
}
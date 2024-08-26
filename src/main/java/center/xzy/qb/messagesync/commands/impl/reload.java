package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import center.xzy.qb.messagesync.commands.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class reload extends ICommand {
    public reload(){
        super("reload", "", "重载配置");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        Main.instance.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "重载配置成功！");
        return true;
    }

    public String permission(){
        return "messagesync.ms.reload";
    }
}

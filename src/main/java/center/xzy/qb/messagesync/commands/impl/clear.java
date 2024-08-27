package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import center.xzy.qb.messagesync.commands.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class clear extends ICommand {
    public clear(){
        super("clear", "", "清除登录信息");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        Main.instance.getLogger().warning(ChatColor.RED + "请注意，在服务器正常运行时执行该指令可能导致玩家登录/注册出现未知异常！");
        Main.regData.clear();
        Main.regIpData.clear();
        Main.gmData.clear();
        Main.LoginData.clear();
        Main.instance.getLogger().info(ChatColor.GREEN + "清除登录信息成功！");
        return true;
    }

    public String permission(){
        return "messagesync.ms.clear";
    }
}

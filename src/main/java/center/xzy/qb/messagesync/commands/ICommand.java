package center.xzy.qb.messagesync.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;


public abstract class ICommand {

    /**
     * 子指令名
     */
    private final String cmdName;

    /**
     * 子指令参数
     */
    private String params;

    /**
     * 子指令描述
     */
    private String info;

    public ICommand(String cmdName) {
        this.cmdName = cmdName;
    }

    public ICommand(String cmdName, String params,String usage) {
        this.cmdName = cmdName;
        this.info = usage;
        this.params = params;
    }

    public String getCmdName() {
        return cmdName;
    }
    public String getParams() {
        return params;
    }


    public String showUsage() {
        return "/ms " + ChatColor.AQUA + cmdName + " "+ params + ChatColor.WHITE + " -- " + ChatColor.GREEN + info;
    }
    /**
     * 指令内容
     * @param sender    发送者
     * @param args      参数
     * @return          是否执行成功
     */
    public abstract boolean onCommand(CommandSender sender, String[] args);

    /**
     * 指令权限
     * @return        权限
     */
    public abstract String permission();
}
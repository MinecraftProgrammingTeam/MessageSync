package center.xzy.qb.messagesync.executor;

import center.xzy.qb.messagesync.commands.ICommand;
import center.xzy.qb.messagesync.commands.impl.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 子指令处理器
 */
public class CommandHandler implements TabExecutor {
    private static CommandHandler instance;

    /**
     * 维护的指令集合
     */
    private final Map<String, ICommand> commands = new HashMap<>();

    public Map<String, ICommand> getCommands() {
        return commands;
    }

    /**
     * handler初始化构造器
     */
    public CommandHandler() {
        instance = this;
        initHandler();
    }

    public static CommandHandler getInstance() {
        return instance;
    }

    /**
     * 初始化指令集
     * 注意要使用小写，与发送者的指令进行匹配
     */
    private void initHandler() {
        /*
        Reflections reflections = new Reflections("center.xzy.qb.messagesync.commands.impl");
        Set<Class<? extends ICommand>> subTypesOf = reflections.getSubTypesOf(ICommand.class);
//        Main.instance.getLogger().warning(subTypesOf.toArray().toString());
        subTypesOf.forEach(aClass -> {
            try {
                ICommand command = aClass.newInstance();
                registerCommand(command);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        */

        registerCommand(new enable());
        registerCommand(new disable());
        registerCommand(new useradd());
        registerCommand(new userdel());
        registerCommand(new userlist());
        registerCommand(new login());
        registerCommand(new say());
        registerCommand(new reload());
        registerCommand(new clear());
        registerCommand(new reconnect());
    }

    /**
     * 手动注册指令
     * @param command 指令
     */
    public void registerCommand(ICommand command) {
        //command.setHandler(this);
        commands.put(command.getCmdName(), command);
    }

    /**
     * 使用帮助指令
     * @param sender 发送者
     */
    public void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + "MessageSync for PBF " + ChatColor.GREEN + "插件帮助");
        for (String key: commands.keySet()) {
            sender.sendMessage(commands.get(key).showUsage());
        }
    }

    /**
     * 统一返回true，使用自定义的showHelp()方法。
     * @param sender 发送者
     * @param command 指令
     * @param label 标签
     * @param args 参数
     * @return true
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args == null || args.length < 1) {
            showHelp(sender);
            return true;
        }

        //mc输入的文字区分大小写
        ICommand cmd = commands.get(args[0].toLowerCase());
        try {
            if (cmd != null && sender.hasPermission(cmd.permission())) {
                //指令参数
                String[] params = new String[0];
                if (args.length >= 2) {
                    //用链表的removeFirst，删掉第指令，得到参数
                    LinkedList<String> list = new LinkedList<>(Arrays.asList(args));
                    list.removeFirst();
                    params = list.toArray(new String[0]);
                }
                boolean res = cmd.onCommand(sender, params);
                if (!res) {
                    //使用 cmd 自身的说明，而非调用 showHelp()
                    sender.sendMessage(cmd.showUsage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "发生了异常：" + e.getMessage());
            return true;
        }
        return true;
    }

    /**
     * 玩家每输入一个字母都会被服务器响应
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args == null || args.length < 1) {
            showHelp(sender);
            return null;
        }

        List<String> result = new ArrayList<>();
        //正在输第一个指令，如 /sub god...
        if (args.length == 1) {
            String typingStr = args[0].toLowerCase();
            for (String cmdName : commands.keySet()) {
                /*
                 * 如果正在输入的字母是正确指令的前缀，且玩家拥有对应指令的权限，就将指令名称拼接到结果里去
                 * 注意：这里并不是检测到一个符合就立马返回，而是返回符合前缀的指令集合
                 */
                if (cmdName.startsWith(typingStr)) {
                    ICommand cmd = commands.get(cmdName);
                    if (sender.hasPermission(cmd.permission())) {
                        result.add(cmdName);
                    }
                }
            }
        } else {
            //获取指令参数
//            String typingStr = args[1].toLowerCase();
            //得到第一个指令，查看对应参数
            ICommand cmd = commands.get(args[0].toLowerCase());

            //玩家可能会输错，找不到指令，那就不管了
            if (cmd != null) {
                String [] params = cmd.getParams().split(" ");
                if (params.length > args.length-2) {
                    String param = params[args.length - 2];
                    return Collections.singletonList(param);
                } else {
                    sender.sendMessage(cmd.showUsage());
                }
            }else {
                showHelp(sender);
                return  null;
            }
        }
        return result;
    }
}
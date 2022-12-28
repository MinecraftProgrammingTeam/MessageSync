package center.xzy.qb.messagesync.executor;

import center.xzy.qb.messagesync.Main;
import center.xzy.qb.messagesync.commands.impl.*;
import center.xzy.qb.messagesync.commands.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 子指令处理器
 */
public class CommandHandler implements TabExecutor {
    private static CommandHandler instance;

    /**
     * 维护的指令集合
     */
    private Map<String, ICommand> commands = new HashMap<>();

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
        String packageName = "center.xzy.qb.messagesync.commands.impl";

        List<String> classNames = getClassName(packageName);
        for (String className : classNames) {
            Main.instance.getLogger().info(ChatColor.YELLOW + className);
            Object classInstance = NewInstance(className);
            registerCommand((ICommand) classInstance);
        }
        registerCommand(new disable());
        registerCommand(new enable());
    }

    public Object NewInstance(String className){
        Object obj = null;
        try {
            obj = Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 手动注册指令
     * @param command
     */
    public void registerCommand(ICommand command) {
        //command.setHandler(this);
        commands.put(command.getCmdName(), command);
    }

    /**
     * 获取某包下（包括该包的所有子包）所有类
     * @param packageName 包名
     * @return 类的完整名称
     */
    public static List<String> getClassName(String packageName) {
        return getClassName(packageName, true);
    }

    /**
     * 获取某包下所有类
     * @param packageName 包名
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    public static List<String> getClassName(String packageName, boolean childPackage) {
        List<String> fileNames = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();
            if (type.equals("file")) {
                fileNames = getClassNameByFile(url.getPath(), null, childPackage);
            } else if (type.equals("jar")) {
                fileNames = getClassNameByJar(url.getPath(), childPackage);
            }
        } else {
            fileNames = getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage);
        }
        return fileNames;
    }

    /**
     * 从项目文件获取某包下所有类
     * @param filePath 文件路径
     * @param className 类名集合
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private static List<String> getClassNameByFile(String filePath, List<String> className, boolean childPackage) {
        List<String> myClassName = new ArrayList<String>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), myClassName, childPackage));
                }
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("\\", ".");
                    myClassName.add(childFilePath);
                }
            }
        }

        return myClassName;
    }

    /**
     * 从jar获取某包下所有类
     * @param jarPath jar文件路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private static List<String> getClassNameByJar(String jarPath, boolean childPackage) {
        List<String> myClassName = new ArrayList<String>();
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        String packagePath = jarInfo[1].substring(1);
        try {
            JarFile jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    if (childPackage) {
                        if (entryName.startsWith(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                            myClassName.add(entryName);
                        }
                    } else {
                        int index = entryName.lastIndexOf("/");
                        String myPackagePath;
                        if (index != -1) {
                            myPackagePath = entryName.substring(0, index);
                        } else {
                            myPackagePath = entryName;
                        }
                        if (myPackagePath.equals(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                            myClassName.add(entryName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myClassName;
    }

    /**
     * 从所有jar中搜索该包，并获取该包下所有类
     * @param urls URL集合
     * @param packagePath 包路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private static List<String> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage) {
        List<String> myClassName = new ArrayList<String>();
        if (urls != null) {
            for (int i = 0; i < urls.length; i++) {
                URL url = urls[i];
                String urlPath = url.getPath();
                // 不必搜索classes文件夹
                if (urlPath.endsWith("classes/")) {
                    continue;
                }
                String jarPath = urlPath + "!/" + packagePath;
                myClassName.addAll(getClassNameByJar(jarPath, childPackage));
            }
        }
        return myClassName;
    }

    /**
     * 使用帮助指令
     * @param sender
     */
    public void showHelp(CommandSender sender) {
        new help().onCommand(sender, new String[0]);
    }

    /**
     * 统一返回true，使用自定义的showHelp()方法。
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /**
         *  假设输入的是=》 /sub hello senko ，那么得到的args=> [hello, senko]，而我们需要调用的command就是hello,参数为senko。
         *  判断< 1是防止 args.length > Integer.MAX_SIZE导致变成负数
         */
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
                    params = list.toArray(new String[list.size()]);
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
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args == null || args.length < 1) {
            showHelp(sender);
            return null;
        }

        List<String> result = new ArrayList<>();
        //正在输第一个指令，如 /sub god...
        if (args.length == 1) {
            String typingStr = args[0].toLowerCase();
            for (String cmdName : commands.keySet()) {
                /**
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
        } else if (args.length > 1) {
            //获取指令参数
            String typingStr = args[1].toLowerCase();
            //得到第一个指令，查看对应参数
            ICommand cmd = commands.get(args[0].toLowerCase());

            //玩家可能会输错，找不到指令，那就不管了
            if (cmd != null) {
                sender.sendMessage(cmd.showUsage());
            }else {
                showHelp(sender);
                return  null;
            }

        }
        return result;
    }
}
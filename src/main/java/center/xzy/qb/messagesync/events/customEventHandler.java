package center.xzy.qb.messagesync.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.plugin.Plugin;
import center.xzy.qb.messagesync.Main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class customEventHandler implements Listener {
    Plugin plugin = Main.getPlugin(Main.class);
    @EventHandler
    public void PlayerChat(AsyncPlayerChatEvent event) {
        sendRequest("<" + event.getPlayer().getName() + "> " + event.getMessage());
    }

    @EventHandler
    public void PlayerLogin(PlayerJoinEvent event) {
//        String Message = "玩家<" + event.getName() + ">已从" + event.getAddress() + "登录服务器";
        sendRequest(event.getJoinMessage());
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event) {
        sendRequest(event.getQuitMessage());
    }

    @EventHandler
    public void PlayerRespawn(PlayerRespawnEvent event) {
        sendRequest("玩家<" + event.getPlayer().getName() + ">重生在了(" + event.getRespawnLocation().getX() + "," + event.getRespawnLocation().getY() + "," + event.getRespawnLocation().getZ() + ")");
    }

    @EventHandler
    public void EntityDeath(PlayerDeathEvent event) {
        sendRequest(event.getDeathMessage());
    }

    public String regReplace(String content,String pattern,String newString){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        return m.replaceAll(newString);
    }

    public void sendRequest(String msg) {
        if (Main.pluginStatus) {
            // 去除颜色代码
            String pattern1 = "§[a-z0-9]?";
            String pattern2 = "&[a-z0-9]?";
            msg = regReplace(msg, pattern1, "");
            msg = regReplace(msg, pattern2, "");

            // 首先抓取异常并处理
            String returnString = "1";
            try {
                // 拼接url
                Integer qn = plugin.getConfig().getInt("qn");
                String uuid = plugin.getConfig().getString("uuid");
                String urlP = plugin.getConfig().getString("url") + "/MCServer?msg=" + URLEncoder.encode(msg, "UTF-8") + "&qn=" + qn + "&uuid=" + uuid;

                // 1  创建URL对象,接收用户传递访问地址对象链接
                URL url = new URL(urlP);

                // 2 打开用户传递URL参数地址
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                connect.setRequestProperty("User-agent", "	Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");

                // 3 设置HTTP请求的一些参数信息
                connect.setRequestMethod("GET"); // 参数必须大写
                connect.connect();


                // 4 获取URL请求到的数据，并创建数据流接收
                InputStream isString = connect.getInputStream();

                // 5 构建一个字符流缓冲对象,承载URL读取到的数据
                BufferedReader isRead = new BufferedReader(new InputStreamReader(isString));

                // 6 输出打印获取到的文件流
                String str = "";
                while ((str = isRead.readLine()) != null) {
                    str = new String(str.getBytes(), "UTF-8"); //解决中文乱码问题
                    //          System.out.println("文件解析打印：");
                    //          System.out.println(str);
                    returnString = str;
                }

                // 7 关闭流
                isString.close();
                connect.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

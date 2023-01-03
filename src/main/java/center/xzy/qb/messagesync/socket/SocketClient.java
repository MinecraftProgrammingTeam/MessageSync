package center.xzy.qb.messagesync.socket;

import java.net.URI;
import java.net.URISyntaxException;

import com.alibaba.fastjson.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import center.xzy.qb.messagesync.Main;

public class SocketClient extends WebSocketClient{
    Plugin plugin = Main.getPlugin(Main.class);

    public SocketClient(String url) throws URISyntaxException {
        super(new URI(url));
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onOpen(ServerHandshake shake) {
        // 发送init请求
        send("{\"type\":\"init\",\"client_id\":\"" +plugin.getConfig().getString("socket-client_id")+ "\",\"client_secret\":\"" +plugin.getConfig().getString("socket-client_secret")+ "\",\"data\":{}}");
    }

    @Override
    public void onMessage(String paramString) {
        // TODO Auto-generated method stub
        Main.instance.getLogger().warning(ChatColor.BLUE + "WebSocket收到消息：" + paramString);
        JSONObject object = JSONObject.parseObject(paramString);
        switch (object.getString("type")){
            case "command":
                Bukkit.getScheduler().runTask(Main.instance, () -> {
                    Main.instance.getServer().dispatchCommand(Main.instance.getServer().getConsoleSender(), object.getJSONObject("data").getString("cmd"));
                });
                break;

            case "message":
                Main.instance.getServer().broadcastMessage(plugin.getConfig().getString("say-prefix") + " " + object.getJSONObject("data").getString("msg"));
                break;
        }
    }

    @Override
    public void onClose(int paramInt, String paramString, boolean paramBoolean) {
        // TODO Auto-generated method stub
        Main.instance.getLogger().warning(ChatColor.RED + "WebSocket被关闭：" + paramString);
    }

    @Override
    public void onError(Exception e) {
        // TODO Auto-generated method stub
        Main.instance.getLogger().warning(ChatColor.RED + "WebSocket异常：" + e.getMessage());
    }
}
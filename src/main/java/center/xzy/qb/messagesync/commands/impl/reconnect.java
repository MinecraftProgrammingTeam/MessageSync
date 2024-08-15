package center.xzy.qb.messagesync.commands.impl;

import center.xzy.qb.messagesync.Main;
import center.xzy.qb.messagesync.commands.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class reconnect extends ICommand {
    public reconnect(){
        super("reconnect", "", "重连WebSocket服务器");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        Main.instance.getLogger().info(ChatColor.GREEN + "Reconnecting to WebSocket server...");
        sender.sendMessage(ChatColor.GREEN + "Reconnecting to WebSocket server...");
        try{
            Main.socket.close();
        }catch (Exception e){
            Main.instance.getLogger().info(ChatColor.RED + "Failed to close WebSocket connection!");
            sender.sendMessage(ChatColor.RED + "Failed to close WebSocket connection!");
            Main.reconnectSocket();
            sender.sendMessage(ChatColor.GREEN + "Reconnected to WebSocket server!");
        }
        return true;
    }

    public String permission(){
        return "messagesync.ms.disable";
    }
}

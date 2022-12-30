package center.xzy.qb.messagesync.scheduler;

import center.xzy.qb.messagesync.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerLogin extends BukkitRunnable{
    public Player player;
    public void setPlayer(Player p) {
        player = p;
    }

    @Override
    public void run() {
        if (Main.regData.containsKey(player.getName())){
            player.kickPlayer(Main.instance.getConfig().getString("reg-allow-tick-msg"));
        } else if (Main.LoginData.containsKey(player.getName())) {
            player.kickPlayer(Main.instance.getConfig().getString("login-allow-tick-msg"));
        }
    }
}

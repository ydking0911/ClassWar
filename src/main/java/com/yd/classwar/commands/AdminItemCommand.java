package com.yd.classwar.commands;

import com.yd.classwar.ClassWar;
import com.yd.classwar.gui.AdminItemGUI;
import com.yd.classwar.managers.RankManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminItemCommand implements CommandExecutor {
    private ClassWar plugin;
    private RankManager rankManager;

    public AdminItemCommand(ClassWar plugin, RankManager rankManager) {
        this.plugin = plugin;
        this.rankManager = rankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){sender.sendMessage("플레이어만 사용가능");return true;}
        Player p = (Player)sender;
        if(!rankManager.isAdmin(p)) {
            p.sendMessage("운영자만 사용할 수 있습니다.");
            return true;
        }
        AdminItemGUI.openAdminItemGUI(p);
        return true;
    }
}
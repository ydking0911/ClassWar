package com.yd.classwar.commands;

import com.yd.classwar.ClassWar;
import com.yd.classwar.gui.RankSettingsGUI;
import com.yd.classwar.managers.RankManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankSettingsCommand implements CommandExecutor {
    private RankManager rankManager;
    private ClassWar plugin;

    public RankSettingsCommand(ClassWar plugin, RankManager rankManager) {
        this.rankManager = rankManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){sender.sendMessage("플레이어만 사용가능");return true;}
        Player p = (Player) sender;
        if(!rankManager.isAdmin(p)) {
            p.sendMessage("운영자만 사용가능합니다.");
            return true;
        }
        RankSettingsGUI.openRankSettingsGUI(plugin, p, rankManager);
        return true;
    }
}
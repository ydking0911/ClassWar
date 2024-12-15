package com.yd.classwar.commands;

import com.yd.classwar.managers.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartGameCommand implements CommandExecutor {
    private GameManager gameManager;

    public StartGameCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /등급전쟁 시작 -> startGame
        // /등급전쟁 끝 -> endGame
        if (args.length > 0) {
            if(args[0].equalsIgnoreCase("시작")) {
                gameManager.startGame();
                sender.sendMessage("게임을 시작하였습니다.");
                return true;
            } else if(args[0].equalsIgnoreCase("끝")) {
                gameManager.endGame();
                sender.sendMessage("게임을 종료하였습니다.");
                return true;
            }
        }
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.WHITE + "=======" + ChatColor.YELLOW + " 등급전쟁 (Made by Moody) " + ChatColor.WHITE + "=======");
        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.YELLOW + "/등급전쟁 시작" + ChatColor.WHITE + " : 등급전쟁을 시작합니다.");
        sender.sendMessage(ChatColor.YELLOW + "/등급전쟁 끝" + ChatColor.WHITE + " : 등급전쟁을 종료합니다.");
        sender.sendMessage(ChatColor.YELLOW + "/운영자" + ChatColor.WHITE + " : 운영자모드로 전환합니다. 해당 모드인 상태에서 등급설정이 가능합니다.");
        sender.sendMessage(ChatColor.YELLOW + "/참가자" + ChatColor.WHITE + " : 참가자 모드로 전환합니다. 해당 상태에서만 게임에 참가할 수 있습니다.");
        sender.sendMessage(ChatColor.YELLOW + "/등급설정" + ChatColor.WHITE + " : 등급 색상의 on/off를 설정합니다.");
        sender.sendMessage(ChatColor.YELLOW + "/등급교환" + ChatColor.WHITE + " : 상점을 오픈합니다. (SHIFT + 손바꾸기 키)");
        sender.sendMessage(ChatColor.YELLOW + "/__상점" + ChatColor.WHITE + " : 해당 상점 GUI가 뜨며, 내부 아이템을 클릭 시 제거 가능합니다. (체력상점/무기상점/마나상점)");
        sender.sendMessage(ChatColor.YELLOW + "/__상점 설정 [가격] [개수]" + ChatColor.WHITE + " : 해당 상점에 등록할 아이템을 손에 들고 사용하면 해당 상점에 등록됩니다. (체력상점/무기상점/마나상점)");
        sender.sendMessage(" ");
        sender.sendMessage("====================================");

        return true;
    }
}

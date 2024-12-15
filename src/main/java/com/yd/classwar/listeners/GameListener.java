package com.yd.classwar.listeners;

import com.yd.classwar.ClassWar;
import com.yd.classwar.items.SpecialItems;
import com.yd.classwar.managers.GameManager;
import com.yd.classwar.managers.PointManager;
import com.yd.classwar.managers.RankManager;
import com.yd.classwar.utils.Rank;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GameListener implements Listener {
    private GameManager gameManager;
    private RankManager rankManager;
    private PointManager pointManager;
    private ClassWar plugin;

    public GameListener(ClassWar plugin, GameManager gameManager, RankManager rankManager, PointManager pointManager) {
        this.gameManager = gameManager;
        this.rankManager = rankManager;
        this.pointManager = pointManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!gameManager.isGameStarted()) return;
        // 게임 시작하지 않아도 아이템 사용 가능하게 하려면 조건 제거

        Player p = e.getPlayer();
        if(e.getItem() == null || e.getItem().getType() == Material.AIR) return;

        ItemStack item = e.getItem();
        if(!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        String name = item.getItemMeta().getDisplayName();

        // 아이템 이름 비교 후 해당 효과 적용
        if(name.equals(SpecialItems.RANK_UP)) {
            SpecialItems.useRankUp(p, rankManager);
            consumeItem(p, item);
        } else if(name.equals(SpecialItems.RANDOM_RANK_ALL_NAME)) {
            SpecialItems.useRandomRankAll(rankManager);
            consumeItem(p, item);
        } else if(name.equals(SpecialItems.RANDOM_RANK_DOWN_OTHER_NAME)) {
            SpecialItems.useRandomRankDownOther(p, rankManager);
            consumeItem(p, item);
        } else if(name.equals(SpecialItems.RANDOM_PLAYER_RANK_CHANGE_NAME)) {
            SpecialItems.useRandomChangeOther(p, rankManager);
            consumeItem(p, item);
        } else if(name.equals(SpecialItems.NEAR_PLAYER_RANK_HINT_NAME)) {
            SpecialItems.useNearPlayerRankHint(p, rankManager);
            consumeItem(p, item);
        } else if(name.equals(SpecialItems.RANDOM_TELEPORT_SWAP_NAME)) {
            SpecialItems.useRandomTeleportSwap(p, rankManager);
            consumeItem(p, item);
        } else if(name.equals(SpecialItems.RANDOM_SUMMON_NAME)) {
            SpecialItems.useRandomSummon(p, rankManager);
            consumeItem(p, item);
        } else if(name.equals(SpecialItems.POINT_INTEREST_UP_NAME)) {
            SpecialItems.usePointInterestUp(p, pointManager);
            consumeItem(p, item);
        } else if(name.equals(SpecialItems.POINT_PLUS_ONE_NAME)) {
            SpecialItems.usePointPlusOne(p, pointManager);
            consumeItem(p, item);
        }
    }

    private void consumeItem(Player p, ItemStack item) {
        // 아이템 1개 소모
        int amt = item.getAmount();
        if(amt > 1) {
            item.setAmount(amt-1);
        } else {
            p.getInventory().removeItem(item);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (!gameManager.isGameStarted()) return;
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player attacker = (Player)e.getDamager();
            Player victim = (Player)e.getEntity();

            if(rankManager.isAdmin(attacker) || rankManager.isAdmin(victim)) {
                return; // 어드민은 영향을 받지 않는다.
            }

            Rank attackerRank = rankManager.getPlayerRank(attacker);
            Rank victimRank = rankManager.getPlayerRank(victim);
            if(attackerRank == null || victimRank == null) return;

            // 킬 판정
            if(e.getFinalDamage() >= victim.getHealth()) {
                // 등급 비교
                if(victimRank.getOrder() < attackerRank.getOrder()) {
                    // 정상 킬
                    victim.setHealth(0);
                    victim.setGameMode(GameMode.SPECTATOR);
                } else {
                    // 잘못된 살인: 공격자 역사
                    e.setCancelled(true);
                    victim.setHealth(20);
                    attacker.setHealth(0);
                    attacker.setGameMode(GameMode.SPECTATOR);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        // 죽은 플레이어는 관전자로 바뀌었다고 가정 (onPlayerDamage 내에서 처리)
    }
}
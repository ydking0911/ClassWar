package com.yd.classwar.managers;

import com.yd.classwar.ClassWar;
import com.yd.classwar.utils.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class RankManager {
    private ClassWar plugin;
    private Set<UUID> admins = new HashSet<>();
    private Set<UUID> participants = new HashSet<>();
    private Map<UUID, Rank> playerRanks = new HashMap<>();
    private Map<Rank, Boolean> rankEnabled = new HashMap<>();

    private ScoreboardManager scoreboardManager;
    private Scoreboard globalScoreboard;  // 모든 플레이어가 공유하는 글로벌 스코어보드
    private Objective objective;

    private Team teamPoints;    // 더 이상 개인별로 사용하지 않음 (사용X)
    private Team teamInterest;  // 더 이상 개인별로 사용하지 않음 (사용X)
    private Team teamAlive;     // 더 이상 개인별로 사용하지 않음 (사용X)

    // 개인 정보를 표시하던 라인을 제거하고, 대신 액션바나 탭 목록으로 표기
    // 여기서는 탭 목록 하단(Footer)에 표시할 것.

    private PointManager pointManager;

    public RankManager(ClassWar plugin) {
        this.plugin = plugin;
        for (Rank r : Rank.values()) {
            rankEnabled.put(r, true);
        }

        scoreboardManager = Bukkit.getScoreboardManager();
        globalScoreboard = scoreboardManager.getNewScoreboard();

        for(Rank r : Rank.values()) {
            Team t = globalScoreboard.registerNewTeam(r.name());
            t.setColor(r.getColor());
            t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }

        objective = globalScoreboard.registerNewObjective("classwar", "dummy", ChatColor.GOLD + "등급전쟁");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // 글로벌 Scoreboard에 공통 정보 표시 (예: 전체 남은 인원 수)
        // 이제 개인별 정보(포인트, 이자) 표시를 여기서 하지 않음.
        objective.getScore(ChatColor.YELLOW + "남은 인원 :").setScore(1);
    }

    public void setPointManager(PointManager pointManager) {
        this.pointManager = pointManager;
    }

    public void setAdminMode(Player p, boolean admin) {
        if (admin) {
            admins.add(p.getUniqueId());
            participants.remove(p.getUniqueId());
            playerRanks.remove(p.getUniqueId());
            p.setDisplayName(ChatColor.GRAY + "[Admin]" + p.getName());
            removeFromAllTeams(p);
            p.setPlayerListName(ChatColor.WHITE + p.getName());
        } else {
            admins.remove(p.getUniqueId());
            participants.add(p.getUniqueId());
        }
    }

    public boolean isAdmin(Player p) {
        return admins.contains(p.getUniqueId());
    }

    public void setParticipant(Player p) {
        admins.remove(p.getUniqueId());
        participants.add(p.getUniqueId());
    }

    public boolean isParticipant(Player p) {
        return participants.contains(p.getUniqueId());
    }

    public void assignRandomRanks() {
        List<Rank> enabledRanks = new ArrayList<>();
        for (Rank r : Rank.values()) {
            if (rankEnabled.getOrDefault(r, true)) {
                enabledRanks.add(r);
            }
        }

        Random random = new Random();
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (!isAdmin(pl) && isParticipant(pl)) {
                Rank randomRank = enabledRanks.get(random.nextInt(enabledRanks.size()));
                playerRanks.put(pl.getUniqueId(), randomRank);
                assignPlayerToRankTeam(pl, randomRank);
                pl.setPlayerListName(ChatColor.WHITE + pl.getName());
                pl.setScoreboard(globalScoreboard);
            }
        }

        // 모든 플레이어에게 글로벌 Scoreboard 적용
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(globalScoreboard);
        }
    }

    public Rank getPlayerRank(Player p) {
        return playerRanks.get(p.getUniqueId());
    }

    public void setRankEnabled(Rank rank, boolean enabled) {
        rankEnabled.put(rank, enabled);
    }

    public boolean isRankEnabled(Rank rank) {
        return rankEnabled.getOrDefault(rank, true);
    }

    public void setPlayerRank(Player p, Rank r) {
        playerRanks.put(p.getUniqueId(), r);
        assignPlayerToRankTeam(p, r);
        p.setPlayerListName(ChatColor.WHITE + p.getName());
    }

    public Rank getRandomEnabledRank() {
        List<Rank> enabledRanks = new ArrayList<>();
        for (Rank r : Rank.values()) {
            if (isRankEnabled(r)) enabledRanks.add(r);
        }
        if(enabledRanks.isEmpty()) {
            return Rank.RED;
        }
        return enabledRanks.get(new Random().nextInt(enabledRanks.size()));
    }

    private void assignPlayerToRankTeam(Player p, Rank rank) {
        removeFromAllTeams(p);
        Team t = globalScoreboard.getTeam(rank.name());
        if(t!=null && !t.hasEntry(p.getName())) {
            t.addEntry(p.getName());
        }
        p.setScoreboard(globalScoreboard);
    }

    private void removeFromAllTeams(Player p) {
        for(Team team : globalScoreboard.getTeams()) {
            if(team.hasEntry(p.getName())) {
                team.removeEntry(p.getName());
            }
        }
    }

    public void resetAllPlayersTeam() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            removeFromAllTeams(p);
            p.setPlayerListName(p.getName());
        }
    }

    public void clearRanks() {
        playerRanks.clear();
    }

    /**
     * 이제 개인별로 Sidebar는 사용하지 않고,
     * 개인별 포인트, 이자, 남은 인원 수는 탭 목록 Footer로 표시.
     */
    public void updatePersonalInfo(Player p) {
        if (pointManager == null) return;

        int points = pointManager.getPoints(p);
        int interest = pointManager.getInterest(p);
        int alive = pointManager.getAlivePlayersCount();

        String footer = ChatColor.YELLOW + "포인트: " + ChatColor.WHITE + points +
                ChatColor.YELLOW + " | 이자: " + ChatColor.WHITE + interest +
                ChatColor.YELLOW + " | 남은인원: " + ChatColor.WHITE + alive;

        p.setPlayerListFooter(footer);
    }

    /**
     * 전역 Scoreboard는 남은 인원 수 등의 글로벌 정보만 표시할 수 있음.
     * 필요하다면 startGlobalUpdateTask 같은 것을 돌려 전체 인원수만 업데이트
     */
    public void updateGlobalInfo() {
        int alive = 0;
        if (pointManager != null) {
            alive = pointManager.getAlivePlayersCount();
        }
        // globalScoreboard sidebar에 남은 인원만 표시 (예: 등급전쟁 상단 타이틀 밑에)
        objective.getScore(ChatColor.YELLOW + "남은 인원 :").setScore(alive);
    }
}
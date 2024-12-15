package com.yd.classwar.gui;

import com.yd.classwar.ClassWar;
import com.yd.classwar.managers.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import com.yd.classwar.utils.ItemUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class PlayerShopGUI {
    public static final String TITLE = "플레이어 상점";

    public static void openSpecificShopGUI(Player p, ShopManager shopManager, String shopType) {
        Inventory inv = Bukkit.createInventory(null, 54, shopType+" 상점");
        List<ShopManager.ItemInfo> items = shopManager.getShopItems(shopType);
        for (int i=0; i<items.size() && i<54; i++) {
            inv.setItem(i, items.get(i).itemStack.clone());
        }
        p.openInventory(inv);
    }


}
package com.github.tommyt0mmy.firefighter.model;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FireFighterHelmet {

    ItemStack item;
    int dur = 1;


    public FireFighterHelmet(ItemStack item, int dur) {
        this.item = item;
        this.dur = dur;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getDur() {
        return dur;
    }

    public void setDur(int dur) {
        this.dur = dur;
    }

    public boolean isFireHelmet(ItemStack targetItem){
        if (targetItem.getType() != item.getType()) return false;
        ItemMeta targetMeta = targetItem.getItemMeta();
        if (targetMeta.getCustomModelData() != item.getItemMeta().getCustomModelData()) return false;
        if (!targetMeta.hasDisplayName()) return false;
        if (!ChatColor.stripColor(targetMeta.getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(item.getItemMeta().getDisplayName()))) return false;
        return true;
    }
}

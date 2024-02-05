package de.teamholy.core.bukkit.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;

import java.lang.reflect.Field;
import java.util.*;

/* copyright by Yassino */
public class ItemBuilder {

    private static String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    private static Class<?> skullMetaClass;

    static {
        try {
            skullMetaClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftMetaSkull");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Bukkit itemMeta;
    public ItemStack itemStack;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static ItemStack buildItem(Material material, String displayname, int amount) {
        int size = amount;
        String name = displayname;
        ItemStack item = new ItemStack(material, size);
        ArrayList<String> lore2 = new ArrayList<>();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore2);
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    public ItemBuilder(Material material, int amount, int subID) {
        this.itemStack = new ItemStack(material, amount, (short) subID);
    }

    public ItemBuilder(Material material, int amount, byte subID) {
        this.itemStack = new ItemStack(material, amount, (short) subID);
    }

    public ItemBuilder setName(String displayName) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder removeLore() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta.hasLore())
            itemMeta.setLore(new ArrayList());
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setLore(List lore) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setLore(lore);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setSkullOwner(String skullOwner) {
        SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();
        skullMeta.setOwner(skullOwner);
        this.itemStack.setItemMeta((ItemMeta) skullMeta);
        return this;
    }

    public ItemBuilder setSplash() {
        Potion potion = new Potion(itemStack.getDurability());
        potion.setSplash(true);
        potion.apply(itemStack);
        return this;
    }

    public ItemBuilder setLeatherColor(Color color) {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.itemStack.getItemMeta();
        leatherArmorMeta.setColor(color);
        this.itemStack.setItemMeta((ItemMeta) leatherArmorMeta);
        return this;
    }

    public ItemBuilder setEnchantments(Enchantment enchantments, int level) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addEnchant(enchantments, level, true);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setAttributs() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setAttribut(ItemFlag itemFlag) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addItemFlags(itemFlag);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder getSkull(String url) {
        SkullMeta itemMeta = (SkullMeta) this.itemStack.getItemMeta();
        try {
            Field field = skullMetaClass.getDeclaredField("profile");
            field.setAccessible(true);
            field.set(itemMeta, getProfile(url));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        itemStack.setItemMeta((ItemMeta) itemMeta);
        return this;
    }

    public ItemBuilder setSkullMeta(String value, String signature) {
        if (this.itemStack.getType() != Material.SKULL_ITEM) {
            this.itemStack.setType(Material.SKULL_ITEM);
            this.itemStack.setDurability((short) 3);
        }
        try {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            gameProfile.getProperties().put("textures", new Property("textures", value, signature));
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
            this.itemStack.setItemMeta((ItemMeta) skullMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public ItemBuilder setBannerMeta(DyeColor baseColor, List<Pattern> patterns) {
        BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();
        itemStack.setItemMeta(bannerMeta);
        bannerMeta.setBaseColor(baseColor);
        bannerMeta.setPatterns(patterns);
        itemStack.setItemMeta(bannerMeta);
        return this;
    }

    public ItemBuilder withGlow(boolean b) {
        if (b) {
            this.itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            ItemMeta meta  = this.itemStack.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.itemStack.setItemMeta(meta);
        } else {
            this.itemStack.removeEnchantment(Enchantment.DURABILITY);
            ItemMeta meta  = this.itemStack.getItemMeta();
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.itemStack.setItemMeta(meta);
        }
        return this;
    }

    private GameProfile getProfile(String url) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        String base64 = Base64.getEncoder().encodeToString((new String("{textures:{SKIN:{url:\"" + url + "\"}}}")).getBytes());
        Property property = new Property("textures", base64);
        profile.getProperties().put("textures", property);
        return profile;
    }

    public ItemBuilder setUnbreakable() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setId(int id) {
        this.itemStack.setDurability((short)id);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }
}

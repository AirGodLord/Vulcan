package me.zeph.vulcan.vulcan;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;

import me.zeph.vulcan.listener.AbilityListener;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;



public final class Vulcan extends JavaPlugin {
	
    public static Vulcan plugin;

    @Override
    public void onEnable() {
        plugin = this;


        CoreAbility.registerPluginAbilities(plugin, "me.zeph.vulcan.abilities");
        CoreAbility.registerPluginAbilities(plugin, "me.zeph.vulcan.abilities.combos");
        this.registerListeners();

        plugin.getLogger().info("Successfully enabled Vulcan.");
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Successfully disabled Vulcan.");
    }

    public static Vulcan getInstance() {
        return plugin;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AbilityListener(), this);
    }
}
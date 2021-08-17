/*package me.zeph.vulcan.configuration;



import org.bukkit.configuration.file.FileConfiguration;

import com.projectkorra.projectkorra.configuration.ConfigManager;

import me.zeph.vulcan.vulcan.Vulcan;


public class Config {

    private static ConfigFile main;
    static Vulcan plugin;

    public Config(Vulcan plugin) {
        Config.plugin = plugin;
        main = new ConfigFile("config");
        loadConfig();
    }

    
    public static FileConfiguration getConfig() {
        return main.getConfig();
    }

    public void loadConfig() {
        FileConfiguration config = Vulcan.plugin.getConfig();
        
        	

        //Ability configuration
        
        

		config.addDefault("Flower.SeedBomb.Cooldown", Long.valueOf(1000));
		config.addDefault("Flower.SeedBomb.Range", Integer.valueOf(20));
		config.addDefault("Flower.SeedBomb.Damage", Double.valueOf(2.0D));
		config.addDefault("Flower.SeedBomb.Speed", Integer.valueOf(1));
		config.addDefault("Flower.SeedBomb.Charge", Integer.valueOf(4));
		config.addDefault("Flower.SeedBomb.Hitbox", Long.valueOf(1));
		config.addDefault("Flower.SeedBomb.ShotInterval", Long.valueOf(500));
		config.addDefault("Flower.SeedBomb.Drop", Integer.valueOf(5));
		config.addDefault("Flower.SeedBomb.DropInfo",String.valueOf("Arbitrary value, increase to increase gravity on projectile."));
		config.addDefault("Flower.SeedBomb.ExplosionPower1", Integer.valueOf(1));
		config.addDefault("Flower.SeedBomb.ExplosionPower2", Integer.valueOf(2));
		config.addDefault("Flower.SeedBomb.ExplosionPower3", Integer.valueOf(2));
		config.addDefault("Flower.SeedBomb.ExplosionPower4", Integer.valueOf(3));
		config.addDefault("Flower.SeedBomb.ExplosionPower5", Integer.valueOf(3));

		config.addDefault("Flower.LeafStorm.ChargeHitbox", Long.valueOf(2));
		config.addDefault("Flower.LeafStorm.ChargeRadius", Double.valueOf(2));
		config.addDefault("Flower.LeafStorm.Cooldown", Long.valueOf(5000));
		config.addDefault("Flower.LeafStorm.ChargeSpeed", Long.valueOf(3000));
		config.addDefault("Flower.LeafStorm.ChargeDamage", Double.valueOf(1.0D));
		config.addDefault("Flower.LeafStorm.ChargeHeight", Double.valueOf(4));
		config.addDefault("Flower.LeafStorm.ShotSpeed", Double.valueOf(1));
		config.addDefault("Flower.LeafStorm.ShotRange", Double.valueOf(20));
		config.addDefault("Flower.LeafStorm.MaxHeightFromGround", Double.valueOf(1));

		config.addDefault("Flower.SporeSpray.Cooldown", Long.valueOf(5000));
		config.addDefault("Flower.SporeSpray.Duration", Long.valueOf(5000));
		config.addDefault("Flower.SporeSpray.Range", Integer.valueOf(20));
		config.addDefault("Flower.SporeSpray.Particles", Integer.valueOf(10));
		config.addDefault("Flower.SporeSpray.Damage", Double.valueOf(4));
	

		config.addDefault("Flower.PlantDrain.Cooldown", Long.valueOf(5000));
		config.addDefault("Flower.PlantDrain.Duration", Long.valueOf(5000));
		config.addDefault("Flower.PlantDrain.Range", Integer.valueOf(20));
		config.addDefault("Flower.PlantDrain.Particles", Integer.valueOf(10));
		config.addDefault("Flower.PlantDrain.Damage", Double.valueOf(4));
		
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
}
*/
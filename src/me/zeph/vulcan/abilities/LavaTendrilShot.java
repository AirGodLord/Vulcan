package me.zeph.vulcan.abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;

import me.domirusz24.pk.configapi.pkconfigapi.annotations.ConfigAbility;
import me.domirusz24.pk.configapi.pkconfigapi.annotations.ConfigValue;

@ConfigAbility(value = "Vulcan", configPath = "Vulcan/config.yml", plugin = "Vulcan")
public class LavaTendrilShot extends LavaAbility{

	//Config 
	@ConfigValue("Damage")
	private static double damage = 2;
	@ConfigValue("Range")
	private static double range = 10;
	@ConfigValue("Hitbox")
	private static double hitbox = 1;
	@ConfigValue("Speed")
	private static double speed = 1;
	@ConfigValue("SourceRange")
	private static double sourcerange = 10;
	@ConfigValue("Cooldown")
	private static long cooldown = 3000;
	@ConfigValue("RiseDuration")
	private static long riseduration = 250;
	

	//set variables
	private Location loc;
	private Location origin;
	private Vector dir;
	private long starttime, timepassed;
	private List<TempBlock>tbs = new ArrayList<TempBlock>();
	private List<TempBlock>tbsduplicate = new ArrayList<TempBlock>();

	public LavaTendrilShot(Player player, Location source) {
		super(player);
		this.origin = source;
		this.loc = source;

		if (!bPlayer.canBend(this)) {
			return;
		}

		if (bPlayer.isOnCooldown(this)) {
			return;
		}

		setFields();
		start();

	
	}

	private void setFields() {
		//Config

		//Set variables
		this.dir = player.getEyeLocation().getDirection().normalize().subtract(new Vector(0,0.2,0)).normalize();
		this.starttime = System.currentTimeMillis();
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public String getName() {
		return "LavaTendril";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public void progress() {

		if (this.player.isDead() || !this.player.isOnline()) {
			this.remove();
			return;
		} else if (GeneralMethods.isRegionProtectedFromBuild(this, this.loc)) {
			this.remove();
			return;
		}
		
		if (loc.distance(player.getEyeLocation()) > range) {
			this.remove();
			return;
		}
		
		timepassed = System.currentTimeMillis() - starttime;
		
		if (timepassed < riseduration) {
			loc.add(new Vector (0, 0.5, 0));
		}
		else {
			loc.add(dir.clone().multiply(speed));
		}
		
		tbs.add(new TempBlock(loc.getBlock(), Material.LAVA));
		loc.getBlock().setMetadata("Lava", new FixedMetadataValue(ProjectKorra.plugin, this));
		
		for (Entity e : GeneralMethods.getEntitiesAroundPoint(loc, hitbox)) {
			if (e instanceof LivingEntity && e.getUniqueId() != player.getUniqueId()) {
				DamageHandler.damageEntity(e, damage, this);
				this.remove();
			}
		}
		
		tbsduplicate = tbs;
		for (Iterator<TempBlock> it = tbsduplicate.iterator(); it.hasNext(); ) {
		    TempBlock tb = it.next();
		    if (tb.getLocation().distance(loc) > 4) {
		    	tb.revertBlock();
		        it.remove();
		    }
		    if (tb.getLocation().distance(player.getLocation().add(0,1,0)) > range){
		    	tb.revertBlock();
		    }
		}
		tbs = tbsduplicate;
	}


	@Override
	public void remove() {
		for (TempBlock tb: tbs) {
			tb.revertBlock();
		}
		super.remove();
	}
	
	@Override
	public String getDescription() {
		return "By: __Zephyrus \n"
				+ "Shoot small bursts of lava at your enemies."; 
	}
	
	@Override
	public String getInstructions() {
		return "Tap shift at lava to select a source, click to fire. You can have multiple projectiles."; 
	}
}





























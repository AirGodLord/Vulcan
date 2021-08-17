package me.zeph.vulcan.abilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;

import me.domirusz24.pk.configapi.pkconfigapi.annotations.ConfigAbility;
import me.domirusz24.pk.configapi.pkconfigapi.annotations.ConfigValue;

@ConfigAbility(value = "Vulcan", configPath = "Vulcan/config.yml", plugin = "Vulcan")
public class LavaTendril extends LavaAbility{

	//Config 
	@ConfigValue("Range")
	private static double range = 20;
	@ConfigValue("Hitbox")
	private static double hitbox = 1;
	@ConfigValue("Speed")
	private static double speed = 1;
	@ConfigValue("SourceRange")
	private static double sourcerange = 10;
	@ConfigValue("GravityPerSecond")
	private static double gravitypersecond = 1;
	@ConfigValue("Cooldown")
	private static long cooldown = 3000;


	//set variables
	private Location loc;
	private Location origin;
	private Vector dir;
	private List<Block>sources = new ArrayList<Block>();
	private List<TempBlock>tbs = new ArrayList<TempBlock>();
	private int shots;


	public LavaTendril(Player player) {
		super(player);

		if (!bPlayer.canBend(this)) {
			return;
		}

		if (bPlayer.isOnCooldown(this)) {
			return;
		}

		if (CoreAbility.hasAbility(player, this.getClass())) {
			return;
		}

		getSource();
		setFields();
		start();


	}

	private void setFields() {
		//Config

		//Set variables
		this.loc = player.getLocation();
		this.origin = loc.clone();
		this.dir = loc.getDirection();
		this.shots = 0;


	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return null;
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

		if (shots == 3) {
			this.remove();
			return;
		}

	}

	public void getSource() {
		Block block = BlockSource.getLavaSourceBlock(player, sourcerange, ClickType.SHIFT_DOWN);
		if (block == null) {
			return;
		}

		if (sources.size() == 3 - shots) {
			sources.remove(0);
		}
		sources.add(block);
		if (tbs.size() == 3 - shots) {
			tbs.get(0).revertBlock();
			tbs.remove(0);
		}
		tbs.add(new TempBlock(block, Material.MAGMA_BLOCK));
	}


	public void shootTendril() {
		if (sources.isEmpty()) {
			return;
		}
		Location loc = sources.get(0).getLocation();
		tbs.get(0).revertBlock();
		sources.remove(0);
		new LavaTendrilShot(player,loc);
		shots++;

	}



	@Override
	public void remove() {
		for (TempBlock tb : tbs) {
			tb.revertBlock();
		}
		bPlayer.addCooldown(this);
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



























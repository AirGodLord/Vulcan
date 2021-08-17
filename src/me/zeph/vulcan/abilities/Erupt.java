package me.zeph.vulcan.abilities;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
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
public class Erupt extends LavaAbility{

	//Config 
	@ConfigValue("Range")
	private static double range = 20;
	@ConfigValue("Hitbox")
	private static double hitbox = 1;
	@ConfigValue("Speed")
	private static double speed = 1.5;
	@ConfigValue("Cooldown")
	private static long cooldown = 3000;
	@ConfigValue("Radius")
	private static double radius = 4;
	@ConfigValue("Duration")
	private static long duration = 5000;
	@ConfigValue("Height")
	private static double height = 6;
	@ConfigValue("SourceRange")
	private static double sourcerange = 10;
	@ConfigValue("FallingBlocks")
	private static int fallingblocks = 5;
	@ConfigValue("Damage")
	private static double damage = 0.5;
	@ConfigValue("Shots")
	private static int shots = 4;

	//set variables

	private ArrayList<Block>blocks = new ArrayList<Block>();
	private ArrayList<TempBlock>tbs = new ArrayList<TempBlock>();
	private Block source;

	private Vector dir;
	private Location loc;
	private Boolean hasshot;
	private ArrayList<FallingBlock>fbs = new ArrayList<FallingBlock>();
	private long starttime;
	private int currentshots;

	public Erupt(Player player) {
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

		Block block = BlockSource.getEarthSourceBlock(player, sourcerange, ClickType.SHIFT_DOWN);
		if (block == null) {
			return;
		}

		source = block;
		for (Block b : GeneralMethods.getBlocksAroundPoint(source.getLocation(), 2)) {
			if (b.getType().isSolid() && b.getLocation().add(0,1,0).getBlock().getType() == Material.AIR) {
				b.setMetadata("Lava", new FixedMetadataValue(ProjectKorra.plugin, this));
				TempBlock tb = new TempBlock(b, Material.LAVA);
				tb.setRevertTime(duration);
			}
		}
		for (int i = 0; i<height; i++) {
			Block higherblock = block.getLocation().add(new Vector(0,i,0)).getBlock();
			blocks.add(higherblock);
			new TempBlock(higherblock, Material.LAVA).setRevertTime(duration/(i+1));
		}
		setFields();
		start();
	}


	private void setFields() {
		//Config

		//Set variables
		this.loc = source.getLocation();
		this.hasshot = false;
		this.starttime = System.currentTimeMillis();
		this.currentshots = 0;
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
		return "Erupt";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
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

		if (System.currentTimeMillis() > starttime + duration) {
			this.remove();
			return;
		}

		if (hasshot) {
			for (FallingBlock fb : fbs) {
				for (Entity entity : GeneralMethods.getEntitiesAroundPoint(fb.getLocation(), hitbox)) {
					if ((entity instanceof LivingEntity) && entity.getEntityId() != player.getEntityId()) {
						DamageHandler.damageEntity(entity, damage, this);
						return;
					}
				}
			}
		}

		if (currentshots == shots) {
			bPlayer.addCooldown(this);
		}

	}

	public void onClick() {
		if (currentshots == shots) {
			return;
		}
		currentshots ++;
		hasshot = true;
		bPlayer.addCooldown(this);
		this.dir = GeneralMethods.getTargetedLocation(player, range).subtract(loc).toVector().normalize();

		for (Block b : blocks) {
			if (TempBlock.isTempBlock(b)) {
				if (b.getLocation().getY() > loc.getY()) {
					loc = b.getLocation();
				}
				TempBlock tb = TempBlock.get(b);
				tb.setType(Material.MAGMA_BLOCK);

			}
		}
		FallingBlock fb = GeneralMethods.spawnFallingBlock(loc.clone().add(0,1,0).subtract(0, height * (System.currentTimeMillis() - starttime) / duration, 0), Material.MAGMA_BLOCK);
		fb.setMetadata("Erupt", new FixedMetadataValue(ProjectKorra.plugin, this));
		fb.setDropItem(false);
		fb.setVelocity(dir.clone().multiply(speed));
		fbs.add(fb);
	}
	

	@Override
	public String getDescription() {
		return "By: __Zephyrus\n"
				+ "Cause a miniature volcanic eruption."; 
	}

	@Override
	public String getInstructions() {
		return "Tap shift on an earthbendable block, click to spew magma from the eruption."; 
	}
}























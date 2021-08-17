package me.zeph.vulcan.abilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import me.zeph.vulcan.abilities.combos.MagmaMelt;

@ConfigAbility(value = "Vulcan", configPath = "Vulcan/config.yml", plugin = "Vulcan")
public class LavaLine extends LavaAbility{

	//Config 
	@ConfigValue("Damage")
	private static double damage = 2;
	@ConfigValue("Range")
	private static double range = 20;
	@ConfigValue("Hitbox")
	private static double hitbox = 1.5;
	@ConfigValue("Speed")
	private static double speed = 1;
	@ConfigValue("SourceRange")
	private static double sourcerange = 10;
	@ConfigValue("Cooldown")
	private static long cooldown = 3000;
	@ConfigValue("Width")
	private static double width = 1.5;
	@ConfigValue("Duration")
	private static long duration = 3000;

	//set variables
	private Location loc;
	private Location origin;
	private Vector dir;
	private Block source;
	private TempBlock sourceblock;
	private Boolean shot;
	private List<TempBlock>tbs = new ArrayList<TempBlock>();
	private Location surroundingloc;

	public LavaLine(Player player) {
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
		if (source!=null) {
			sourceblock = new TempBlock(source, Material.MAGMA_BLOCK);
			setFields();
			start();
		}
	}

	private void setFields() {
		//Config

		//Set variables
		this.loc = source.getLocation();
		this.origin = loc.clone();
		this.shot = false;
		
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
		return "LavaLine";
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

		if (!climb()) {
			if (bPlayer.getBoundAbilityName().equalsIgnoreCase("IgneousShift") && player.isSneaking()) {
				new MagmaMelt(player, loc.getBlock());
			}
			this.remove();
			return;
		}

		if (this.shot) {
			FallingBlock fb = GeneralMethods.spawnFallingBlock(loc.clone().add(0,0.5,0).add(dir.clone().multiply(speed)), Material.MAGMA_BLOCK);
			fb.setMetadata("LavaLine", new FixedMetadataValue(ProjectKorra.plugin, this));
			fb.setDropItem(false);
			
			loc.add(dir.clone().multiply(speed));
			for (Block block : GeneralMethods.getBlocksAroundPoint(loc, width)) {
				surroundingloc = block.getLocation();
				if (climbsurrounding() && loc.getBlock() != surroundingloc.getBlock()) {
					tbs.add(new TempBlock(surroundingloc.getBlock(), Material.LAVA));
					block.setMetadata("Lava", new FixedMetadataValue(ProjectKorra.plugin, this));
				}
			}
			

			if (loc.distance(origin) > range) {
				this.remove();
				return;
			}

			for (Entity entity : GeneralMethods.getEntitiesAroundPoint(loc.clone().add(0,1,0), hitbox)) {
				if ((entity instanceof LivingEntity) && entity.getEntityId() != player.getEntityId()) {
					DamageHandler.damageEntity(entity, damage, this);
					this.remove();
					return;

				}
			}
		}
			

	}


	public void getSource() {
		if (!CoreAbility.hasAbility(player, this.getClass())) {
			Block block = BlockSource.getEarthSourceBlock(player, sourcerange, ClickType.SHIFT_DOWN);
			if (block!=null) {
				source = block;
			}
		}
		else {
			return;
		}
	}


	@Override
	public void remove() {
		for (Iterator<TempBlock> it = tbs.iterator(); it.hasNext(); ) {
		    TempBlock tb = it.next();
		    	tb.setRevertTime(duration);
		        it.remove();
		    
		}
		super.remove();
	}

	private boolean climb() {
		Block above = loc.getBlock().getRelative(BlockFace.UP);

		if (!isTransparent(above)) {
			// Attempt to climb since the current location has a block above it.
			loc.add(0, 1, 0);
			above = loc.getBlock().getRelative(BlockFace.UP);

			// The new location must be earthbendable and have something transparent above it.
			return EarthAbility.isEarthbendable(loc.getBlock().getType(), true, true, true) && isTransparent(above);
		} else if (isTransparent(loc.getBlock()) && loc.getBlock().getType() != Material.LAVA ) {
			// Attempt to fall since the current location is transparent and the above block was transparent.
			loc.add(0, -1, 0);

			// The new location must be earthbendable and we already know the block above it is transparent.
			return EarthAbility.isEarthbendable(loc.getBlock().getType(), true, true, true);
		}

		return true;
	}

	private boolean climbsurrounding() {
		Block above = surroundingloc.getBlock().getRelative(BlockFace.UP);

		if (!isTransparent(above)) {
			// Attempt to climb since the current location has a block above it.
			surroundingloc.add(0, 1, 0);
			above = surroundingloc.getBlock().getRelative(BlockFace.UP);

			// The new location must be earthbendable and have something transparent above it.
			return EarthAbility.isEarthbendable(surroundingloc.getBlock().getType(), true, true, true) && isTransparent(above);
		} else if (isTransparent(surroundingloc.getBlock()) ) {
			// Attempt to fall since the current location is transparent and the above block was transparent.
			surroundingloc.add(0, -1, 0);

			// The new location must be earthbendable and we already know the block above it is transparent.
			return EarthAbility.isEarthbendable(surroundingloc.getBlock().getType(), true, true, true);
		}

		return true;
	}
	
	
	
	public void onClick() {
		if (this.shot) {
			return;
		}
		else {
			sourceblock.revertBlock();
			this.shot = true;
			this.dir = player.getEyeLocation().getDirection().setY(0);
			bPlayer.addCooldown(this);
			return;
		}
	}

	@Override
	public String getDescription() {
		return "By: __Zephyrus \n"
				+ "Rip open the earth's crust."; 
	}
	
	@Override
	public String getInstructions() {
		return "Tap shift at an earthbendable block, click to fire."; 
	}

}


































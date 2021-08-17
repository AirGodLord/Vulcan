package me.zeph.vulcan.abilities.combos;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.util.TempBlock;

import me.domirusz24.pk.configapi.pkconfigapi.annotations.ConfigAbility;
import me.domirusz24.pk.configapi.pkconfigapi.annotations.ConfigValue;

@ConfigAbility(value = "Vulcan", configPath = "Vulcan/config.yml", plugin = "Vulcan")
public class MagmaMelt extends LavaAbility{

	//Config 
	@ConfigValue("Damage")
	private static double damage = 2;
	@ConfigValue("MaxHeight")
	private static double maxheight = 30;
	@ConfigValue("Hitbox")
	private static double hitbox = 1;
	@ConfigValue("Speed")
	private static double speed = 1;
	@ConfigValue("Cooldown")
	private static long cooldown = 3000;
	@ConfigValue("Radius")
	private static double radius = 4;
	@ConfigValue("DurationMagma")
	private static long durationmagma = 5000;
	@ConfigValue("TimeTillMaxRadius")
	private static long durationgrow = 5000;
	@ConfigValue("TimeTillRevert")
	private static long revertduration = 60000;

	//set variables
	private Location loc;
	private Block source;
	private List<TempBlock> tbs;
	private long starttime;
	private long timepassed;
	private double currentradius;
	private List<Block> falling;
	private List<Location> fallenblocks;
	private double currentheight;
	private List<Block>hasfallen;

	public MagmaMelt(Player player, Block source) {
		super(player);


		if (bPlayer.isOnCooldown(this)) {
			return;
		}

		if (CoreAbility.hasAbility(player, this.getClass())) {
			return;
		}

		this.source = source;

		setFields();
		start();
	}


	private void setFields() {
		//Config

		//Set variables
		this.loc = player.getLocation();
		this.starttime = System.currentTimeMillis();
		this.tbs = new ArrayList<TempBlock>();
		this.falling = new ArrayList<Block>();
		this.fallenblocks = new ArrayList<Location>();
		this.hasfallen = new ArrayList<Block>();

		this.currentradius = 0;
		this.currentheight = 0;
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
		return "MagmaMelt";
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


		timepassed = System.currentTimeMillis() - starttime;
		if (currentradius<radius) {
			currentradius = timepassed * (radius/durationgrow);
		}
		if (currentheight<maxheight) {
			currentheight = (timepassed-durationgrow) * (maxheight/durationmagma);
			if (currentheight<0) {
				currentheight = 0 ;
			}
		}
	
		for (Block b : GeneralMethods.getBlocksAroundPoint(source.getLocation(), currentradius)) {
			double y = source.getLocation().getY();
			if (b.getLocation().getY() >= y && b.getType() != Material.AIR) {	
				if (!falling.contains(b)) {
					falling.add(b);
				}
				climb(b.getLocation());
			}
		}
		
		if (timepassed < (durationmagma + durationgrow)) {
			for (Block b : falling) {
				if (!TempBlock.isTempBlock(b)) {
					TempBlock tb = new TempBlock(b, Material.MAGMA_BLOCK);
					tb.setRevertTime(durationgrow + durationmagma+revertduration);
					tbs.add(tb);
				}
			}
		}

		if (timepassed >= (durationmagma + durationgrow)) {
			
			for (TempBlock tb : tbs) {
				tb.setType(Material.AIR);
			}
			
			
			
			for (Block b : falling) {
					if (!fallenblocks.contains(b.getLocation())) {
						FallingBlock fb = GeneralMethods.spawnFallingBlock(b.getLocation(), Material.MAGMA_BLOCK);
						fb.setMetadata("MagmaMelt", new FixedMetadataValue(ProjectKorra.plugin, this));
						fb.setDropItem(false);
						fallenblocks.add(b.getLocation());
					}
					hasfallen.add(b);
				}
			}
			if (hasfallen.size() == falling.size()) {
				bPlayer.addCooldown(this);
				this.remove();
				return;
			}

	}

	

	public void climb(Location loc) {
		Location toploc = new Location (player.getWorld(), loc.getX(), loc.clone().getY()+currentheight, loc.getZ());
		Location bottomloc = loc.clone();
		for (Block b : GeneralMethods.getBlocksAlongLine(bottomloc, toploc, loc.getWorld())){
			if (!GeneralMethods.isSolid(loc.getBlock())) {
				if (!loc.getBlock().getType().equals(Material.LAVA)){
					return;
				}
			}
			loc.add(0,1,0);
			if (!falling.contains(b)) {
				falling.add(b);
			}
		}
	}
	
	@Override
	public String getDescription() {
		return "By: __Zephyrus \n"
				+ "Ghazan brought down the mighty walls of Ba Sing Se with this..."; 
	}
	
	@Override
	public String getInstructions() {
		return "Shoot a LavaLine into a wall, and hold down shift with IgneousShift as the LavaLine makes contact."; 
	}
}































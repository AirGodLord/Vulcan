package me.zeph.vulcan.abilities;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.TempBlock;

import me.domirusz24.pk.configapi.pkconfigapi.annotations.ConfigAbility;
import me.domirusz24.pk.configapi.pkconfigapi.annotations.ConfigValue;

@ConfigAbility(value = "Vulcan", configPath = "Vulcan/config.yml", plugin = "Vulcan")
public class LavaPool extends LavaAbility{

	//Config 
	@ConfigValue("LavaPoolRange")
	private static double range = 10;
	@ConfigValue("LavaPoolCooldown")
	private static long cooldown = 3000;
	@ConfigValue("LavaPoolRadius")
	private static double radius = 2;
	@ConfigValue("LavaPoolDuration")
	private static long duration = 2000;

	//set variables
	private ArrayList<Block> blocks = new ArrayList<Block>();
	private ArrayList<Block> lavablocks = new ArrayList<Block>();
	private ArrayList<Block> magmablocks = new ArrayList<Block>();
	private ArrayList<TempBlock> tbs = new ArrayList<TempBlock>();
	private long starttime;
	private Boolean lava = false;
	private long timeleft;



	public LavaPool(Player player) {
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

		blocks =  this.getBlocksAroundPoint(GeneralMethods.getTargetedLocation(player, range, false), radius);
		for (Block b : blocks) {
			if (EarthAbility.isEarthbendable(player, b)) {
				lavablocks.add(b);
				magmablocks.add(b);
				if (!lava) {
					lava = true;
				}
			}
		}
		if (lava) {
			setFields();
			start();
		}
		else {
			return;
		}
	}

	private void setFields() {
		//Config

		//Set variables
		this.starttime = System.currentTimeMillis();
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
		return "IgneousShift";
	}

	@Override
	public boolean isHarmlessAbility() {
		return true;
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
		} else if (GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation())) {
			this.remove();
			return;
		}
		
		timeleft = (starttime+duration)-System.currentTimeMillis();
	
		
		if (timeleft > 0) {
			Random r = new Random();
			if (magmablocks.size()>1) {
				int block = r.nextInt(magmablocks.size() - 1);
				TempBlock tb = new TempBlock(magmablocks.get(block), Material.MAGMA_BLOCK);
				tbs.add(tb);
				magmablocks.remove(block);
			}
				
		}
		
		
		
		if (timeleft < 0) {
			for (TempBlock magma : tbs) {
				magma.revertBlock();
			}
			for (Block lava: lavablocks) {
				lava.setType(Material.LAVA);
				lava.setMetadata("Lava", new FixedMetadataValue(ProjectKorra.plugin, this));
			}
			this.remove();
			return;
		}


	}


	public ArrayList<Block> getBlocksAroundPoint(final Location location, final double radius) {
		final ArrayList<Block> blocks = new ArrayList<Block>();

		final int xorg = location.getBlockX();
		final int yorg = location.getBlockY();
		final int zorg = location.getBlockZ();

		final int r = (int) radius * 4;

		for (int x = xorg - r; x <= xorg + r; x++) {
			for (int y = yorg - r; y <= yorg + r; y++) {
				for (int z = zorg - r; z <= zorg + r; z++) {
					final Block block = location.getWorld().getBlockAt(x, y, z);
					if (block.getLocation().distanceSquared(location) <= radius * radius) {
						blocks.add(block);
					}
				}
			}
		}
		return blocks;
	}


	
	@Override
	public void remove() {
		super.remove();
		bPlayer.addCooldown(this);
	}

	@Override
	public String getDescription() {
		return "By: __Zephyrus \n"
				+ "A utility move for lavabenders."; 
	}
	
	@Override
	public String getInstructions() {
		return "Hold shift at lava to solidify it or click an earthbendable block to melt it. Alternatively use the slot to negate damage from lava and walk on magma blocks."; 
	}
}




package me.zeph.vulcan.abilities;

import java.util.ArrayList;
import java.util.Collections;
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
public class LavaStream extends LavaAbility{

	//Config 
	@ConfigValue("Damage")
	private static double damage = 2;
	@ConfigValue("Range")
	private static double range = 20;
	@ConfigValue("Hitbox")
	private static double hitbox = 1;
	@ConfigValue("Speed")
	private static double speed = 1;
	@ConfigValue("SourceRange")
	private static double sourcerange = 10;
	@ConfigValue("Cooldown")
	private static long cooldown = 3000;
	@ConfigValue("Radius")
	private static double radius = 2.5;

	//set variables
	private Location loc;
	private Location ploc;
	private Location origin;
	private Vector dir;
	private Block source;
	private State state;
	private double angle;
	private HashMap<TempBlock, Vector>tbs = new HashMap<TempBlock, Vector>();
	private List<TempBlock>replacedtbs = new ArrayList<TempBlock>();
	private List<TempBlock>firedtbs = new ArrayList<TempBlock>();

	public enum State{
		SELECTED, SOURCE, RING, FIRING, CONTACT
	}


	public LavaStream(Player player) {
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
		loc = source.getLocation();
		state = State.SELECTED;
		setFields();
		start();

	}

	private void setFields() {
		//Config

		//Set variables
		this.loc = source.getLocation();
		this.origin = loc.clone();
		this.angle = 0;
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
		return "LavaStream";
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

		if (state == State.SELECTED || state == State.RING) {
			if (! player.isSneaking()) {
				this.remove();
				return;
			}
		}


		ploc = player.getEyeLocation();

		if (state == State.SELECTED) {
			source();
		}
		if (loc.distance(ploc) < radius && state.equals(State.SELECTED) ) {
			state = State.RING;
		}

		if (state == State.RING) {
			dir = (loc.clone().subtract(ploc)).toVector().normalize().multiply(radius).setY(0);
			dir.rotateAroundY(Math.toRadians(20));
			loc = ploc.clone().add(dir);

			if (!TempBlock.isTempBlock(loc.getBlock())) {
				TempBlock tb = new TempBlock(loc.getBlock(), Material.LAVA);
				loc.getBlock().setMetadata("Lava", new FixedMetadataValue(ProjectKorra.plugin, this));
				tb.setRevertTime(500);
			}

		}
		if (state == State.FIRING) {
			loc.add(dir);
			TempBlock tb = new TempBlock(loc.getBlock(), Material.LAVA);
			firedtbs.add(tb);
			loc.getBlock().setMetadata("Lava", new FixedMetadataValue(ProjectKorra.plugin, this));
			tb.setRevertTime(1000);
			for (Entity e : GeneralMethods.getEntitiesAroundPoint(loc, hitbox)) {
				if (e instanceof LivingEntity && e.getUniqueId() != player.getUniqueId()) {
					DamageHandler.damageEntity(e, damage, this);
					remove();
				}
			}

			if (GeneralMethods.isSolid(loc.getBlock())) {
				state = State.CONTACT;
			}

			if (loc.distance(ploc) > range) {
				remove();
			}
		}

	}

	public void onClick() {
		if (state != State.RING) {
			return;
		}
		state = State.FIRING;
		dir = player.getLocation().add(0,1,0).getDirection();
		for (TempBlock tb : tbs.keySet()) {
			tb.revertBlock();
		}
		for (TempBlock tb : replacedtbs) {
			tb.revertBlock();
		}
	}




	public void source() {
		dir = ((player.getLocation().add(0,1,0)).subtract(loc)).toVector().normalize();
		loc.add(dir.clone().multiply(speed));
		TempBlock tb = new TempBlock(loc.getBlock(), Material.LAVA);
		loc.getBlock().setMetadata("Lava", new FixedMetadataValue(ProjectKorra.plugin, this));
		tb.setRevertTime(1000);
	}

	public void getSource() {
		Block block = BlockSource.getLavaSourceBlock(player, sourcerange, ClickType.SHIFT_DOWN);
		if (block == null) {
			return;
		}
		source = block;
	}

	@Override
	public void remove() {
		for (TempBlock tb : tbs.keySet()) {
			tb.revertBlock();
		}
		for (TempBlock tb : replacedtbs) {
			tb.revertBlock();
		}
		for (TempBlock tb : firedtbs) {
			tb.revertBlock();
		}
		bPlayer.addCooldown(this);
		super.remove();
		return;
	}

	@Override
	public String getDescription() {
		return "By: __Zephyrus \n"
				+ "Manipulate a stream of lava."; 
	}

	@Override
	public String getInstructions() {
		return "Tap shift once whilst facing lava to create a ring around yourself, click to fire."; 
	}
}
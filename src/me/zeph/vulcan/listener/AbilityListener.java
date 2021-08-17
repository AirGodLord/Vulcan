package me.zeph.vulcan.listener;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.PlayerBindChangeEvent;

import me.zeph.vulcan.abilities.Erupt;
import me.zeph.vulcan.abilities.IgneousShift;
import me.zeph.vulcan.abilities.LavaLine;
import me.zeph.vulcan.abilities.LavaPool;
import me.zeph.vulcan.abilities.LavaStream;
import me.zeph.vulcan.abilities.LavaTendril;
import me.zeph.vulcan.abilities.LavaTendrilShot;

public class AbilityListener implements Listener {

	@EventHandler
	public void onSwing(PlayerAnimationEvent event) {

		Player player = event.getPlayer();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

		if (event.isCancelled() || bPlayer == null) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase(null)) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("LavaStream")) {
			if (CoreAbility.hasAbility(player, LavaStream.class)) {
				CoreAbility.getAbility(player, LavaStream.class).onClick();
			}


		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("LavaTendril")) {
			if (CoreAbility.hasAbility(player, LavaTendril.class)) {
				CoreAbility.getAbility(player, LavaTendril.class).shootTendril();
			}

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("LavaLine")) {
			if (CoreAbility.hasAbility(player, LavaLine.class)) {
				CoreAbility.getAbility(player, LavaLine.class).onClick();
			}

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Erupt")) {
			if (CoreAbility.hasAbility(player, Erupt.class)) {
				CoreAbility.getAbility(player, Erupt.class).onClick();
			}
		}
		else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("IgneousShift")) {
			new LavaPool(player);
		}
		}




	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {

		Player player = event.getPlayer();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

		if (event.isCancelled() || bPlayer == null) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase(null)) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("Erupt")) {
			new Erupt(player);

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("IgneousShift")) {
			new IgneousShift(player);

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("LavaLine")) {
			new LavaLine(player);


		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("LavaStream")) {
			new LavaStream(player);


		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("LavaTendril")) {
			if (!CoreAbility.hasAbility(player, LavaTendril.class)) {
				new LavaTendril(player);
			}
			else {
				CoreAbility.getAbility(player, LavaTendril.class).getSource();
			}
		}
	}

	@EventHandler
	public void magmaWalk(EntityDamageEvent event) {

		if (! (event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

		if (event.isCancelled() || bPlayer == null) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase(null)) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("IgneousShift")) {
			if (event.getCause().equals(DamageCause.FIRE_TICK) || event.getCause().equals(DamageCause.HOT_FLOOR) || event.getCause().equals(DamageCause.LAVA) ) {
				event.setCancelled(true);
			}
		}
	}

	/*
	@EventHandler
	public void stopLavaBurn(EntityCombustByBlockEvent event) {

		if (! (event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (event.getCombuster().hasMetadata("Lava")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void stopLavaDamage(EntityDamageByBlockEvent event) {

		if (! (event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (event.getDamager().hasMetadata("Lava")) {
			event.setCancelled(true);
		}
	}
	 */

	@EventHandler
	public void stopLava(EntityDamageEvent event) {

		if (! (event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (event.getCause().equals(DamageCause.LAVA)){
			Boolean lava = false;
			for (Block b : GeneralMethods.getBlocksAroundPoint(player.getLocation(), 2)) {
				if (b.hasMetadata("Lava")) {
					lava = true;
				}
			}
			if (lava) {
				event.setDamage(0);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void stopLavaBurn(EntityCombustEvent event) {
		if (! (event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		Boolean lava = false;
		for (Block b : GeneralMethods.getBlocksAroundPoint(player.getLocation(), 2)) {
			if (b.hasMetadata("Lava")) {
				lava = true;
			}
		}
		if (lava) {
			event.setCancelled(true);
		}
	}





	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		Entity e = event.getEntity();

		if (e instanceof FallingBlock) {
			FallingBlock fb = (FallingBlock)e;
			if (fb.hasMetadata("MagmaMelt") || fb.hasMetadata("Erupt") || fb.hasMetadata("LavaLine")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBind(PlayerBindChangeEvent e) {
		if(e.isCancelled() || e.isMultiAbility()) return;
		if(e.getAbility().equalsIgnoreCase("MagmaMelt")) {
			e.getPlayer().sendMessage(ChatColor.GOLD+"ProjectKorra"+ ChatColor.RED + " Binding this ability is useless, read /b h MagmaMelt.");
			e.setCancelled(true);	
		}
	}
	
	@EventHandler
	public void onSpread(BlockFromToEvent event) {
		if (event.getBlock().hasMetadata("Lava") && event.getBlock().getType() == Material.LAVA) {
			Levelled levelledBlock = (Levelled) event.getBlock().getBlockData();
			if (levelledBlock.getLevel() != 1) {
				event.setCancelled(true);
			}
		}
	}
}

















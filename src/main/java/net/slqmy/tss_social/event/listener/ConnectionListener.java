package net.slqmy.tss_social.event.listener;

import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.util.MapUtil;
import net.slqmy.tss_social.TSSSocialPlugin;
import net.slqmy.tss_social.type.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConnectionListener implements Listener {

  private final TSSSocialPlugin plugin;

  public ConnectionListener(TSSSocialPlugin plugin) {
	this.plugin = plugin;
  }

  @EventHandler
  public void onQuitWhileMessaging(@NotNull PlayerQuitEvent event) {
	HashMap<UUID, UUID> conversations = plugin.getConversations();
	UUID quitUuid = event.getPlayer().getUniqueId();

	if (conversations.containsKey(quitUuid)) {
	  conversations.remove(quitUuid);
	  return;
	}

	Map<UUID, UUID> inverted = MapUtil.invertMap(conversations);

	if (inverted.containsKey(quitUuid)) {
	  conversations.remove(inverted.get(quitUuid));
	}
  }

  @EventHandler
  public void onQuitWhilePartying(@NotNull PlayerQuitEvent event) {
	Player player = event.getPlayer();
	Party party = plugin.getPartyManager().getPlayerParty(event.getPlayer());

	if (party == null) {
	  return;
	}

	String playerName = player.getName();
	party.sendMessage(Message.PLAYER_DISCONNECTED_FROM_PARTY, playerName);

	int[] seconds = {0};
	new BukkitRunnable() {

	  @Override
	  public void run() {
		if (seconds[0] == 60 * 5) {
		  Bukkit.getServer().dispatchCommand(player, "/party leave");
		  party.sendMessage(Message.PLAYER_KICKED_BY_DISCONNECT, playerName);
		  party.removePlayer(player);
		  cancel();
		  return;
		}

		if (player.isOnline()) {
		  party.sendMessage(Message.PLAYER_HAS_REJOINED, playerName);
		  cancel();
		  return;
		}

		seconds[0]++;
	  }
	}.runTaskTimer(plugin, 1L, 20L);
  }
}

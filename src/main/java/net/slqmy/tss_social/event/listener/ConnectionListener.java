package net.slqmy.tss_social.event.listener;

import net.slqmy.tss_core.util.MapUtil;
import net.slqmy.tss_social.TSSSocialPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
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
  public void onQuit(@NotNull PlayerQuitEvent event) {
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
}

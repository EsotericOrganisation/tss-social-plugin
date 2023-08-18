package net.slqmy.tss_social.type;

import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_social.TSSSocialPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;

public class Party {

  private static final Random random = new Random();

  private final TSSSocialPlugin plugin;
  private final LinkedList<UUID> partyGoerUuids;
  private final byte settingsBitField;
  private UUID ownerUuid;

  public Party(UUID ownerUuid, LinkedList<UUID> partyGoerUuids, byte settingsBitField, TSSSocialPlugin plugin) {
	this.plugin = plugin;

	this.ownerUuid = ownerUuid;
	this.partyGoerUuids = partyGoerUuids;
	this.settingsBitField = settingsBitField;
  }

  public UUID getOwnerUuid() {
	return ownerUuid;
  }

  public void setOwnerUuid(UUID ownerUuid) {
	this.ownerUuid = ownerUuid;
  }

  public LinkedList<UUID> getPartyGoerUuids() {
	return partyGoerUuids;
  }

  public byte getSettingsBitField() {
	return settingsBitField;
  }

  @Nullable
  public Player getPlayer(UUID uuid) {
	for (UUID partyGoerUuid : partyGoerUuids) {
	  if (partyGoerUuid.equals(uuid)) {
		return Bukkit.getPlayer(partyGoerUuid);
	  }
	}

	return null;
  }

  public Player getPlayer(@NotNull Player player) {
	return getPlayer(player.getUniqueId());
  }

  public void sendMessage(Message message, Object... placeholderValues) {
	for (UUID partyGoerUuid : partyGoerUuids) {
	  Player player = Bukkit.getPlayer(partyGoerUuid);
	  plugin.getCore().getMessageManager().sendMessage(player, message, placeholderValues);
	}
  }

  public boolean containsPlayer(UUID uuid) {
	return partyGoerUuids.contains(uuid);
  }

  public boolean containsPlayer(@NotNull Player player) {
	return containsPlayer(player.getUniqueId());
  }

  public void removePlayer(@NotNull Player player) {
	UUID playerUuid = player.getUniqueId();
	partyGoerUuids.remove(playerUuid);

	sendMessage(Message.PLAYER_LEFT, player.getName());

	TSSCorePlugin core = plugin.getCore();
	MessageManager messageManager = core.getMessageManager();
	messageManager.sendMessage(player, Message.LEFT_PARTY);

	if (partyGoerUuids.isEmpty()) {
	  plugin.getPartyManager().getParties().remove(this);
	} else if (ownerUuid.equals(playerUuid)) {
	  UUID newPartyOwnerUuid = partyGoerUuids.get(random.nextInt(partyGoerUuids.size()));

	  ownerUuid = newPartyOwnerUuid;

	  Player newPartyOwner = Bukkit.getPlayer(newPartyOwnerUuid);
	  assert newPartyOwner != null;

	  messageManager.sendMessage(newPartyOwner, Message.YOU_ARE_NEW_PARTY_OWNER);
	  sendMessage(Message.NEW_PARTY_OWNER, newPartyOwner.getName());
	}
  }
}

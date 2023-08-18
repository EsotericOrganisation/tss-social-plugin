package net.slqmy.tss_social.type;

import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_social.TSSSocialPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.UUID;

public class Party {

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
}

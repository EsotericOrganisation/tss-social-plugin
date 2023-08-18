package net.slqmy.tss_social.manager;

import net.slqmy.tss_social.type.Party;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PartyManager {

  private final ArrayList<Party> parties = new ArrayList<>();
  private final HashMap<UUID, Party> partyRequests = new HashMap<>();

  public ArrayList<Party> getParties() {
	return parties;
  }

  public HashMap<UUID, Party> getPartyRequests() {
	return partyRequests;
  }

  public boolean isInParty(@NotNull Player player) {
	return getPlayerParty(player) != null;
  }

  public Party getPlayerParty(@NotNull Player player) {
	UUID uuid = player.getUniqueId();

	for (Party party : parties) {
	  if (party.getPartyGoerUuids().contains(uuid)) {
		return party;
	  }
	}

	return null;
  }
}

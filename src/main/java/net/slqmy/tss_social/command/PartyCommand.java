package net.slqmy.tss_social.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_core.util.MapUtil;
import net.slqmy.tss_core.util.MessageUtil;
import net.slqmy.tss_social.TSSSocialPlugin;
import net.slqmy.tss_social.manager.PartyManager;
import net.slqmy.tss_social.type.Party;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PartyCommand {

  public PartyCommand(@NotNull TSSSocialPlugin plugin) {
	PartyManager partyManager = plugin.getPartyManager();
	MessageManager messageManager = plugin.getCore().getMessageManager();

	new CommandAPICommand("party")
			.withArguments(new PlayerArgument("player"))
			.executesPlayer((Player player, CommandArguments args) -> {
			  Player target = (Player) args.get("player");

			  UUID targetUuid = target.getUniqueId();
			  UUID playerUuid = player.getUniqueId();

			  if (playerUuid.equals(targetUuid)) {
				messageManager.sendMessage(player, Message.CANT_PARTY_YOURSELF);
				return;
			  }

			  Party newParty = partyManager.getPlayerParty(player);
			  if (newParty == null) {
				newParty = new Party(
						playerUuid,
						new LinkedList<>(List.of(playerUuid)),
						(byte) 0,
						plugin
				);
			  } else {
				if (!newParty.getOwnerUuid().equals(playerUuid)) {
				  messageManager.sendMessage(player, Message.NOT_YOUR_PARTY);
				  return;
				}

				HashMap<UUID, Party> partyRequests = partyManager.getPartyRequests();
				if (partyRequests.containsKey(targetUuid)) {
				  messageManager.sendMessage(player, Message.PLAYER_ALREADY_INVITED);
				  return;
				}
			  }

			  partyManager.getParties().add(newParty);
			  messageManager.sendMessage(player, Message.INVITED_TO_PARTY, target.getName());

			  partyManager.getPartyRequests().put(targetUuid, newParty);

			  TextComponent inviteMessage = messageManager.getPlayerMessage(Message.ACCEPT_PARTY_INVITE, target, player.getName());
			  TextComponent acceptMessage = messageManager.getPlayerMessage(Message.ACCEPT, target)
					  .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept"));

			  target.sendMessage(
					  inviteMessage.appendSpace().append(
							  MessageUtil.getLeftSquareBracket()
					  ).append(
							  acceptMessage
					  ).append(
							  MessageUtil.getRightSquareBracket()
					  )
			  );
			})
			.withSubcommand(
					new CommandAPICommand("accept")
							.withRequirement((CommandSender sender) -> partyManager.getPartyRequests().get(((Player) sender).getUniqueId()) != null)
							.executesPlayer((Player player, CommandArguments args) -> {
							  Party playerParty = partyManager.getPlayerParty(player);
							  if (playerParty != null) {
								messageManager.sendMessage(player, Message.ALREADY_IN_PARTY);
								return;
							  }

							  UUID playerUuid = player.getUniqueId();
							  HashMap<UUID, Party> partyRequests = partyManager.getPartyRequests();

							  Party requester = partyRequests.get(playerUuid);

							  if (requester == null) {
								messageManager.sendMessage(player, Message.NO_PARTY_INVITES);
								return;
							  }

							  requester.getPartyGoerUuids().add(playerUuid);
							  partyRequests.remove(playerUuid);

							  requester.sendMessage(Message.PLAYER_JOINED_PARTY, player.getName());
							  messageManager.sendMessage(player, Message.JOINED_PARTY);
							})
			)
			.withSubcommand(
					new CommandAPICommand("list")
							.withRequirement((CommandSender player) -> partyManager.isInParty((Player) player))
							.executesPlayer((Player player, CommandArguments args) -> {
							  Party party = partyManager.getPlayerParty(player);

							  for (UUID partyGoerUuid : party.getPartyGoerUuids()) {
								OfflinePlayer partyGoer = Bukkit.getOfflinePlayer(partyGoerUuid);
								player.sendMessage(partyGoer.getName());
							  }
							})
			)
			.withSubcommand(
					new CommandAPICommand("kick")
							.withRequirement((CommandSender sender) -> {
							  Player player = (Player) sender;
							  return partyManager.isInParty(player) && partyManager.getPlayerParty(player).getOwnerUuid().equals(player.getUniqueId());
							})
							.withArguments(
									new PlayerArgument("player")
											.replaceSuggestions(ArgumentSuggestions.strings((SuggestionInfo<CommandSender> info) -> partyManager.getPlayerParty((Player) info.sender()).getPartyGoerUuids().stream().map((UUID partyGoerUuid) -> Bukkit.getPlayer(partyGoerUuid).getName()).toArray(String[]::new)))
							)
							.executesPlayer((Player player, CommandArguments args) -> {
							  Party party = partyManager.getPlayerParty(player);
							  UUID playerUuid = player.getUniqueId();

							  Player target = (Player) args.get("player");
							  UUID targetUuid = target.getUniqueId();
							  if (targetUuid.equals(playerUuid)) {
								messageManager.sendMessage(player, Message.CANT_KICK_YOURSELF);
								return;
							  }

							  if (!party.containsPlayer(target)) {
								messageManager.sendMessage(player, Message.PLAYER_NOT_IN_PARTY);
								return;
							  }

							  party.getPartyGoerUuids().remove(targetUuid);
							  messageManager.sendMessage(target, Message.KICKED_FROM_PARTY);
							  messageManager.sendMessage(player, Message.SUCCESSFUL_PARTY_PLAYER_KICK, target.getName());
							})
			)
			.withSubcommand(
					new CommandAPICommand("leave")
							.withRequirement((CommandSender player) -> partyManager.isInParty((Player) player))
							.executesPlayer((Player player, CommandArguments args) -> {
							  Party party = partyManager.getPlayerParty(player);
							  party.removePlayer(player);
							})
			)
			.withSubcommand(
					new CommandAPICommand("disband")
							.withRequirement((CommandSender sender) -> {
							  Player player = (Player) sender;
							  return partyManager.isInParty(player) && partyManager.getPlayerParty(player).getOwnerUuid().equals(player.getUniqueId());
							})
							.executesPlayer((Player player, CommandArguments args) -> {
							  Party party = partyManager.getPlayerParty(player);
							  partyManager.getParties().remove(party);
							  party.sendMessage(Message.PARTY_DISBANDED);

							  Map<UUID, Party> partyRequests = partyManager.getPartyRequests();
							  partyRequests.remove(MapUtil.invertMap(
									  partyRequests
							  ).get(party));

							  party.getPartyGoerUuids().clear();
							})
			)
			.register();
  }
}

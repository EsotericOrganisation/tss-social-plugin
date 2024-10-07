package org.esoteric_organisation.tss_social_plugin.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import org.esoteric_organisation.tss_core_plugin.datatype.Colour;
import org.esoteric_organisation.tss_core_plugin.datatype.Rank;
import org.esoteric_organisation.tss_core_plugin.datatype.player.Message;
import org.esoteric_organisation.tss_core_plugin.manager.MessageManager;
import org.esoteric_organisation.tss_ranks_plugin.manager.RankManager;
import org.esoteric_organisation.tss_social_plugin.TSSSocialPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ReplyCommand {

  public ReplyCommand(TSSSocialPlugin plugin) {
	new CommandAPICommand("reply")
			.withAliases("r")
			.withArguments(new GreedyStringArgument("message"))
			.executesPlayer((Player player, CommandArguments args) -> {
			  HashMap<UUID, UUID> conversations = plugin.getConversations();

			  UUID playerUuid = player.getUniqueId();
			  MessageManager messageManager = plugin.getCore().getMessageManager();
			  if (!conversations.containsKey(playerUuid)) {
				messageManager.sendMessage(player, Message.NO_RECENT_MESSAGES);
				return;
			  }

			  UUID targetUuid = conversations.get(playerUuid);
			  Player target = Bukkit.getPlayer(targetUuid);

			  String message = (String) args.get("message");

			  RankManager rankManager = plugin.getRanksPlugin().getRankManager();
			  Rank playerRank = rankManager.getPlayerRank(player);
			  Rank targetRank = rankManager.getPlayerRank(target);

			  conversations.put(targetUuid, playerUuid);

			  player.sendMessage(
					  playerRank.getDisplayName()
							  .appendSpace().append(
									  messageManager.getPlayerMessage(Message.YOU, player).color(Colour.WHITE)
							  ).appendSpace().append(
									  Component.text(
											  "->",
											  Colour.WHITE
									  )
							  ).appendSpace().append(
									  targetRank.getDisplayName()
							  ).appendSpace().append(
									  Component.text(
											  target.getName(),
											  Colour.WHITE
									  )
							  ).append(
									  Component.text(
											  ":",
											  Colour.WHITE
									  )
							  ).appendSpace().append(
									  Component.text(message)
							  )
			  );

			  target.sendMessage(
					  playerRank.getDisplayName()
							  .appendSpace().append(
									  Component.text(
											  player.getName(),
											  Colour.WHITE
									  )
							  ).appendSpace().append(
									  Component.text(
											  "->",
											  Colour.WHITE
									  )
							  ).appendSpace().append(
									  targetRank.getDisplayName()
							  ).appendSpace().append(
									  messageManager.getPlayerMessage(Message.YOU, target).color(Colour.WHITE)
							  ).append(
									  Component.text(
											  ":",
											  Colour.WHITE
									  )
							  ).appendSpace().append(
									  Component.text(message)
							  )
			  );
			})
			.register();
  }
}

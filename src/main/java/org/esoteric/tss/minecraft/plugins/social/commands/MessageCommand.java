package org.esoteric.tss.minecraft.plugins.social.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import org.esoteric.tss.minecraft.plugins.core.data.Colour;
import org.esoteric.tss.minecraft.plugins.core.data.Rank;
import org.esoteric.tss.minecraft.plugins.core.data.player.Message;
import org.esoteric.tss.minecraft.plugins.core.managers.MessageManager;
import org.esoteric.tss.minecraft.plugins.ranks.managers.RankManager;
import org.esoteric.tss.minecraft.plugins.social.TSSSocialPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MessageCommand {

  public MessageCommand(TSSSocialPlugin plugin) {
	new CommandAPICommand("message")
			.withAliases("r", "w", "pm", "dm", "m")
			.withArguments(new PlayerArgument("player"), new GreedyStringArgument("message"))
			.executesPlayer((Player player, CommandArguments args) -> {
			  Player target = (Player) args.get("player");
			  assert target != null;

			  UUID playerUuid = player.getUniqueId();
			  UUID targetUuid = target.getUniqueId();

			  MessageManager messageManager = plugin.getCore().getMessageManager();
			  if (targetUuid.equals(playerUuid)) {
				messageManager.sendMessage(player, Message.CANT_MESSAGE_YOURSELF);
				return;
			  }

			  HashMap<UUID, UUID> conversations = plugin.getConversations();
			  if (!conversations.containsKey(targetUuid)) {
				conversations.put(targetUuid, playerUuid);
			  }

			  String message = (String) args.get("message");

			  RankManager rankManager = plugin.getRanksPlugin().getRankManager();
			  Rank playerRank = rankManager.getPlayerRank(player);
			  Rank targetRank = rankManager.getPlayerRank(target);

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

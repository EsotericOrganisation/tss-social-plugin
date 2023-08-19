package net.slqmy.tss_social.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_social.TSSSocialPlugin;
import net.slqmy.tss_social.type.ChatType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChatCommand {

  public ChatCommand(@NotNull TSSSocialPlugin plugin) {
	TSSCorePlugin core = plugin.getCore();
	MessageManager messageManager = core.getMessageManager();

	new CommandAPICommand("chat")
			.withArguments(new StringArgument("type")
					.replaceSuggestions(ArgumentSuggestions.stringsWithTooltips((SuggestionInfo<CommandSender> info) -> {
					  Player player = (Player) info.sender();

					  ChatType[] chatTypes = ChatType.values();
					  List<StringTooltip> tooltips = new ArrayList<>(Arrays.stream(chatTypes).map((ChatType chatType) -> StringTooltip.ofString(messageManager.getPlayerMessage(chatType.getName(), player).content(), messageManager.getPlayerMessage(chatType.getDescription(), player).content())).toList());
					  tooltips.addAll(Arrays.stream(chatTypes).map((ChatType chatType) -> StringTooltip.ofString(messageManager.getPlayerMessage(chatType.getShortName(), player).content(), messageManager.getPlayerMessage(chatType.getDescription(), player).content())).toList());
					  return tooltips.toArray(StringTooltip[]::new);
					})))
			.executesPlayer((Player player, CommandArguments args) -> {
			  UUID playerUuid = player.getUniqueId();

			  HashMap<UUID, ChatType> playerChatModes = plugin.getPlayerChatModes();
			  String typeString = (String) args.get("type");

			  ChatType type = ChatType.valueOf(typeString);
			  ChatType selected = playerChatModes.get(playerUuid);
			  if (selected == null) {
				selected = ChatType.ALL_CHAT;
			  }

			  if (type == selected) {
				messageManager.sendMessage(player, Message.CHAT_MODE_ALREADY_SELECTED);
				return;
			  }

			  playerChatModes.replace(playerUuid, type);
			  messageManager.sendMessage(player, Message.CHAT_MODE_SUCCESSFULLY_SELECTED);
			})
			.register();
  }
}

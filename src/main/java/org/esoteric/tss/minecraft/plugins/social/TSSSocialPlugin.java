package org.esoteric.tss.minecraft.plugins.social;

import org.esoteric.tss.minecraft.plugins.core.TSSCorePlugin;
import org.esoteric.tss.minecraft.plugins.ranks.TSSRanksPlugin;
import org.esoteric.tss.minecraft.plugins.social.commands.ChatCommand;
import org.esoteric.tss.minecraft.plugins.social.commands.MessageCommand;
import org.esoteric.tss.minecraft.plugins.social.commands.PartyCommand;
import org.esoteric.tss.minecraft.plugins.social.commands.ReplyCommand;
import org.esoteric.tss.minecraft.plugins.social.event.listeners.ConnectionListener;
import org.esoteric.tss.minecraft.plugins.social.managers.PartyManager;
import org.esoteric.tss.minecraft.plugins.social.types.ChatType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class TSSSocialPlugin extends JavaPlugin {

  private final PluginManager pluginManager = Bukkit.getPluginManager();

  private final TSSCorePlugin core = (TSSCorePlugin) pluginManager.getPlugin("tss-core-plugin");
  private final TSSRanksPlugin ranksPlugin = (TSSRanksPlugin) pluginManager.getPlugin("tss-ranks-plugin");
  private final HashMap<UUID, UUID> conversations = new HashMap<>();
  private final HashMap<UUID, ChatType> playerChatModes = new HashMap<>();
  private PartyManager partyManager;

  public TSSCorePlugin getCore() {
	return core;
  }

  public TSSRanksPlugin getRanksPlugin() {
	return ranksPlugin;
  }

  public HashMap<UUID, UUID> getConversations() {
	return conversations;
  }

  public HashMap<UUID, ChatType> getPlayerChatModes() {
	return playerChatModes;
  }

  public PartyManager getPartyManager() {
	return partyManager;
  }

  @Override
  public void onEnable() {
	partyManager = new PartyManager();

	new MessageCommand(this);
	new ReplyCommand(this);

	new PartyCommand(this);

	new ChatCommand(this);

	pluginManager.registerEvents(new ConnectionListener(this), this);
  }
}

package net.slqmy.tss_social.type;

import net.slqmy.tss_core.datatype.player.Message;

public enum ChatType {

  ALL_CHAT(Message.ALL_CHAT, Message.ALL_CHAT_SHORT_NAME, Message.ALL_CHAT_DESCRIPTION),
  PARTY_CHAT(Message.PARTY_CHAT, Message.PARTY_CHAT_SHORT_NAME, Message.PARTY_CHAT_DESCRIPTION),
  MESSAGE_CHAT(Message.MESSAGE_CHAT, Message.MESSAGE_CHAT_SHORT_NAME, Message.MESSAGE_CHAT_DESCRIPTION);

  private final Message name;
  private final Message shortName;
  private final Message description;

  ChatType(Message name, Message shortName, Message description) {
	this.name = name;
	this.shortName = shortName;
	this.description = description;
  }

  public Message getName() {
	return name;
  }

  public Message getDescription() {
	return description;
  }

  public Message getShortName() {
	return shortName;
  }
}

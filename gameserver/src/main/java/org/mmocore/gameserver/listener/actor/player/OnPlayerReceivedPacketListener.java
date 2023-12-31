package org.mmocore.gameserver.listener.actor.player;

import org.mmocore.gameserver.listener.PlayerListener;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
@FunctionalInterface
public interface OnPlayerReceivedPacketListener extends PlayerListener {
    void onReceivedPacket(final Player activeChar, final Object... param);
}
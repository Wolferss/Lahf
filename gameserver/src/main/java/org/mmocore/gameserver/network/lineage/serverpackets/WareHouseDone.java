package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class WareHouseDone extends GameServerPacket {

    @Override
    protected void writeData() {
        writeD(0); //?
    }
}
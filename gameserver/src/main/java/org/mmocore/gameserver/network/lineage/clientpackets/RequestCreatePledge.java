package org.mmocore.gameserver.network.lineage.clientpackets;

public class RequestCreatePledge extends L2GameClientPacket {
    //Format: cS
    private String _pledgename;

    @Override
    protected void readImpl() {
        _pledgename = readS(64);
    }

    @Override
    protected void runImpl() {
        //TODO not implemented
    }
}
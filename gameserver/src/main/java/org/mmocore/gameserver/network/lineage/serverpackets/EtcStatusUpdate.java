package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class EtcStatusUpdate extends GameServerPacket {
    /**
     * Packet for lvl 3 client buff line
     * <p/>
     * Example:(C4)
     * F9 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 - empty statusbar
     * F9 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 - increased force lvl 1
     * F9 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 - weight penalty lvl 1
     * F9 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 - chat banned
     * F9 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 - Danger Area lvl 1
     * F9 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 - lvl 1 grade penalty
     * <p/>
     * packet format: cdd //and last three are ddd???
     * <p/>
     * Some test results:
     * F9 07 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 - lvl 7 increased force lvl 4 weight penalty
     * <p/>
     * Example:(C5 709)
     * F9 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 0F 00 00 00 - lvl 1 charm of courage lvl 15 Death Penalty
     * <p/>
     * <p/>
     * NOTE:
     * End of buff:
     * You must send empty packet
     * F9 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
     * to remove the statusbar or just empty value to remove some icon.
     */

    private final int IncreasedForce;
    private final int WeightPenalty;
    private final int MessageRefusal;
    private final int DangerArea;
    private final int armorExpertisePenalty;
    private final int weaponExpertisePenalty;
    private final int CharmOfCourage;
    private final int DeathPenaltyLevel;
    private final int ConsumedSouls;

    public EtcStatusUpdate(final Player player) {
        IncreasedForce = player.getIncreasedForce();
        WeightPenalty = player.getWeightPenalty();
        MessageRefusal = player.getMessageRefusal() || player.getNoChannel() != 0 || player.isBlockAll() ? 1 : 0;
        DangerArea = player.isInDangerArea() ? 1 : 0;
        armorExpertisePenalty = player.getArmorsExpertisePenalty();
        weaponExpertisePenalty = player.getWeaponsExpertisePenalty();
        CharmOfCourage = player.isCharmOfCourage() ? 1 : 0;
        DeathPenaltyLevel = player.getDeathPenalty() == null ? 0 : player.getDeathPenalty().getLevel();
        ConsumedSouls = player.getConsumedSouls();
    }

    @Override
    protected final void writeData() {
        // dddddddd
        writeD(IncreasedForce); // skill id 4271, 7 lvl
        writeD(WeightPenalty); // skill id 4270, 4 lvl
        writeD(MessageRefusal); //skill id 4269, 1 lvl
        writeD(DangerArea); // skill id 4268, 1 lvl
        writeD(weaponExpertisePenalty); // weapon grade penalty, skill 6209 in epilogue
        writeD(armorExpertisePenalty); // armor grade penalty, skill 6213 in epilogue
        writeD(CharmOfCourage); //Charm of Courage, "Prevents experience value decreasing if killed during a siege war".
        writeD(DeathPenaltyLevel); //Death Penalty max lvl 15, "Combat ability is decreased due to death."
        writeD(ConsumedSouls);
    }
}
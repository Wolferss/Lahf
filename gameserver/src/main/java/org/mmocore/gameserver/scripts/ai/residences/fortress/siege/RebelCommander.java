package org.mmocore.gameserver.scripts.ai.residences.fortress.siege;

import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.scripts.ai.residences.SiegeGuardFighter;

/**
 * @author VISTALL
 * @date 20:10/19.04.2011
 */
public class RebelCommander extends SiegeGuardFighter {
    public RebelCommander(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        super.onEvtDead(killer);

        Functions.npcSay(getActor(), NpcString.DONT_THINK_THAT_ITS_GONNA_END_LIKE_THIS);
    }
}

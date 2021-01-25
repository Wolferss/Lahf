package org.mmocore.gameserver.scripts.ai.pts.hellbound;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.ai.ScriptEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;

/**
 * @author KilRoy
 */
public class wizard_quarry_seeker extends Mystic {
    public wizard_quarry_seeker(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtScriptEvent(ScriptEvent event, Object arg1, Object arg2) {
        if (event == ScriptEvent.SCE_QUARRY_SLAVE_SEE) {
            final Creature quarry = (Creature) arg1;
            if (quarry != null) {
                getActor().getAggroList().clear();
                notifyEvent(CtrlEvent.EVT_AGGRESSION, quarry, 2);
            }
        }
    }
}
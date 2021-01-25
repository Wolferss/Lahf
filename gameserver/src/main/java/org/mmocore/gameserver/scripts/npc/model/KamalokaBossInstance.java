package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

public class KamalokaBossInstance extends LostCaptainInstance {
    private static final Logger _log = LoggerFactory.getLogger(KamalokaBossInstance.class);

    private ScheduledFuture<?> _manaRegen;

    public KamalokaBossInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        _manaRegen = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ManaRegen(), 20000, 20000);
    }

    @Override
    public boolean isRaid() {
        return false;
    }

    @Override
    protected void onDeath(Creature killer) {
        if (_manaRegen != null) {
            _manaRegen.cancel(false);
            _manaRegen = null;
        }

        super.onDeath(killer);
    }

    private class ManaRegen implements Runnable {
        @Override
        public void run() {
            try {
                for (final Player p : World.getAroundPlayers(KamalokaBossInstance.this)) {
                    if (p.isHealBlocked()) {
                        continue;
                    }
                    final int addMp = getAddMp();
                    if (addMp <= 0) {
                        return;
                    }
                    final double newMp = Math.min(Math.max(0, p.getMaxMp() - p.getCurrentMp()), addMp);
                    if (newMp > 0) {
                        p.setCurrentMp(newMp + p.getCurrentMp());
                    }
                    p.sendPacket(new SystemMessage(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addNumber(Math.round(newMp)));
                }
            } catch (Exception e) {
                _log.error("", e);
            }
        }

        private int getAddMp() {
            switch (getLevel()) {
                case 23:
                case 26:
                    return 6;
                case 33:
                case 36:
                    return 10;
                case 43:
                case 46:
                    return 13;
                case 53:
                case 56:
                    return 16; // С потолка
                case 63:
                case 66:
                    return 19; // С потолка
                case 73:
                    return 22; // С потолка
                default:
                    return 0;
            }
        }
    }
}
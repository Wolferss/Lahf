package org.mmocore.gameserver.model.instances.residences.dominion;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.geometry.Circle;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.instances.residences.SiegeFlagInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.funcs.FuncMul;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.ZoneTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.world.World;

/**
 * @author VISTALL
 * @date 14:59/09.06.2011
 * FIXME [VISTALL] возможна корекция статов
 */
public class OutpostInstance extends SiegeFlagInstance {
    private Zone _zone = null;

    public OutpostInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        final Circle c = new Circle(getLoc(), 250);
        c.setZmax(World.MAP_MAX_Z);
        c.setZmin(World.MAP_MIN_Z);

        final StatsSet set = new StatsSet();
        set.set("name", StringUtils.EMPTY);
        set.set("type", ZoneType.dummy);
        set.set("territory", new Territory().add(c));

        _zone = new Zone(new ZoneTemplate(set));
        _zone.setReflection(ReflectionManager.DEFAULT);
        _zone.addListener(new OnZoneEnterLeaveListenerImpl());
        _zone.setActive(true);
    }

    @Override
    public void onDelete() {
        super.onDelete();

        _zone.setActive(false);
        _zone = null;
    }

    @Override
    public boolean isInvul() {
        return true;
    }

    private class OnZoneEnterLeaveListenerImpl implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(final Zone zone, final Creature actor) {
            final DominionSiegeEvent siegeEvent = OutpostInstance.this.getEvent(DominionSiegeEvent.class);
            if (siegeEvent == null) {
                return;
            }

            if (actor.getEvent(DominionSiegeEvent.class) != siegeEvent) {
                return;
            }

            actor.addStatFunc(new FuncMul(Stats.REGENERATE_HP_RATE, 0x40, OutpostInstance.this, 2.));
            actor.addStatFunc(new FuncMul(Stats.REGENERATE_MP_RATE, 0x40, OutpostInstance.this, 2.));
            actor.addStatFunc(new FuncMul(Stats.REGENERATE_CP_RATE, 0x40, OutpostInstance.this, 2.));
        }

        @Override
        public void onZoneLeave(final Zone zone, final Creature actor) {
            actor.removeStatsByOwner(OutpostInstance.this);
        }
    }
}

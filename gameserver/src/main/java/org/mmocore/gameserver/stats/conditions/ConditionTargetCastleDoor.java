package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionTargetCastleDoor extends Condition {
    private final boolean _isCastleDoor;

    public ConditionTargetCastleDoor(final boolean isCastleDoor) {
        _isCastleDoor = isCastleDoor;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!optionalTarget.isPresent())
            return false;

        final Creature target = optionalTarget.get();
        return target.isDoor() == _isCastleDoor;
    }
}

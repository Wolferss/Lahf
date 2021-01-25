package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public class ConditionTargetAggro extends Condition {
    private final boolean _isAggro;

    public ConditionTargetAggro(final boolean isAggro) {
        _isAggro = isAggro;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        if (!optionalTarget.isPresent())
            return false;

        final Creature target = optionalTarget.get();

        if (target.isMonster()) {
            return ((MonsterInstance) target).isAggressive() == _isAggro;
        }
        if (target.isPlayer()) {
            return target.getKarma() > 0;
        }

        return false;
    }
}

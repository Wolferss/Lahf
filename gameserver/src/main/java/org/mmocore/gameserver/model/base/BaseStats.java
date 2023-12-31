package org.mmocore.gameserver.model.base;

import org.jts.dataparser.data.holder.PCParameterHolder;
import org.jts.dataparser.data.holder.pcparameter.common.LevelBonus;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;

public enum BaseStats {
    STR {
        @Override
        public final int getStat(final Creature actor) {
            return actor == null ? 1 : actor.getSTR();
        }

        @Override
        public final double calcBonus(final Creature actor) {
            if (actor.isPlayer()) {
                final Player player = actor.getPlayer();
                if (player != null && player.isCombatTransformed()) {
                    return player.getTransformation().getBonus(LevelBonus.str);
                }
            }
            return actor == null ? 1. : PCParameterHolder.getInstance().getStrBonus().returnValue(actor.getSTR());
        }

        @Override
        public final double calcChanceMod(final Creature actor) {
            return Math.min(2. - Math.sqrt(calcBonus(actor)), 1.); // не более 1
        }
    },
    INT {
        @Override
        public final int getStat(final Creature actor) {
            return actor == null ? 1 : actor.getINT();
        }

        @Override
        public final double calcBonus(final Creature actor) {
            if (actor.isPlayer()) {
                final Player player = actor.getPlayer();
                if (player != null && player.isCombatTransformed()) {
                    return player.getTransformation().getBonus(LevelBonus._int);
                }
            }
            return actor == null ? 1. : PCParameterHolder.getInstance().getIntBonus().returnValue(actor.getINT());
        }
    },
    DEX {
        @Override
        public final int getStat(final Creature actor) {
            return actor == null ? 1 : actor.getDEX();
        }

        @Override
        public final double calcBonus(final Creature actor) {
            if (actor.isPlayer()) {
                final Player player = actor.getPlayer();
                if (player != null && player.isCombatTransformed()) {
                    return player.getTransformation().getBonus(LevelBonus.dex);
                }
            }
            return actor == null ? 1. : PCParameterHolder.getInstance().getDexBonus().returnValue(actor.getDEX());
        }
    },
    WIT {
        @Override
        public final int getStat(final Creature actor) {
            return actor == null ? 1 : actor.getWIT();
        }

        @Override
        public final double calcBonus(final Creature actor) {
            if (actor.isPlayer()) {
                final Player player = actor.getPlayer();
                if (player != null && player.isCombatTransformed()) {
                    return player.getTransformation().getBonus(LevelBonus.wit);
                }
            }
            return actor == null ? 1. : PCParameterHolder.getInstance().getWitBonus().returnValue(actor.getWIT());
        }
    },
    CON {
        @Override
        public final int getStat(final Creature actor) {
            return actor == null ? 1 : actor.getCON();
        }

        @Override
        public final double calcBonus(final Creature actor) {
            if (actor.isPlayer()) {
                final Player player = actor.getPlayer();
                if (player != null && player.isCombatTransformed()) {
                    return player.getTransformation().getBonus(LevelBonus.con);
                }
            }
            return actor == null ? 1. : PCParameterHolder.getInstance().getConBonus().returnValue(actor.getCON());
        }
    },
    MEN {
        @Override
        public final int getStat(final Creature actor) {
            return actor == null ? 1 : actor.getMEN();
        }

        @Override
        public final double calcBonus(final Creature actor) {
            if (actor.isPlayer()) {
                final Player player = actor.getPlayer();
                if (player != null && player.isCombatTransformed()) {
                    return player.getTransformation().getBonus(LevelBonus.men);
                }
            }
            return actor == null ? 1. : PCParameterHolder.getInstance().getMenBonus().returnValue(actor.getMEN());
        }
    },
    NONE {
        @Override
        public int getStat(final Creature actor) {
            return 1;
        }

        @Override
        public double calcBonus(final Creature actor) {
            return 1.0;
        }
    };
    public static final int[][] EVASION_OR_ATTACK_ACCURACY_78_85 = new int[][]{{94, 94, 97, 99, 100, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 112, 113, 114, 115, 115, 116, 117, 117, 118, 118, 119, 120, 120, 121, 121, 122, 122, 123, 123, 124, 124, 125, 125, 126, 126, 127, 127, 128, 128, 129, 129, 130, 130, 130, 131, 131, 132, 132, 132, 133, 133, 134, 134, 134, 135, 135, 136, 136, 136, 137, 137, 138, 138, 138, 139, 139, 139, 140, 140, 140}, {97, 97, 99, 101, 103, 104, 105, 106, 108, 109, 110, 110, 111, 112, 113, 114, 115, 115, 116, 117, 117, 118, 119, 119, 120, 121, 121, 122, 122, 123, 123, 124, 125, 125, 126, 126, 127, 127, 128, 128, 129, 129, 129, 130, 130, 131, 131, 132, 132, 133, 133, 133, 134, 134, 135, 135, 135, 136, 136, 137, 137, 137, 138, 138, 139, 139, 139, 140, 140, 140, 141, 141, 142, 142, 142, 143}, {99, 99, 101, 103, 105, 106, 108, 109, 110, 111, 112, 113, 114, 114, 115, 116, 117, 118, 118, 119, 120, 120, 121, 122, 122, 123, 123, 124, 125, 125, 126, 126, 127, 127, 128, 128, 129, 129, 130, 130, 131, 131, 132, 132, 133, 133, 134, 134, 134, 135, 135, 136, 136, 136, 137, 137, 138, 138, 139, 139, 139, 140, 140, 140, 141, 141, 142, 142, 142, 143, 143, 143, 144, 144, 144, 145}, {101, 101, 104, 105, 107, 108, 110, 111, 112, 113, 114, 115, 116, 117, 117, 118, 119, 120, 120, 121, 122, 123, 123, 124, 124, 125, 126, 126, 127, 127, 128, 128, 129, 129, 130, 131, 131, 132, 132, 132, 133, 133, 134, 134, 135, 135, 136, 136, 137, 137, 137, 138, 138, 139, 139, 140, 140, 140, 141, 141, 141, 142, 142, 143, 143, 143, 144, 144, 144, 145, 145, 146, 146, 146, 147, 147}, {103, 103, 106, 108, 109, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 120, 121, 122, 123, 123, 124, 125, 125, 126, 127, 127, 128, 128, 129, 130, 130, 131, 131, 132, 132, 133, 133, 134, 134, 135, 135, 136, 136, 137, 137, 137, 138, 138, 139, 139, 140, 140, 140, 141, 141, 142, 142, 143, 143, 143, 144, 144, 144, 145, 145, 146, 146, 146, 147, 147, 147, 148, 148, 148, 149, 149}, {105, 105, 108, 110, 111, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 123, 124, 125, 126, 126, 127, 128, 128, 129, 129, 130, 131, 131, 132, 132, 133, 133, 134, 134, 135, 135, 136, 136, 137, 137, 138, 138, 139, 139, 140, 140, 141, 141, 141, 142, 142, 143, 143, 144, 144, 144, 145, 145, 146, 146, 146, 147, 147, 147, 148, 148, 149, 149, 149, 150, 150, 150, 151, 151, 151}, {108, 108, 110, 112, 114, 115, 116, 118, 119, 120, 121, 122, 122, 123, 124, 125, 126, 126, 127, 128, 128, 129, 130, 130, 131, 132, 132, 133, 133, 134, 135, 135, 136, 136, 137, 137, 138, 138, 139, 139, 140, 140, 141, 141, 141, 142, 142, 143, 143, 144, 144, 144, 145, 145, 146, 146, 147, 147, 147, 148, 148, 149, 149, 149, 150, 150, 150, 151, 151, 151, 152, 152, 153, 153, 153, 154}, {110, 110, 112, 114, 116, 117, 119, 120, 121, 122, 123, 124, 125, 125, 126, 127, 128, 129, 129, 130, 131, 131, 132, 133, 133, 134, 134, 135, 136, 136, 137, 137, 138, 138, 139, 139, 140, 140, 141, 141, 142, 142, 143, 143, 144, 144, 145, 145, 145, 146, 146, 147, 147, 148, 148, 148, 149, 149, 150, 150, 150, 151, 151, 151, 152, 152, 153, 153, 153, 154, 154, 154, 155, 155, 155, 156}};
    public static final int[][] SUMMON_EVASION_OR_ATTACK_ACCURACY_78_85 = new int[][]{{99, 99, 102, 104, 105, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 117, 118, 119, 120, 120, 121, 122, 122, 123, 123, 124, 125, 125, 126, 126, 127, 127, 128, 128, 129, 129, 130, 130, 131, 131, 132, 132, 133, 133, 134, 134, 135, 135, 135, 136, 136, 137, 137, 137, 138, 138, 139, 139, 139, 140, 140, 141, 141, 141, 142, 142, 143, 143, 143, 144, 144, 144, 145, 145, 145}, {102, 102, 104, 106, 108, 109, 110, 111, 113, 114, 115, 115, 116, 117, 118, 119, 120, 120, 121, 122, 122, 123, 124, 124, 125, 126, 126, 127, 127, 128, 128, 129, 130, 130, 131, 131, 132, 132, 133, 133, 134, 134, 134, 135, 135, 136, 136, 137, 137, 138, 138, 138, 139, 139, 140, 140, 140, 141, 141, 142, 142, 142, 143, 143, 144, 144, 144, 145, 145, 145, 146, 146, 147, 147, 147, 148}, {103, 103, 105, 107, 109, 110, 112, 113, 114, 115, 116, 117, 118, 118, 119, 120, 121, 122, 122, 123, 124, 124, 125, 126, 126, 127, 127, 128, 129, 129, 130, 130, 131, 131, 132, 132, 133, 133, 134, 134, 135, 135, 136, 136, 137, 137, 138, 138, 138, 139, 139, 140, 140, 140, 141, 141, 142, 142, 143, 143, 143, 144, 144, 144, 145, 145, 146, 146, 146, 147, 147, 147, 148, 148, 148, 149}, {106, 106, 109, 110, 112, 113, 115, 116, 117, 118, 119, 120, 121, 122, 122, 123, 124, 125, 125, 126, 127, 128, 128, 129, 129, 130, 131, 131, 132, 132, 133, 133, 134, 134, 135, 136, 136, 137, 137, 137, 138, 138, 139, 139, 140, 140, 141, 141, 142, 142, 142, 143, 143, 144, 144, 145, 145, 145, 146, 146, 146, 147, 147, 148, 148, 148, 149, 149, 149, 150, 150, 151, 151, 151, 152, 152}, {108, 108, 111, 113, 114, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 125, 126, 127, 128, 128, 129, 130, 130, 131, 132, 132, 133, 133, 134, 135, 135, 136, 136, 137, 137, 138, 138, 139, 139, 140, 140, 141, 141, 142, 142, 142, 143, 143, 144, 144, 145, 145, 145, 146, 146, 147, 147, 148, 148, 148, 149, 149, 149, 150, 150, 151, 151, 151, 152, 152, 152, 153, 153, 153, 154, 154}, {110, 110, 113, 115, 116, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 128, 129, 130, 131, 131, 132, 133, 133, 134, 134, 135, 136, 136, 137, 137, 138, 138, 139, 139, 140, 140, 141, 141, 142, 142, 143, 143, 144, 144, 145, 145, 146, 146, 146, 147, 147, 148, 148, 149, 149, 149, 150, 150, 151, 151, 151, 152, 152, 152, 153, 153, 154, 154, 154, 155, 155, 155, 156, 156, 156}, {112, 112, 114, 116, 118, 119, 120, 122, 123, 124, 125, 126, 126, 127, 128, 129, 130, 130, 131, 132, 132, 133, 134, 134, 135, 136, 136, 137, 137, 138, 139, 139, 140, 140, 141, 141, 142, 142, 143, 143, 144, 144, 145, 145, 145, 146, 146, 147, 147, 148, 148, 148, 149, 149, 150, 150, 151, 151, 151, 152, 152, 153, 153, 153, 154, 154, 154, 155, 155, 155, 156, 156, 157, 157, 157, 158}, {114, 114, 116, 118, 120, 121, 123, 124, 125, 126, 127, 128, 129, 129, 130, 131, 132, 133, 133, 134, 135, 135, 136, 137, 137, 138, 138, 139, 140, 140, 141, 141, 142, 142, 143, 143, 144, 144, 145, 145, 146, 146, 147, 147, 148, 148, 149, 149, 149, 150, 150, 151, 151, 152, 152, 152, 153, 153, 154, 154, 154, 155, 155, 155, 156, 156, 157, 157, 157, 158, 158, 158, 159, 159, 159, 160}};

    public abstract int getStat(final Creature actor);

    public abstract double calcBonus(final Creature actor);

    public double calcChanceMod(final Creature actor) {
        return 2. - Math.sqrt(calcBonus(actor));
    }
}
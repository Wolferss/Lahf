package org.jts.dataparser.data.holder.pcparameter;

import org.jts.dataparser.data.holder.PCParameterHolder;
import org.jts.dataparser.data.holder.pcparameter.bonus.LevelBonus;

/**
 * @author KilRoy
 */
public class PCParameterUtils {
    public static ClassDataInfo getClassDataInfoFor(final int classId) {
        return PCParameterHolder.getInstance().getClassDataInfo().get(classId);
    }

    public static double getLevelParameter(final int level, final LevelBonus levelBonus) {
        switch (level) {
            case 1:
                return levelBonus.lvl_1;
            case 2:
                return levelBonus.lvl_2;
            case 3:
                return levelBonus.lvl_3;
            case 4:
                return levelBonus.lvl_4;
            case 5:
                return levelBonus.lvl_5;
            case 6:
                return levelBonus.lvl_6;
            case 7:
                return levelBonus.lvl_7;
            case 8:
                return levelBonus.lvl_8;
            case 9:
                return levelBonus.lvl_9;
            case 10:
                return levelBonus.lvl_10;
            case 11:
                return levelBonus.lvl_11;
            case 12:
                return levelBonus.lvl_12;
            case 13:
                return levelBonus.lvl_13;
            case 14:
                return levelBonus.lvl_14;
            case 15:
                return levelBonus.lvl_15;
            case 16:
                return levelBonus.lvl_16;
            case 17:
                return levelBonus.lvl_17;
            case 18:
                return levelBonus.lvl_18;
            case 19:
                return levelBonus.lvl_19;
            case 20:
                return levelBonus.lvl_20;
            case 21:
                return levelBonus.lvl_21;
            case 22:
                return levelBonus.lvl_22;
            case 23:
                return levelBonus.lvl_23;
            case 24:
                return levelBonus.lvl_24;
            case 25:
                return levelBonus.lvl_25;
            case 26:
                return levelBonus.lvl_26;
            case 27:
                return levelBonus.lvl_27;
            case 28:
                return levelBonus.lvl_28;
            case 29:
                return levelBonus.lvl_29;
            case 30:
                return levelBonus.lvl_30;
            case 31:
                return levelBonus.lvl_31;
            case 32:
                return levelBonus.lvl_32;
            case 33:
                return levelBonus.lvl_33;
            case 34:
                return levelBonus.lvl_34;
            case 35:
                return levelBonus.lvl_35;
            case 36:
                return levelBonus.lvl_36;
            case 37:
                return levelBonus.lvl_37;
            case 38:
                return levelBonus.lvl_38;
            case 39:
                return levelBonus.lvl_39;
            case 40:
                return levelBonus.lvl_40;
            case 41:
                return levelBonus.lvl_41;
            case 42:
                return levelBonus.lvl_42;
            case 43:
                return levelBonus.lvl_43;
            case 44:
                return levelBonus.lvl_44;
            case 45:
                return levelBonus.lvl_45;
            case 46:
                return levelBonus.lvl_46;
            case 47:
                return levelBonus.lvl_47;
            case 48:
                return levelBonus.lvl_48;
            case 49:
                return levelBonus.lvl_49;
            case 50:
                return levelBonus.lvl_50;
            case 51:
                return levelBonus.lvl_51;
            case 52:
                return levelBonus.lvl_52;
            case 53:
                return levelBonus.lvl_53;
            case 54:
                return levelBonus.lvl_54;
            case 55:
                return levelBonus.lvl_55;
            case 56:
                return levelBonus.lvl_56;
            case 57:
                return levelBonus.lvl_57;
            case 58:
                return levelBonus.lvl_58;
            case 59:
                return levelBonus.lvl_59;
            case 60:
                return levelBonus.lvl_60;
            case 61:
                return levelBonus.lvl_61;
            case 62:
                return levelBonus.lvl_62;
            case 63:
                return levelBonus.lvl_63;
            case 64:
                return levelBonus.lvl_64;
            case 65:
                return levelBonus.lvl_65;
            case 66:
                return levelBonus.lvl_66;
            case 67:
                return levelBonus.lvl_67;
            case 68:
                return levelBonus.lvl_68;
            case 69:
                return levelBonus.lvl_69;
            case 70:
                return levelBonus.lvl_70;
            case 71:
                return levelBonus.lvl_71;
            case 72:
                return levelBonus.lvl_72;
            case 73:
                return levelBonus.lvl_73;
            case 74:
                return levelBonus.lvl_74;
            case 75:
                return levelBonus.lvl_75;
            case 76:
                return levelBonus.lvl_76;
            case 77:
                return levelBonus.lvl_77;
            case 78:
                return levelBonus.lvl_78;
            case 79:
                return levelBonus.lvl_79;
            case 80:
                return levelBonus.lvl_80;
            case 81:
                return levelBonus.lvl_81;
            case 82:
                return levelBonus.lvl_82;
            case 83:
                return levelBonus.lvl_83;
            case 84:
                return levelBonus.lvl_84;
            case 85:
                return levelBonus.lvl_85;
            case 86:
                return levelBonus.lvl_86;
            case 87:
                return levelBonus.lvl_87;
            case 88:
                return levelBonus.lvl_88;
            case 89:
                return levelBonus.lvl_89;
            case 90:
                return levelBonus.lvl_90;
            case 91:
                return levelBonus.lvl_91;
            case 92:
                return levelBonus.lvl_92;
            case 93:
                return levelBonus.lvl_93;
            case 94:
                return levelBonus.lvl_94;
            case 95:
                return levelBonus.lvl_95;
            case 96:
                return levelBonus.lvl_96;
            case 97:
                return levelBonus.lvl_97;
            case 98:
                return levelBonus.lvl_98;
            case 99:
                return levelBonus.lvl_99;
        }
        return 0;
    }
}
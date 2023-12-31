package org.jts.dataparser.data.holder.setting;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.ObjectValue;
import org.jts.dataparser.data.common.ItemName_Count;
import org.jts.dataparser.data.common.Point4;

import java.util.List;

/**
 * @author : Camelion
 * @date : 23.08.12 2:32
 * <p/>
 * Содержит в себе информацию о каком-то эвенте.
 */
public class CleftSetting {
    @ObjectArray
    public Point4[] cleft_zone_territory;
    // Точки старта синей и красной команды
    @Element(start = "red_start_point_begin", end = "red_start_point_end")
    public ListPoint redStartPoint;
    @Element(start = "blue_start_point_begin", end = "blue_start_point_end")
    public ListPoint blueStartPoint;
    // Какие-то точки для синей и красной команды
    @Element(start = "red_banish_point_begin", end = "red_banish_point_end")
    public ListPoint redBanishPoint;
    @Element(start = "blue_banish_point_begin", end = "blue_banish_point_end")
    public ListPoint blueBanishPoint;
    // Период категорий?
    @IntValue
    public int CAT_period;
    // Награда победителю
    @ObjectValue(canBeNull = false)
    public ItemName_Count winner_reward;
    // Награда проигравшему
    @ObjectValue(canBeNull = false)
    public ItemName_Count loser_reward;
    // Награда в SP победителю
    @IntValue
    public int winner_sp_bonus;
    // Награда в SP победителю
    @IntValue
    public int loser_sp_bonus;
    // Неизвестно. Возможно награда за убийство
    @IntValue
    public int kill_sp_bonus;
    @StringArray
    public String[] waiting_skill;

    public static class ListPoint {
        @IntArray(name = "point", canBeNull = false)
        public List<int[]> points;
    }
}

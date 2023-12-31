package org.jts.dataparser.data.holder.monrace;

import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author : Camelion
 * @date : 30.08.12 14:14
 */
public class MonRuner {
    @IntValue(withoutName = true)
    public int runner_id;
    @IntValue(withoutName = true)
    public int npc_class_id;
    @StringValue(withoutName = true)
    public String mon_name;
    @IntValue
    public int sys_string;
    @IntValue
    public int max_speed;
    @IntValue
    public int min_speed;
    @IntArray(splitter = ",")
    public int[] guts;
    @IntValue
    public int initial_cond;
    @IntValue
    public int initial_win;
    @IntValue
    public int initial_runcount;
    @IntValue
    public int initial_wincount;
}

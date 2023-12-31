package org.jts.dataparser.data.holder.areadata.area;

import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * @author : Camelion
 * @date : 25.08.12  14:23
 */
@ParseSuper
public class SSQZone extends DefaultArea {
    @IntValue
    private int exp_penalty_per; // штраф к опыту (в процентах)

    @EnumValue
    private OnOffZoneParam item_drop; // дроп вещей вкл/выкл

    public SSQZone(DefaultArea defaultSetting) {
        super(defaultSetting);
        exp_penalty_per = ((SSQZone) defaultSetting).exp_penalty_per;
        item_drop = ((SSQZone) defaultSetting).item_drop;
    }

    public SSQZone() {

    }
}

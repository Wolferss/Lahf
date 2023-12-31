package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.fieldcycle.FieldCycle;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;

/**
 * @author : Camelion
 * @date : 27.08.12 2:19
 */
public class FieldCycleHolder extends AbstractHolder {
    private static FieldCycleHolder ourInstance = new FieldCycleHolder();
    @Element(start = "cycle_begin", end = "cycle_end")
    public List<FieldCycle> fieldCycles;

    private FieldCycleHolder() {
    }

    public static FieldCycleHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return fieldCycles.size();
    }

    public List<FieldCycle> getFieldCycles() {
        return fieldCycles;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
}
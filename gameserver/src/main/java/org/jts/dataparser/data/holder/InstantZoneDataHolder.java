package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.factory.IObjectFactory;
import org.jts.dataparser.data.holder.instantzonedata.InstantGroup;
import org.jts.dataparser.data.holder.instantzonedata.InstantZone;
import org.jts.dataparser.data.holder.instantzonedata.entrance_cond.CheckLevelEntranceCond;
import org.jts.dataparser.data.holder.instantzonedata.entrance_cond.CheckQuestEntranceCond;
import org.jts.dataparser.data.holder.instantzonedata.entrance_cond.DefaultEntranceCond;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;

/**
 * @author : Camelion
 * @date : 27.08.12 13:54
 */
public class InstantZoneDataHolder extends AbstractHolder {
    private static InstantZoneDataHolder ourInstance = new InstantZoneDataHolder();
    @Element(start = "instantzone_begin", end = "instantzone_end")
    private List<InstantZone> instantZones;
    @Element(start = "group_begin", end = "group_end")
    private List<InstantGroup> instantGroups;

    private InstantZoneDataHolder() {
    }

    public static InstantZoneDataHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return instantZones.size() + instantGroups.size();
    }

    public List<InstantZone> getInstantZones() {
        return instantZones;
    }

    public List<InstantGroup> getInstantGroups() {
        return instantGroups;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }

    public static class EntranceCondObjectFactory implements IObjectFactory<DefaultEntranceCond> {
        @Override
        public DefaultEntranceCond createObjectFor(StringBuilder data) throws IllegalAccessException, InstantiationException {
            if (data.indexOf("check_level") >= 0)
                return new CheckLevelEntranceCond();
            else if (data.indexOf("check_quest") >= 0)
                return new CheckQuestEntranceCond();
            return null;
        }

        @Override
        public void setFieldClass(Class<?> clazz) {
            // Ignored
        }
    }
}
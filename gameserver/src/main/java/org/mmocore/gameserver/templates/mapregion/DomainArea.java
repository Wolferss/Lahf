package org.mmocore.gameserver.templates.mapregion;

import org.mmocore.gameserver.model.Territory;

public class DomainArea implements RegionData {
    private final int _id;
    private final Territory _territory;

    public DomainArea(final int id, final Territory territory) {
        _id = id;
        _territory = territory;
    }

    public int getId() {
        return _id;
    }

    @Override
    public Territory getTerritory() {
        return _territory;
    }
}

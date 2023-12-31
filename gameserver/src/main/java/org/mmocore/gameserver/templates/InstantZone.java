package org.mmocore.gameserver.templates;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.templates.spawn.SpawnTemplate;
import org.mmocore.gameserver.utils.Location;
import org.quartz.CronExpression;

import java.util.List;
import java.util.Map;

public class InstantZone {
    private final int _id;
    private final int _displayId;
    private final String _name;
    private final CronExpression _resetReuse;
    private final int _sharedReuseGroup;
    private final int _timelimit;
    private final boolean _dispelBuffs;
    private final int _minLevel;
    private final int _maxLevel;
    private final int _minParty;
    private final int _maxParty;
    private final boolean _onPartyDismiss;
    private final int _timer;
    private final List<Location> _teleportCoords;
    private final Location _returnCoords;
    private final int _mapx;
    private final int _mapy;
    private final Map<Integer, DoorInfo> _doors;
    private final Map<String, ZoneInfo> _zones;
    private final Map<String, SpawnInfo2> _spawns;
    private final List<SpawnInfo> _spawnsInfo;
    private final int _collapseIfEmpty;
    private final int _maxChannels;
    private final int _removedItemId;
    private final int _removedItemCount;
    private final boolean _removedItemNecessity;
    private final int _giveItemId;
    private final int _givedItemCount;
    private final int _requiredQuestId;
    private final boolean _setReuseUponEntry;
    private final StatsSet _addParams;
    private final InstantZoneEntryType _entryType;
    public InstantZone(final int id, final int displayId, final String name, final CronExpression resetReuse, final int sharedReuseGroup, final int timelimit, final boolean dispelBuffs, final int minLevel,
                       final int maxLevel, final int minParty, final int maxParty, final int timer, final boolean onPartyDismiss, final List<Location> tele, final Location ret, final int mapx, final int mapy,
                       final Map<Integer, DoorInfo> doors, final Map<String, ZoneInfo> zones, final Map<String, SpawnInfo2> spawns, final List<SpawnInfo> spawnsInfo,
                       final int collapseIfEmpty, final int maxChannels, final int removedItemId, final int removedItemCount, final boolean removedItemNecessity, final int giveItemId,
                       final int givedItemCount, final int requiredQuestId, final boolean setReuseUponEntry, final StatsSet params) {
        _id = id;
        _displayId = displayId;
        _name = name;
        _resetReuse = resetReuse;
        _sharedReuseGroup = sharedReuseGroup;
        _timelimit = timelimit;
        _dispelBuffs = dispelBuffs;
        _minLevel = minLevel;
        _maxLevel = maxLevel;
        _teleportCoords = tele;
        _returnCoords = ret;
        _minParty = minParty;
        _maxParty = maxParty;
        _onPartyDismiss = onPartyDismiss;
        _timer = timer;
        _mapx = mapx;
        _mapy = mapy;
        _doors = doors;
        _zones = zones;
        _spawnsInfo = spawnsInfo;
        _spawns = spawns;
        _collapseIfEmpty = collapseIfEmpty;
        _maxChannels = maxChannels;
        _removedItemId = removedItemId;
        _removedItemCount = removedItemCount;
        _removedItemNecessity = removedItemNecessity;
        _giveItemId = giveItemId;
        _givedItemCount = givedItemCount;
        _requiredQuestId = requiredQuestId;
        _setReuseUponEntry = setReuseUponEntry;
        _addParams = params;

        if (getMinParty() == 1) {
            _entryType = InstantZoneEntryType.SOLO;
        } else if (getMinParty() > 1 && getMaxParty() <= 9) {
            _entryType = InstantZoneEntryType.PARTY;
        } else if (getMinParty() == 9 && getMaxParty() > 9) {
            _entryType = InstantZoneEntryType.PARTY_OR_COMMAND_CHANNEL;
        } else if (getMaxParty() > 9) {
            _entryType = InstantZoneEntryType.COMMAND_CHANNEL;
        } else {
            throw new IllegalArgumentException("Invalid type?: " + _name);
        }
    }

    public int getId() {
        return _id;
    }

    public int getDisplayId() {
        return _displayId;
    }

    public String getName() {
        return _name;
    }

    public CronExpression getResetReuse() {
        return _resetReuse;
    }

    public boolean isDispelBuffs() {
        return _dispelBuffs;
    }

    public int getTimelimit() {
        return _timelimit;
    }

    public int getMinLevel() {
        return _minLevel;
    }

    public int getMaxLevel() {
        return _maxLevel;
    }

    public int getMinParty() {
        return _minParty;
    }

    public int getMaxParty() {
        return _maxParty;
    }

    public int getTimerOnCollapse() {
        return _timer;
    }

    public boolean isCollapseOnPartyDismiss() {
        return _onPartyDismiss;
    }

    public Location getTeleportCoord() {
        if (_teleportCoords.isEmpty()) {
            return null;
        }
        if (_teleportCoords.size() == 1)   // fast hack?
        {
            return _teleportCoords.get(0);
        }
        return _teleportCoords.get(Rnd.get(_teleportCoords.size()));
    }

    public Location getReturnCoords() {
        return _returnCoords;
    }

    public int getMapX() {
        return _mapx;
    }

    public int getMapY() {
        return _mapy;
    }

    public List<SpawnInfo> getSpawnsInfo() {
        return _spawnsInfo;
    }

    public int getSharedReuseGroup() {
        return _sharedReuseGroup;
    }

    public int getCollapseIfEmpty() {
        return _collapseIfEmpty;
    }

    public int getRemovedItemId() {
        return _removedItemId;
    }

    public int getRemovedItemCount() {
        return _removedItemCount;
    }

    public boolean getRemovedItemNecessity() {
        return _removedItemNecessity;
    }

    public int getGiveItemId() {
        return _giveItemId;
    }

    public int getGiveItemCount() {
        return _givedItemCount;
    }

    public int getRequiredQuestId() {
        return _requiredQuestId;
    }

    public boolean getSetReuseUponEntry() {
        return _setReuseUponEntry;
    }

    public int getMaxChannels() {
        return _maxChannels;
    }

    public InstantZoneEntryType getEntryType() {
        return _entryType;
    }

    public IBroadcastPacket canCreate() {
        if (ReflectionManager.getInstance().size() > ServerConfig.MAX_REFLECTIONS_COUNT) {
            return SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED;
        }
        if (ReflectionManager.getInstance().getCountByIzId(_id) >= _maxChannels) {
            return SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED;
        }

        return null;
    }

    public Map<Integer, DoorInfo> getDoors() {
        return _doors;
    }

    public Map<String, ZoneInfo> getZones() {
        return _zones;
    }

    public List<Location> getTeleportCoords() {
        return _teleportCoords;
    }

    public Map<String, SpawnInfo2> getSpawns() {
        return _spawns;
    }

    public StatsSet getAddParams() {
        return _addParams;
    }

    public static class DoorInfo {
        private final DoorTemplate _template;
        private final boolean _opened;
        private final boolean _invul;

        public DoorInfo(final DoorTemplate template, final boolean opened, final boolean invul) {
            _template = template;
            _opened = opened;
            _invul = invul;
        }

        public DoorTemplate getTemplate() {
            return _template;
        }

        public boolean isOpened() {
            return _opened;
        }

        public boolean isInvul() {
            return _invul;
        }
    }

    public static class ZoneInfo {
        private final ZoneTemplate _template;
        private final boolean _active;

        public ZoneInfo(final ZoneTemplate template, final boolean opened) {
            _template = template;
            _active = opened;
        }

        public ZoneTemplate getTemplate() {
            return _template;
        }

        public boolean isActive() {
            return _active;
        }
    }

    public static class SpawnInfo2 {
        private final List<SpawnTemplate> _template;
        private final boolean _spawned;

        public SpawnInfo2(final List<SpawnTemplate> template, final boolean spawned) {
            _template = template;
            _spawned = spawned;
        }

        public List<SpawnTemplate> getTemplates() {
            return _template;
        }

        public boolean isSpawned() {
            return _spawned;
        }
    }

    @Deprecated
    public static class SpawnInfo {
        private final int _spawnType;
        private final int _npcId;
        private final int _count;
        private final int _respawn;
        private final int _respawnRnd;
        private final List<Location> _coords;
        private final Territory _territory;

        public SpawnInfo(final int spawnType, final int npcId, final int count, final int respawn, final int respawnRnd, final Territory territory) {
            this(spawnType, npcId, count, respawn, respawnRnd, null, territory);
        }

        public SpawnInfo(final int spawnType, final int npcId, final int count, final int respawn, final int respawnRnd, final List<Location> coords) {
            this(spawnType, npcId, count, respawn, respawnRnd, coords, null);
        }

        public SpawnInfo(final int spawnType, final int npcId, final int count, final int respawn, final int respawnRnd, final List<Location> coords, final Territory territory) {
            _spawnType = spawnType;
            _npcId = npcId;
            _count = count;
            _respawn = respawn;
            _respawnRnd = respawnRnd;
            _coords = coords;
            _territory = territory;
        }

        public int getSpawnType() {
            return _spawnType;
        }

        public int getNpcId() {
            return _npcId;
        }

        public int getCount() {
            return _count;
        }

        public int getRespawnDelay() {
            return _respawn;
        }

        public int getRespawnRnd() {
            return _respawnRnd;
        }

        public List<Location> getCoords() {
            return _coords;
        }

        public Territory getLoc() {
            return _territory;
        }
    }
}
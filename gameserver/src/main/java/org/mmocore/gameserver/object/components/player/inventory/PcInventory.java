package org.mmocore.gameserver.object.components.player.inventory;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBR_AgathionEnergyInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.object.components.items.LockType;
import org.mmocore.gameserver.object.components.items.listeners.*;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.taskmanager.DelayedItemsManager;
import org.mmocore.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.util.Collection;
import java.util.Collections;

public class PcInventory extends Inventory {
    private static final int[][] arrows = {
            //
            {17}, // NG
            {1341, 22067}, // D
            {1342, 22068}, // C
            {1343, 22069}, // B
            {1344, 22070}, // A
            {1345, 22071}, // S
    };
    private static final int[][] bolts = {
            //
            {9632}, // NG
            {9633, 22144}, // D
            {9634, 22145}, // C
            {9635, 22146}, // B
            {9636, 22147}, // A
            {9637, 22148}, // S
    };
    private final Player owner;
    /**
     * FIXME Хак для обновления скиллов от эквипа при смене сабкласса
     */
    public boolean isRefresh = false;
    // locks
    private LockType _lockType = LockType.none;
    private int[] _lockItems = ArrayUtils.EMPTY_INT_ARRAY;

    public PcInventory(final Player player) {
        super(player.getObjectId());
        owner = player;

        addListener(ItemSkillsListener.getInstance());
        addListener(ItemAugmentationListener.getInstance());
        addListener(ItemEnchantOptionsListener.getInstance());
        addListener(ArmorSetListener.getInstance());
        addListener(BowListener.getInstance());
        addListener(AccessoryListener.getInstance());
    }

    @Override
    public Player getActor() {
        return owner;
    }

    @Override
    protected ItemLocation getBaseLocation() {
        return ItemLocation.INVENTORY;
    }

    @Override
    protected ItemLocation getEquipLocation() {
        return ItemLocation.PAPERDOLL;
    }

    public long getAdena() {
        ItemInstance _adena = getItemByItemId(57);
        if (_adena == null) {
            return 0;
        }
        return _adena.getCount();
    }

    /**
     * Добавляет адену игроку.<BR><BR>
     *
     * @param amount - сколько адены дать
     * @return L2ItemInstance - новое количество адены
     */
    public ItemInstance addAdena(long amount) {
        return addItem(ItemTemplate.ITEM_ID_ADENA, amount);
    }

    public boolean reduceAdena(long adena) {
        return destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, adena);
    }

    public int getPaperdollVariation1Id(int slot) {
        ItemInstance item = _paperdoll[slot];
        if (item != null && item.isAugmented()) {
            return item.getVariation1Id();

            //return item.getAugmentationId();
        }
        return 0;
    }

    public int getPaperdollVariation2Id(int slot) {
        ItemInstance item = _paperdoll[slot];
        if (item != null && item.isAugmented()) {
            return item.getVariation2Id();

            //return item.getAugmentationId();
        }
        return 0;
    }

    @Override
    public int getPaperdollItemId(int slot) {
        final Player player = getActor();

        int itemId = super.getPaperdollItemId(slot);

        if (slot == PAPERDOLL_RHAND && itemId == 0 && player.isClanAirShipDriver()) {
            itemId = 13556; // Затычка на отображение штурвала - Airship Helm
        }

        return itemId;
    }

    @Override
    public int getPaperdollVisualItemId(int slot) {
        Player player = getActor();

        int itemId = super.getPaperdollVisualItemId(slot);

        if (slot == PAPERDOLL_RHAND && itemId == 0 && player.isClanAirShipDriver())
            itemId = 13556; // Airship Helm

        return itemId;
    }

    @Override
    protected void onRefreshWeight() {
        // notify char for overload checking
        getActor().refreshOverloaded();
    }

    /**
     * Функция для валидации вещей в инвентаре.
     * Снимает все вещи, которые нельзя носить.
     * Применяется при входе в игру, смене саба, захвате замка, выходе из клана.
     */
    public void validateItems() {
        for (ItemInstance item : _paperdoll) {
            if (item != null && (ItemFunctions.checkIfCanEquip(getActor(), item) != null || !item.getTemplate().testCondition(getActor(), item))) {
                unEquipItem(item);
                getActor().sendDisarmMessage(item);
            }
        }
    }

    /**
     * FIXME[VISTALL] для скилов критично их всегда удалять и добавлять, для тригеров нет
     */
    public void validateItemsSkills() {
        final Player player = getActor();
        for (ItemInstance item : _paperdoll) {
            if (item == null || item.getTemplate().getType2() != ItemTemplate.TYPE2_WEAPON) {
                continue;
            }

            boolean needUnequipSkills = player.getWeaponsExpertisePenalty() > 0;

            if (item.getTemplate().getAttachedSkills().length > 0) {
                boolean has = player.getSkillLevel(item.getTemplate().getAttachedSkills()[0].getId()) > 0;
                if (needUnequipSkills && has) {
                    ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, player);
                } else if (!needUnequipSkills && !has) {
                    ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, player);
                }
            } else if (item.getTemplate().getEnchant4Skill() != null) {
                boolean has = player.getSkillLevel(item.getTemplate().getEnchant4Skill().getId()) > 0;
                if (needUnequipSkills && has) {
                    ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, player);
                } else if (!needUnequipSkills && !has) {
                    ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, player);
                }
            } else if (!item.getTemplate().getTriggerList().isEmpty()) {
                if (needUnequipSkills) {
                    ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, player);
                } else {
                    ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, player);
                }
            }
        }
    }

    public void refreshEquip() {
        isRefresh = true;
        for (ItemInstance item : getItems()) {
            if (item.isEquipped()) {
                int slot = item.getEquipSlot();
                _listeners.onUnequip(slot, item);
                _listeners.onEquip(slot, item);
            } else if (item.getItemType() == EtcItemType.RUNE) {
                _listeners.onUnequip(-1, item);
                _listeners.onEquip(-1, item);
            }
        }
        isRefresh = false;
    }

    /**
     * Вызывается из RequestSaveInventoryOrder
     */
    public void sort(int[][] order) {
        writeLock();
        try {
            boolean needSort = false;
            for (int[] element : order) {
                ItemInstance item = getItemByObjectId(element[0]);
                if (item == null) {
                    continue;
                }
                if (item.getLocation() != ItemLocation.INVENTORY) {
                    continue;
                }
                if (item.getLocData() == element[1]) {
                    continue;
                }
                try {
                    if (getPaperdollItem(element[1]) != null) {
                        continue;
                    }
                } catch (final Exception e) {
                } finally {
                    item.setLocData(element[1]);
                    item.setJdbcState(JdbcEntityState.UPDATED); // lazy update
                    needSort = true;
                }
            }
            if (needSort) {
                Collections.sort(_items, ItemOrderComparator.getInstance());
            }
        } finally {
            writeUnlock();
        }
    }

    public ItemInstance findArrowForBow(ItemTemplate bow) {
        int[] arrowsId = arrows[bow.getCrystalType().externalOrdinal];
        ItemInstance ret = null;
        for (int id : arrowsId) {
            if ((ret = getItemByItemId(id)) != null) {
                return ret;
            }
        }
        return null;
    }

    public ItemInstance findArrowForCrossbow(ItemTemplate xbow) {
        int[] boltsId = bolts[xbow.getCrystalType().externalOrdinal];
        ItemInstance ret = null;
        for (int id : boltsId) {
            if ((ret = getItemByItemId(id)) != null) {
                return ret;
            }
        }
        return null;
    }

    public ItemInstance findEquippedLure() {
        ItemInstance res = null;
        int last_lure = 0;
        Player owner = getActor();
        String LastLure = owner.getPlayerVariables().get(PlayerVariables.LAST_LURE);
        if (LastLure != null && !LastLure.isEmpty()) {
            last_lure = Integer.parseInt(LastLure);
        }
        for (ItemInstance temp : getItems()) {
            if (temp.getItemType() == EtcItemType.BAIT) {
                if (temp.getLocation() == ItemLocation.PAPERDOLL && temp.getEquipSlot() == PAPERDOLL_LHAND) {
                    return temp;
                } else if (last_lure > 0 && res == null && temp.getObjectId() == last_lure) {
                    res = temp;
                }
            }
        }
        return res;
    }

    public void lockItems(LockType lock, int[] items) {
        if (_lockType != LockType.none) {
            return;
        }

        _lockType = lock;
        _lockItems = items;

        getActor().sendItemList(false);
    }

    public void unlock() {
        if (_lockType == LockType.none) {
            return;
        }

        _lockType = LockType.none;
        _lockItems = ArrayUtils.EMPTY_INT_ARRAY;

        getActor().sendItemList(false);
    }

    public boolean isLockedItem(ItemInstance item) {
        switch (_lockType) {
            case allow:
                return ArrayUtils.contains(_lockItems, item.getItemId());
            case notallow:
                return !ArrayUtils.contains(_lockItems, item.getItemId());
            default:
                return false;
        }
    }

    public LockType getLockType() {
        return _lockType;
    }

    public int[] getLockItems() {
        return _lockItems;
    }

    @Override
    protected void onRestoreItem(ItemInstance item) {
        super.onRestoreItem(item);

        if (item.getItemType() == EtcItemType.RUNE) {
            _listeners.onEquip(-1, item);
        }
        if (item.isTemporalItem()) {
            item.startTimer(new LifeTimeTask(item));
        }
    }

    @Override
    protected void onAddItem(final ItemInstance item) {
        super.onAddItem(item);

        if (item.getItemType() == EtcItemType.RUNE) {
            _listeners.onEquip(-1, item);
        }

        if (item.isTemporalItem()) {
            item.startTimer(new LifeTimeTask(item));
        }

        if (item.isCursed()) {
            CursedWeaponsManager.getInstance().checkPlayer(getActor(), item);
        }
        owner.getListeners().onAddItem(item);
    }

    @Override
    protected void onRemoveItem(ItemInstance item) {
        super.onRemoveItem(item);

        getActor().getShortCutComponent().deleteShortCutByObjectId(item.getObjectId());

        if (item.getItemType() == EtcItemType.RUNE) {
            _listeners.onUnequip(-1, item);
        }

        if (item.isTemporalItem()) {
            item.stopTimer();
        }
    }

    @Override
    protected void onEquip(int slot, ItemInstance item) {
        super.onEquip(slot, item);

        if (item.isShadowItem()) {
            item.startTimer(new ShadowLifeTimeTask(item));
        }
    }

    @Override
    protected void onUnequip(int slot, ItemInstance item) {
        super.onUnequip(slot, item);

        if (item.isShadowItem()) {
            item.stopTimer();
        }
    }

    @Override
    public void restore() {
        final int ownerId = getOwnerId();

        writeLock();
        try {
            Collection<ItemInstance> items = ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(ownerId, getBaseLocation());

            for (ItemInstance item : items) {
                _items.add(item);
                onRestoreItem(item);
            }
            Collections.sort(_items, ItemOrderComparator.getInstance());

            items = ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(ownerId, getEquipLocation());

            for (ItemInstance item : items) {
                _items.add(item);
                onRestoreItem(item);
                if (item.getEquipSlot() >= PAPERDOLL_MAX) {
                    // Неверный слот - возвращаем предмет в инвентарь.
                    item.setLocation(getBaseLocation());
                    item.setLocData(0); // Немного некрасиво, но инвентарь еще весь не загружен и свободный слот не найти
                    item.setEquipped(false);
                    continue;
                }
                setPaperdollItem(item.getEquipSlot(), item);
            }
        } finally {
            writeUnlock();
        }

        DelayedItemsManager.getInstance().loadDelayed(getActor(), false);

        refreshWeight();
    }

    @Override
    public void store() {
        writeLock();
        try {
            ItemsDAO.getInstance().update(_items);
        } finally {
            writeUnlock();
        }
    }

    @Override
    protected void sendAddItem(ItemInstance item) {
        final Player player = getActor();
        player.sendPacket(new InventoryUpdate().addNewItem(item));
        if (item.getAgathionMaxEnergy() > 0) {
            player.sendPacket(new ExBR_AgathionEnergyInfo(1, item));
        }
    }

    @Override
    protected void sendModifyItem(ItemInstance item) {
        final Player player = getActor();

        player.sendPacket(new InventoryUpdate().addModifiedItem(item));
        if (item.getAgathionMaxEnergy() > 0) {
            player.sendPacket(new ExBR_AgathionEnergyInfo(1, item));
        }
    }

    @Override
    protected void sendRemoveItem(ItemInstance item) {
        getActor().sendPacket(new InventoryUpdate().addRemovedItem(item));
    }

    public void startTimers() {
    }

    public void stopAllTimers() {
        for (ItemInstance item : getItems()) {
            if (item.isShadowItem() || item.isTemporalItem()) {
                item.stopTimer();
            }
        }
    }

    protected class ShadowLifeTimeTask extends RunnableImpl {
        private final ItemInstance item;

        ShadowLifeTimeTask(ItemInstance item) {
            this.item = item;
        }

        @Override
        public void runImpl() throws Exception {
            Player player = getActor();

            if (!item.isEquipped()) {
                return;
            }

            int mana;
            synchronized (item) {
                item.setLifeTime(item.getLifeTime() - 1);
                mana = item.getShadowLifeTime();
                if (mana <= 0) {
                    destroyItem(item);
                }
            }

            SystemMessage sm = null;
            if (mana == 10) {
                sm = new SystemMessage(SystemMsg.S1S_REMAINING_MANA_IS_NOW_10);
            } else if (mana == 5) {
                sm = new SystemMessage(SystemMsg.S1S_REMAINING_MANA_IS_NOW_5);
            } else if (mana == 1) {
                sm = new SystemMessage(SystemMsg.S1S_REMAINING_MANA_IS_NOW_1);
            } else if (mana <= 0) {
                sm = new SystemMessage(SystemMsg.S1S_REMAINING_MANA_IS_NOW_0_AND_THE_ITEM_HAS_DISAPPEARED);
            } else {
                player.sendPacket(new InventoryUpdate().addModifiedItem(item));
            }

            if (sm != null) {
                sm.addItemName(item.getItemId());
                player.sendPacket(sm);
            }
        }
    }

    protected class LifeTimeTask extends RunnableImpl {
        private final ItemInstance item;

        LifeTimeTask(ItemInstance item) {
            this.item = item;
        }

        @Override
        public void runImpl() throws Exception {
            Player player = getActor();

            int left;
            synchronized (item) {
                left = item.getTemporalLifeTime();
                if (left <= 0) {
                    destroyItem(item);
                }
            }

            if (left <= 0) {
                player.sendPacket(SystemMsg.THE_LIMITEDTIME_ITEM_HAS_DISAPPEARED_BECAUSE_THE_REMAINING_TIME_RAN_OUT);
            }
        }
    }
}
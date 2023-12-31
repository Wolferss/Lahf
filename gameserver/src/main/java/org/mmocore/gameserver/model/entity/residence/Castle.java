package org.mmocore.gameserver.model.entity.residence;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.GameServer;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.CastleDAO;
import org.mmocore.gameserver.database.dao.impl.CastleHiredGuardDAO;
import org.mmocore.gameserver.database.dao.impl.ClanDataDAO;
import org.mmocore.gameserver.listeners.impl.OnCastleDataLoaded;
import org.mmocore.gameserver.manager.CastleManorManager;
import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.support.MerchantGuard;
import org.mmocore.gameserver.templates.manor.CropProcure;
import org.mmocore.gameserver.templates.manor.SeedProduction;
import org.mmocore.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArraySet;

@SuppressWarnings("unchecked")
public class Castle extends Residence {
    private static final Logger _log = LoggerFactory.getLogger(Castle.class);

    private static final String CASTLE_MANOR_DELETE_PRODUCTION = "DELETE FROM castle_manor_production WHERE castle_id=?;";
    private static final String CASTLE_MANOR_DELETE_PRODUCTION_PERIOD = "DELETE FROM castle_manor_production WHERE castle_id=? AND period=?;";
    private static final String CASTLE_MANOR_DELETE_PROCURE = "DELETE FROM castle_manor_procure WHERE castle_id=?;";
    private static final String CASTLE_MANOR_DELETE_PROCURE_PERIOD = "DELETE FROM castle_manor_procure WHERE castle_id=? AND period=?;";
    private static final String CASTLE_UPDATE_CROP = "UPDATE castle_manor_procure SET can_buy=? WHERE crop_id=? AND castle_id=? AND period=?";
    private static final String CASTLE_UPDATE_SEED = "UPDATE castle_manor_production SET can_produce=? WHERE seed_id=? AND castle_id=? AND period=?";

    private final TIntObjectMap<MerchantGuard> merchantGuards = new TIntObjectHashMap<>();
    private final Map<Integer, List> relatedFortresses = new ConcurrentSkipListMap<>();
    private final NpcString _npcStringName;
    private final Set<ItemInstance> _spawnMerchantTickets = new CopyOnWriteArraySet<>();
    private Dominion dominion;
    private List<CropProcure> _procure;
    private List<SeedProduction> _production;
    private List<CropProcure> _procureNext;
    private List<SeedProduction> _productionNext;
    private boolean _isNextPeriodApproved;
    private int _TaxPercent;
    private double _TaxRate;
    private long _treasury;
    private long _collectedShops;
    private long _collectedSeed;

    public Castle(final StatsSet set) {
        super(set);
        _npcStringName = NpcString.valueOf(1001000 + _id);
    }

    @Override
    public void init() {
        super.init();

        for (final Map.Entry<Integer, List> entry : relatedFortresses.entrySet()) {
            relatedFortresses.remove(entry.getKey());

            final List<Integer> list = entry.getValue();
            final List<Fortress> list2 = new ArrayList<>(list.size());
            for (final int i : list) {
                final Fortress fortress = ResidenceHolder.getInstance().getResidence(Fortress.class, i);
                if (fortress == null) {
                    continue;
                }

                list2.add(fortress);

                fortress.addRelatedCastle(this);
            }
            relatedFortresses.put(entry.getKey(), list2);
        }
    }

    // This method sets the castle owner; null here means give it back to NPC
    @Override
    public void changeOwner(final Clan newOwner) {
        // Если клан уже владел каким-либо замком/крепостью, отбираем его.
        if (newOwner != null) {
            if (newOwner.getHasFortress() != 0) {
                final Fortress oldFortress = ResidenceHolder.getInstance().getResidence(Fortress.class, newOwner.getHasFortress());
                if (oldFortress != null) {
                    oldFortress.changeOwner(null);
                }
            }
            if (newOwner.getCastle() != 0) {
                final Castle oldCastle = ResidenceHolder.getInstance().getResidence(Castle.class, newOwner.getCastle());
                if (oldCastle != null) {
                    oldCastle.changeOwner(null);
                }
            }
        }

        Clan oldOwner = null;
        // Если этим замком уже кто-то владел, отбираем у него замок
        if (getOwnerId() > 0 && (newOwner == null || newOwner.getClanId() != getOwnerId())) {
            // Удаляем замковые скилы у старого владельца
            removeSkills();
            getDominion().changeOwner(null);
            getDominion().removeSkills();

            // Убираем налог
            setTaxPercent(null, 0);
            oldOwner = getOwner();
            if (oldOwner != null) {
                // Переносим сокровищницу в вархауз старого владельца
                final long amount = getTreasury();
                if (amount > 0) {
                    final Warehouse warehouse = oldOwner.getWarehouse();
                    if (warehouse != null) {
                        warehouse.addItem(ItemTemplate.ITEM_ID_ADENA, amount);
                        addToTreasuryNoTax(-amount, false, false);
                        Log.add(getName() + '|' + -amount + "|Castle:changeOwner", "treasury");
                    }
                }

                // Проверяем членов старого клана владельца, снимаем короны замков и корону лорда с лидера
                for (final Player clanMember : oldOwner.getOnlineMembers(0)) {
                    if (clanMember != null && clanMember.getInventory() != null) {
                        clanMember.getInventory().validateItems();
                    }
                }

                // Отнимаем замок у старого владельца
                oldOwner.setHasCastle(0);
            }
        }

        // Выдаем замок новому владельцу
        if (newOwner != null) {
            newOwner.setHasCastle(getId());
        }

        // Сохраняем в базу
        updateOwnerInDB(newOwner);

        // Выдаем замковые скилы новому владельцу
        rewardSkills();

        update();
    }

    // This method loads castle
    @Override
    protected void loadData() {
        _TaxPercent = 0;
        _TaxRate = 0;
        _treasury = 0;
        _procure = new ArrayList<>();
        _production = new ArrayList<>();
        _procureNext = new ArrayList<>();
        _productionNext = new ArrayList<>();
        _isNextPeriodApproved = false;

        _owner = ClanDataDAO.getInstance().getOwner(this);
        CastleDAO.getInstance().select(this);
        CastleHiredGuardDAO.getInstance().load(this);
        GameServer.getInstance().globalListeners().onAction(OnCastleDataLoaded.class, l -> l.onLoad(getId()));
    }

    private void updateOwnerInDB(final Clan clan) {
        _owner = clan; // Update owner id property

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE clan_data SET hasCastle=0 WHERE hasCastle=? LIMIT 1");
            statement.setInt(1, getId());
            statement.execute();
            DbUtils.close(statement);

            if (clan != null) {
                statement = con.prepareStatement("UPDATE clan_data SET hasCastle=? WHERE clan_id=? LIMIT 1");
                statement.setInt(1, getId());
                statement.setInt(2, getOwnerId());
                statement.execute();

                clan.broadcastClanStatus(true, false, false);
            }
        } catch (Exception e) {
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public int getTaxPercent() {
        // Если печатью SEAL_STRIFE владеют DUSK то налог можно выставлять не более 5%
        if (_TaxPercent > 5 && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK) {
            _TaxPercent = 5;
        }
        return _TaxPercent;
    }

    public void setTaxPercent(final int p) {
        _TaxPercent = Math.min(Math.max(0, p), 100);
        _TaxRate = _TaxPercent / 100.0;
    }

    public int getTaxPercent0() {
        return _TaxPercent;
    }

    public long getCollectedShops() {
        return _collectedShops;
    }

    public void setCollectedShops(final long value) {
        _collectedShops = value;
    }

    public long getCollectedSeed() {
        return _collectedSeed;
    }

    public void setCollectedSeed(final long value) {
        _collectedSeed = value;
    }

    /**
     * Add amount to castle instance's treasury (warehouse).
     */
    public void addToTreasury(long amount, final boolean shop, final boolean seed) {
        if (getOwnerId() <= 0) {
            return;
        }

        if (amount == 0) {
            return;
        }

        if (amount > 1 && _id != 5 && _id != 8) // If current castle instance is not Aden or Rune
        {
            final Castle royal = ResidenceHolder.getInstance().getResidence(Castle.class, _id >= 7 ? 8 : 5);
            if (royal != null) {
                final long royalTax = (long) (amount * royal.getTaxRate()); // Find out what royal castle gets from the current castle instance's income
                if (royal.getOwnerId() > 0) {
                    royal.addToTreasury(royalTax, shop, seed); // Only bother to really add the tax to the treasury if not npc owned
                    if (_id == 5) {
                        Log.add("Aden|" + royalTax + "|Castle:adenTax", "treasury");
                    } else if (_id == 8) {
                        Log.add("Rune|" + royalTax + "|Castle:runeTax", "treasury");
                    }
                }

                amount -= royalTax; // Subtract royal castle income from current castle instance's income
            }
        }

        addToTreasuryNoTax(amount, shop, seed);
    }

    // This method add to the treasury

    /**
     * Add amount to castle instance's treasury (warehouse), no tax paying.
     */
    public void addToTreasuryNoTax(final long amount, final boolean shop, final boolean seed) {
        if (getOwnerId() <= 0) {
            return;
        }

        if (amount == 0) {
            return;
        }

        // Add to the current treasury total.  Use "-" to substract from treasury
        _treasury = Math.addExact(_treasury, amount);

        if (shop) {
            _collectedShops += amount;
        }

        if (seed) {
            _collectedSeed += amount;
        }

        setJdbcState(JdbcEntityState.UPDATED);
        update();
    }

    public int getCropRewardType(final int crop) {
        int rw = 0;
        for (final CropProcure cp : _procure) {
            if (cp.getId() == crop) {
                rw = cp.getReward();
            }
        }
        return rw;
    }

    // This method updates the castle tax rate
    public void setTaxPercent(final Player activeChar, final int taxPercent) {
        setTaxPercent(taxPercent);

        setJdbcState(JdbcEntityState.UPDATED);
        update();

        if (activeChar != null) {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.model.entity.Castle.OutOfControl.CastleTaxChangetTo").addString(getName()).addNumber(taxPercent));
        }
    }

    public double getTaxRate() {
        // Если печатью SEAL_STRIFE владеют DUSK то налог можно выставлять не более 5%
        if (_TaxRate > 0.05 && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK) {
            _TaxRate = 0.05;
        }
        return _TaxRate;
    }

    public long getTreasury() {
        return _treasury;
    }

    public void setTreasury(final long t) {
        _treasury = t;
    }

    public List<SeedProduction> getSeedProduction(final int period) {
        return period == CastleManorManager.PERIOD_CURRENT ? _production : _productionNext;
    }

    public List<CropProcure> getCropProcure(final int period) {
        return period == CastleManorManager.PERIOD_CURRENT ? _procure : _procureNext;
    }

    public void setSeedProduction(final List<SeedProduction> seed, final int period) {
        if (period == CastleManorManager.PERIOD_CURRENT) {
            _production = seed;
        } else {
            _productionNext = seed;
        }
    }

    public void setCropProcure(final List<CropProcure> crop, final int period) {
        if (period == CastleManorManager.PERIOD_CURRENT) {
            _procure = crop;
        } else {
            _procureNext = crop;
        }
    }

    public synchronized SeedProduction getSeed(final int seedId, final int period) {
        for (final SeedProduction seed : getSeedProduction(period)) {
            if (seed.getId() == seedId) {
                return seed;
            }
        }
        return null;
    }

    public synchronized CropProcure getCrop(final int cropId, final int period) {
        for (final CropProcure crop : getCropProcure(period)) {
            if (crop.getId() == cropId) {
                return crop;
            }
        }
        return null;
    }

    public long getManorCost(final int period) {
        final List<CropProcure> procure;
        final List<SeedProduction> production;

        if (period == CastleManorManager.PERIOD_CURRENT) {
            procure = _procure;
            production = _production;
        } else {
            procure = _procureNext;
            production = _productionNext;
        }

        long total = 0;
        if (production != null) {
            for (final SeedProduction seed : production) {
                total += Manor.getInstance().getSeedBuyPrice(seed.getId()) * seed.getStartProduce();
            }
        }
        if (procure != null) {
            for (final CropProcure crop : procure) {
                total += crop.getPrice() * crop.getStartAmount();
            }
        }
        return total;
    }

    // Save manor production data
    public void saveSeedData() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(CASTLE_MANOR_DELETE_PRODUCTION);
            statement.setInt(1, getId());
            statement.execute();
            DbUtils.close(statement);

            if (_production != null) {
                int count = 0;
                String query = "INSERT INTO castle_manor_production VALUES ";
                final String[] values = new String[_production.size()];
                for (final SeedProduction s : _production) {
                    values[count] = "(" + getId() + ',' + s.getId() + ',' + s.getCanProduce() + ',' + s.getStartProduce() + ',' + s.getPrice() + ',' + CastleManorManager.PERIOD_CURRENT + ')';
                    count++;
                }
                if (values.length > 0) {
                    query += values[0];
                    for (int i = 1; i < values.length; i++) {
                        query += "," + values[i];
                    }
                    statement = con.prepareStatement(query);
                    statement.execute();
                    DbUtils.close(statement);
                }
            }

            if (_productionNext != null) {
                int count = 0;
                String query = "INSERT INTO castle_manor_production VALUES ";
                final String[] values = new String[_productionNext.size()];
                for (final SeedProduction s : _productionNext) {
                    values[count] = "(" + getId() + ',' + s.getId() + ',' + s.getCanProduce() + ',' + s.getStartProduce() + ',' + s.getPrice() + ',' + CastleManorManager.PERIOD_NEXT + ')';
                    count++;
                }
                if (values.length > 0) {
                    query += values[0];
                    for (int i = 1; i < values.length; i++) {
                        query += ',' + values[i];
                    }
                    statement = con.prepareStatement(query);
                    statement.execute();
                    DbUtils.close(statement);
                }
            }
        } catch (final Exception e) {
            _log.error("Error adding seed production data for castle " + getName() + '!', e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    // Save manor production data for specified period
    public void saveSeedData(final int period) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(CASTLE_MANOR_DELETE_PRODUCTION_PERIOD);
            statement.setInt(1, getId());
            statement.setInt(2, period);
            statement.execute();
            DbUtils.close(statement);

            List<SeedProduction> prod = null;
            prod = getSeedProduction(period);

            if (prod != null) {
                int count = 0;
                String query = "INSERT INTO castle_manor_production VALUES ";
                final String[] values = new String[prod.size()];
                for (final SeedProduction s : prod) {
                    values[count] = "(" + getId() + ',' + s.getId() + ',' + s.getCanProduce() + ',' + s.getStartProduce() + ',' + s.getPrice() + ',' + period + ')';
                    count++;
                }
                if (values.length > 0) {
                    query += values[0];
                    for (int i = 1; i < values.length; i++) {
                        query += "," + values[i];
                    }
                    statement = con.prepareStatement(query);
                    statement.execute();
                    DbUtils.close(statement);
                }
            }
        } catch (final Exception e) {
            _log.error("Error adding seed production data for castle " + getName() + '!', e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    // Save crop procure data
    public void saveCropData() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(CASTLE_MANOR_DELETE_PROCURE);
            statement.setInt(1, getId());
            statement.execute();
            DbUtils.close(statement);
            if (_procure != null) {
                int count = 0;
                String query = "INSERT INTO castle_manor_procure VALUES ";
                final String[] values = new String[_procure.size()];
                for (final CropProcure cp : _procure) {
                    values[count] = "(" + getId() + ',' + cp.getId() + ',' + cp.getAmount() + ',' + cp.getStartAmount() + ',' + cp.getPrice() + ',' + cp.getReward() + ',' + CastleManorManager.PERIOD_CURRENT + ')';
                    count++;
                }
                if (values.length > 0) {
                    query += values[0];
                    for (int i = 1; i < values.length; i++) {
                        query += "," + values[i];
                    }
                    statement = con.prepareStatement(query);
                    statement.execute();
                    DbUtils.close(statement);
                }
            }
            if (_procureNext != null) {
                int count = 0;
                String query = "INSERT INTO castle_manor_procure VALUES ";
                final String[] values = new String[_procureNext.size()];
                for (final CropProcure cp : _procureNext) {
                    values[count] = "(" + getId() + ',' + cp.getId() + ',' + cp.getAmount() + ',' + cp.getStartAmount() + ',' + cp.getPrice() + ',' + cp.getReward() + ',' + CastleManorManager.PERIOD_NEXT + ')';
                    count++;
                }
                if (values.length > 0) {
                    query += values[0];
                    for (int i = 1; i < values.length; i++) {
                        query += "," + values[i];
                    }
                    statement = con.prepareStatement(query);
                    statement.execute();
                    DbUtils.close(statement);
                }
            }
        } catch (final Exception e) {
            _log.error("Error adding crop data for castle " + getName() + '!', e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    // Save crop procure data for specified period
    public void saveCropData(final int period) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(CASTLE_MANOR_DELETE_PROCURE_PERIOD);
            statement.setInt(1, getId());
            statement.setInt(2, period);
            statement.execute();
            DbUtils.close(statement);

            List<CropProcure> proc = null;
            proc = getCropProcure(period);

            if (proc != null) {
                int count = 0;
                String query = "INSERT INTO castle_manor_procure VALUES ";
                final String[] values = new String[proc.size()];

                for (final CropProcure cp : proc) {
                    values[count] = "(" + getId() + ',' + cp.getId() + ',' + cp.getAmount() + ',' + cp.getStartAmount() + ',' + cp.getPrice() + ',' + cp.getReward() + ',' + period + ')';
                    count++;
                }
                if (values.length > 0) {
                    query += values[0];
                    for (int i = 1; i < values.length; i++) {
                        query += "," + values[i];
                    }
                    statement = con.prepareStatement(query);
                    statement.execute();
                    DbUtils.close(statement);
                }
            }
        } catch (final Exception e) {
            _log.error("Error adding crop data for castle " + getName() + '!', e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void updateCrop(final int cropId, final long amount, final int period) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(CASTLE_UPDATE_CROP);
            statement.setLong(1, amount);
            statement.setInt(2, cropId);
            statement.setInt(3, getId());
            statement.setInt(4, period);
            statement.execute();
        } catch (final Exception e) {
            _log.error("Error adding crop data for castle " + getName() + '!', e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void updateSeed(final int seedId, final long amount, final int period) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(CASTLE_UPDATE_SEED);
            statement.setLong(1, amount);
            statement.setInt(2, seedId);
            statement.setInt(3, getId());
            statement.setInt(4, period);
            statement.execute();
        } catch (final Exception e) {
            _log.error("Error adding seed production data for castle " + getName() + '!', e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public boolean isNextPeriodApproved() {
        return _isNextPeriodApproved;
    }

    public void setNextPeriodApproved(final boolean val) {
        _isNextPeriodApproved = val;
    }

    public Dominion getDominion() {
        return dominion;
    }

    public void setDominion(final Dominion dominion) {
        this.dominion = dominion;
    }

    public void addRelatedFortress(final int type, final int fortress) {
        List<Integer> fortresses = relatedFortresses.get(type);
        if (fortresses == null) {
            relatedFortresses.put(type, fortresses = new ArrayList<>());
        }

        fortresses.add(fortress);
    }

    public int getDomainFortressContract() {
        final List<Fortress> list = relatedFortresses.get(Fortress.DOMAIN);
        if (list == null) {
            return 0;
        }
        for (final Fortress f : list) {
            if (f.getContractState() == Fortress.CONTRACT_WITH_CASTLE && f.getCastleId() == getId()) {
                return f.getId();
            }
        }
        return 0;
    }

    @Override
    public void update() {
        CastleDAO.getInstance().update(this);
    }

    public NpcString getNpcStringName() {
        return _npcStringName;
    }

    public Map<Integer, List> getRelatedFortresses() {
        return relatedFortresses;
    }

    public void addMerchantGuard(final MerchantGuard merchantGuard) {
        merchantGuards.put(merchantGuard.getItemId(), merchantGuard);
    }

    public MerchantGuard getMerchantGuard(final int itemId) {
        return merchantGuards.get(itemId);
    }

    public TIntObjectMap<MerchantGuard> getMerchantGuards() {
        return merchantGuards;
    }

    public Set<ItemInstance> getSpawnMerchantTickets() {
        return _spawnMerchantTickets;
    }

    @Deprecated
    public int getRewardCount() {
        return 0;
    }

    @Deprecated
    public void setRewardCount(final int rewardCount) {

    }

    @Override
    @Deprecated
    public void startCycleTask() {
    }
}
package org.mmocore.gameserver.database.dao.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 14:37/31.03.2011
 */
public class SiegePlayerDAO {
    public static final String INSERT_SQL_QUERY = "INSERT INTO siege_players(residence_id, object_id, clan_id) VALUES (?,?,?)";
    public static final String DELETE_SQL_QUERY = "DELETE FROM siege_players WHERE residence_id=? AND object_id=? AND clan_id=?";
    public static final String DELETE_SQL_QUERY2 = "DELETE FROM siege_players WHERE residence_id=?";
    public static final String SELECT_SQL_QUERY = "SELECT object_id FROM siege_players WHERE residence_id=? AND clan_id=?";
    private static final Logger _log = LoggerFactory.getLogger(SiegePlayerDAO.class);
    private static final SiegePlayerDAO _instance = new SiegePlayerDAO();

    public static SiegePlayerDAO getInstance() {
        return _instance;
    }

    public List<Integer> select(final Residence residence, final int clanId) {
        final List<Integer> set = new ArrayList<>();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, residence.getId());
            statement.setInt(2, clanId);
            rset = statement.executeQuery();
            while (rset.next()) {
                set.add(rset.getInt("object_id"));
            }
        } catch (Exception e) {
            _log.error("SiegePlayerDAO.select(Residence, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return set;
    }

    public void insert(final Residence residence, final int clanId, final int playerId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_SQL_QUERY);
            statement.setInt(1, residence.getId());
            statement.setInt(2, playerId);
            statement.setInt(3, clanId);
            statement.execute();
        } catch (Exception e) {
            _log.error("SiegePlayerDAO.insert(Residence, int, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(final Residence residence, final int clanId, final int playerId) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, residence.getId());
            statement.setInt(2, playerId);
            statement.setInt(3, clanId);
            statement.execute();
        } catch (Exception e) {
            _log.error("SiegePlayerDAO.delete(Residence, int, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(final Residence residence) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY2);
            statement.setInt(1, residence.getId());
            statement.execute();
        } catch (Exception e) {
            _log.error("SiegePlayerDAO.delete(Residence): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}

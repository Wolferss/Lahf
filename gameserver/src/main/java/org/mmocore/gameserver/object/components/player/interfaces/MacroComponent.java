package org.mmocore.gameserver.object.components.player.interfaces;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.network.lineage.serverpackets.SendMacroList;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.interfaces.Macro.L2MacroCmd;
import org.mmocore.gameserver.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class MacroComponent {
    private static final Logger _log = LoggerFactory.getLogger(MacroComponent.class);

    private final Player playerRef;
    private final Map<Integer, Macro> _macroses = new HashMap<>();
    private int _revision;
    private int _macroId;

    public MacroComponent(final Player player) {
        playerRef = player;
        _revision = 1;
        _macroId = 1000;
    }

    public Player getPlayer() {
        return playerRef;
    }

    public int getRevision() {
        return _revision;
    }

    public Macro[] getAllMacroses() {
        return _macroses.values().toArray(new Macro[_macroses.size()]);
    }

    public Macro getMacro(int id) {
        return _macroses.get(id - 1);
    }

    public void registerMacro(Macro macro) {
        if (macro.id == 0) {
            macro.id = _macroId++;
            while (_macroses.get(macro.id) != null) {
                macro.id = _macroId++;
            }
            _macroses.put(macro.id, macro);
            registerMacroInDb(macro);
        } else {
            Macro old = _macroses.put(macro.id, macro);
            if (old != null) {
                deleteMacroFromDb(old);
            }
            registerMacroInDb(macro);
        }
        sendUpdate();
    }

    public void deleteMacro(int id) {
        Macro toRemove = _macroses.get(id);
        if (toRemove != null) {
            deleteMacroFromDb(toRemove);
        }
        _macroses.remove(id);
        //		L2ShortCut[] allShortCuts = _owner.getAllShortCuts();
        //		for(L2ShortCut sc : allShortCuts) {
        //			if(sc.getId() == id && sc.getType() == L2ShortCut.TYPE_MACRO)
        //				_owner.sendPacket(new ShortCutRegister(sc.getSlot(), 0, 0, 0, sc.getPage()));
        //		}
        sendUpdate();
    }

    public void sendUpdate() {
        _revision++;
        Macro[] all = getAllMacroses();
        if (all.length == 0) {
            getPlayer().sendPacket(new SendMacroList(_revision, all.length, null));
        } else {
            for (Macro m : all) {
                getPlayer().sendPacket(new SendMacroList(_revision, all.length, m));
            }
        }
    }

    private void registerMacroInDb(Macro macro) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO character_macroses (char_obj_id,id,icon,name,descr,acronym,commands) values(?,?,?,?,?,?,?)");
            statement.setInt(1, getPlayer().getObjectId());
            statement.setInt(2, macro.id);
            statement.setInt(3, macro.icon);
            statement.setString(4, macro.name);
            statement.setString(5, macro.descr);
            statement.setString(6, macro.acronym);
            StringBuilder sb = new StringBuilder();
            for (L2MacroCmd cmd : macro.commands) {
                sb.append(cmd.type).append(',');
                sb.append(cmd.d1).append(',');
                sb.append(cmd.d2);
                if (cmd.cmd != null && !cmd.cmd.isEmpty()) {
                    sb.append(',').append(cmd.cmd);
                }
                sb.append(';');
            }
            statement.setString(7, sb.toString());
            statement.execute();
        } catch (Exception e) {
            _log.error("could not store macro: " + macro, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    /**
     * @param shortcut
     */
    private void deleteMacroFromDb(Macro macro) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM character_macroses WHERE char_obj_id=? AND id=?");
            statement.setInt(1, getPlayer().getObjectId());
            statement.setInt(2, macro.id);
            statement.execute();
        } catch (Exception e) {
            _log.error("could not delete macro:", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void restore() {
        _macroses.clear();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT char_obj_id, id, icon, name, descr, acronym, commands FROM character_macroses WHERE char_obj_id=?");
            statement.setInt(1, getPlayer().getObjectId());
            rset = statement.executeQuery();
            while (rset.next()) {
                int id = rset.getInt("id");
                int icon = rset.getInt("icon");
                String name = Strings.stripSlashes(rset.getString("name"));
                String descr = Strings.stripSlashes(rset.getString("descr"));
                String acronym = Strings.stripSlashes(rset.getString("acronym"));
                List<L2MacroCmd> commands = new ArrayList<>();
                StringTokenizer st1 = new StringTokenizer(rset.getString("commands"), ";");
                while (st1.hasMoreTokens()) {
                    StringTokenizer st = new StringTokenizer(st1.nextToken(), ",");
                    int type = Integer.parseInt(st.nextToken());
                    int d1 = Integer.parseInt(st.nextToken());
                    int d2 = Integer.parseInt(st.nextToken());
                    String cmd = "";
                    if (st.hasMoreTokens()) {
                        cmd = st.nextToken();
                    }
                    L2MacroCmd mcmd = new L2MacroCmd(commands.size(), type, d1, d2, cmd);
                    commands.add(mcmd);
                }

                Macro m = new Macro(id, icon, name, descr, acronym, commands.toArray(new L2MacroCmd[commands.size()]));
                _macroses.put(m.id, m);
            }
        } catch (Exception e) {
            _log.error("could not restore shortcuts:", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }
}
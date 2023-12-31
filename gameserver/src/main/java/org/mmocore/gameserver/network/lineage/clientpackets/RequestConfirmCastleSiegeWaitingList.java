package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.database.dao.impl.SiegeClanDAO;
import org.mmocore.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.CastleSiegeDefenderList;
import org.mmocore.gameserver.object.Player;

/**
 * @reworked VISTALL
 */
public class RequestConfirmCastleSiegeWaitingList extends L2GameClientPacket {
    private boolean _approved;
    private int _unitId;
    private int _clanId;

    @Override
    protected void readImpl() {
        _unitId = readD();
        _clanId = readD();
        _approved = readD() == 1;
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        if (player.getClan() == null) {
            return;
        }

        final Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _unitId);

        if (castle == null || player.getClan().getCastle() != castle.getId()) {
            player.sendActionFailed();
            return;
        }

        final CastleSiegeEvent siegeEvent = castle.getSiegeEvent();

        SiegeClanObject siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.DEFENDERS_WAITING, _clanId);
        if (siegeClan == null) {
            siegeClan = siegeEvent.getSiegeClan(CastleSiegeEvent.DEFENDERS, _clanId);
        }

        if (siegeClan == null) {
            return;
        }

        if ((player.getClanPrivileges() & Clan.CP_CS_MANAGE_SIEGE) != Clan.CP_CS_MANAGE_SIEGE) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_CASTLE_DEFENDER_LIST);
            return;
        }

        if (siegeEvent.isRegistrationOver()) {
            player.sendPacket(SystemMsg.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATIONS_CANNOT_BE_ACCEPTED_OR_REJECTED);
            return;
        }

        final int allSize = siegeEvent.getObjects(CastleSiegeEvent.DEFENDERS).size();
        if (allSize >= CastleSiegeEvent.MAX_SIEGE_CLANS) {
            player.sendPacket(SystemMsg.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE);
            return;
        }

        siegeEvent.removeObject(siegeClan.getType(), siegeClan);

        if (_approved) {
            siegeClan.setType(CastleSiegeEvent.DEFENDERS);
        } else {
            siegeClan.setType(CastleSiegeEvent.DEFENDERS_REFUSED);
        }

        siegeEvent.addObject(siegeClan.getType(), siegeClan);

        SiegeClanDAO.getInstance().update(castle, siegeClan);

        player.sendPacket(new CastleSiegeDefenderList(castle));
    }
}
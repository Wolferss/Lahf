package org.mmocore.gameserver.network.authcomm.channel;

import org.mmocore.commons.net.utils.Net;
import org.mmocore.commons.net.utils.NetInfo;

/**
 * @author VISTALL
 * @since 29.04.14
 */
public class ProxyServerChannel extends AbstractServerChannel {
    public ProxyServerChannel(int id, String ip, int port) {
        super(id);
        add(new NetInfo(Net.valueOf("*.*.*.*"), ip, port));
    }
}

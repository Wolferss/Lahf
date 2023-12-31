package org.mmocore.authserver.manager;

import org.mmocore.authserver.configuration.config.LoginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IpBanManager {
    private static final Logger _log = LoggerFactory.getLogger(IpBanManager.class);

    private static final IpBanManager _instance = new IpBanManager();
    private final Map<String, IpSession> ips = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private IpBanManager() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                long currentMillis = System.currentTimeMillis();

                writeLock.lock();
                try {
                    //Чистка просроченных сессий
                    IpSession session;
                    for (Iterator<IpSession> itr = ips.values().iterator(); itr.hasNext(); ) {
                        session = itr.next();
                        if (session.banExpire < currentMillis && session.lastTry < currentMillis - LoginConfig.LOGIN_TRY_TIMEOUT) {
                            itr.remove();
                        }
                    }
                } finally {
                    writeLock.unlock();
                }
            }

        }, 1000L, 1000L);
    }

    public static IpBanManager getInstance() {
        return _instance;
    }

    public boolean isIpBanned(String ip) {
        readLock.lock();
        try {
            IpSession ipsession;
            if ((ipsession = ips.get(ip)) == null) {
                return false;
            }

            return ipsession.banExpire > System.currentTimeMillis();
        } finally {
            readLock.unlock();
        }
    }

    public boolean tryLogin(String ip, boolean success) {
        writeLock.lock();
        try {
            IpSession ipsession;
            if ((ipsession = ips.get(ip)) == null) {
                ips.put(ip, ipsession = new IpSession());
            }

            long currentMillis = System.currentTimeMillis();

            if (currentMillis - ipsession.lastTry < LoginConfig.LOGIN_TRY_TIMEOUT) {
                success = false;
            }

            //Если успешный вход, и мы уложились в лимит между входами, уменьшаем количество неудачных попыток
            if (success) {
                if (ipsession.tryCount > 0) {
                    ipsession.tryCount--;
                }
            } else {
                if (ipsession.tryCount < LoginConfig.LOGIN_TRY_BEFORE_BAN) {
                    ipsession.tryCount++;
                }
            }

            ipsession.lastTry = currentMillis;

            //Превысили лимит неудачных попыток, баним IP
            if (ipsession.tryCount == LoginConfig.LOGIN_TRY_BEFORE_BAN) {
                _log.warn("IpBanManager: " + ip + " banned for " + LoginConfig.IP_BAN_TIME / 1000L + " seconds.");
                ipsession.banExpire = currentMillis + LoginConfig.IP_BAN_TIME;
                return false;
            }

            return true;
        } finally {
            writeLock.unlock();
        }
    }

    private static class IpSession {
        public int tryCount;
        public long lastTry;
        public long banExpire;
    }
}

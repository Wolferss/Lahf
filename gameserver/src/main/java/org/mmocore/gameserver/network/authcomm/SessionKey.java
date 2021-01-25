package org.mmocore.gameserver.network.authcomm;

/**
 * <p>This class is used to represent session keys used by the client to authenticate in the gameserver</p>
 * <p>A SessionKey is made up of two 8 bytes keys. One is send in the LoginOk packet and the other is sent in PlayOk</p>
 *
 * @author -Wooden-
 */
public class SessionKey {
    public final int playOkID1;
    public final int playOkID2;
    public final int loginOkID1;
    public final int loginOkID2;

    private final int hashCode;

    public SessionKey(final int loginOK1, final int loginOK2, final int playOK1, final int playOK2) {
        playOkID1 = playOK1;
        playOkID2 = playOK2;
        loginOkID1 = loginOK1;
        loginOkID2 = loginOK2;

        int hashCode = playOK1;
        hashCode *= 17;
        hashCode += playOK2;
        hashCode *= 37;
        hashCode += loginOK1;
        hashCode *= 51;
        hashCode += loginOK2;

        this.hashCode = hashCode;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() == this.getClass()) {
            final SessionKey skey = (SessionKey) o;
            return playOkID1 == skey.playOkID1 && playOkID2 == skey.playOkID2 && loginOkID1 == skey.loginOkID1 && loginOkID2 == skey.loginOkID2;
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }

    public String toString() {
        return "[playOkID1: " + playOkID1 + " playOkID2: " + playOkID2 + " loginOkID1: " + loginOkID1 + " loginOkID2: " + loginOkID2 + ']';
    }
}

/******************************************************************************
 *
 * Jacksum version 1.7.0 - checksum utility in Java
 * Copyright (C) 2001-2006 Dipl.-Inf. (FH) Johann Nepomuk Loefflmann,
 * All Rights Reserved, http://www.jonelo.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * E-mail: jonelo@jonelo.de
 *
 *****************************************************************************/

package org.mmocore.commons.crypt.algorithm;

/**
 * A class that can be used to compute the Adler32 of a data stream.
 * This implementation uses the class java.util.zip.Adler32 from the Java Standard API.
 */

public class Adler32 extends AbstractChecksum {
    private java.util.zip.Adler32 adler32 = null;

    public Adler32() {
        super();
        adler32 = new java.util.zip.Adler32();
    }

    @Override
    public void reset() {
        adler32.reset();
        length = 0;
    }

    @Override
    public void update(final byte[] buffer, final int offset, final int len) {
        adler32.update(buffer, offset, len);
        length += len;
    }

    @Override
    public void update(final int b) {
        adler32.update(b);
        length++;
    }

    @Override
    public long getValue() {
        return adler32.getValue();
    }
}

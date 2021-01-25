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

public class Read extends AbstractChecksum {

    public Read() {
        super();
        encoding = HEX;
    }

    @Override
    public void reset() {
        length = 0;
    }

    // from the Checksum interface
    @Override
    public void update(final byte[] bytes, final int offset, final int length) {
        this.length += length;
    }

    @Override
    public void update(final byte[] bytes) {
        this.length += bytes.length;
    }

    @Override
    public void update(final int b) {
        length++;
    }

    @Override
    public void update(final byte b) {
        length++;
    }

    public String toString() {
        return length + separator +
                (isTimestampWanted() ? getTimestampFormatted() + separator : "") +
                getFilename();
    }

    @Override
    public String getFormattedValue() {
        return "";
    }
}

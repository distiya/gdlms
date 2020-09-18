//
// --------------------------------------------------------------------------
//  Gurux Ltd
// 
//
//
// Filename:        $HeadURL$
//
// Version:         $Revision$,
//                  $Date$
//                  $Author$
//
// Copyright (c) Gurux Ltd
//
//---------------------------------------------------------------------------
//
//  DESCRIPTION
//
// This file is a part of Gurux Device Framework.
//
// Gurux Device Framework is Open Source software; you can redistribute it
// and/or modify it under the terms of the GNU General Public License 
// as published by the Free Software Foundation; version 2 of the License.
// Gurux Device Framework is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
// See the GNU General Public License for more details.
//
// More information of Gurux products: https://www.gurux.org
//
// This code is licensed under the GNU General Public License v2. 
// Full text may be retrieved at http://www.gnu.org/licenses/gpl-2.0.txt
//---------------------------------------------------------------------------

package org.distiya.protocol.gdlms.secure;

import org.distiya.protocol.gdlms.GXDLMSServer;
import org.distiya.protocol.gdlms.enums.InterfaceType;

/**
 * Implements secured DLMS server.
 * 
 * @author Gurux Ltd.
 */
public abstract class GXDLMSSecureServer extends GXDLMSServer {
    /**
     * Ciphering settings.
     */
    private GXCiphering ciphering;

    /**
     * Constructor.
     * 
     * @param logicalNameReferencing
     *            Is logical name referencing used.
     * @param type
     *            Interface type.
     */
    public GXDLMSSecureServer(final boolean logicalNameReferencing,
            final InterfaceType type) {
        super(logicalNameReferencing, type);
        ciphering = new GXCiphering("ABCDEFGH".getBytes());
        setCipher(ciphering);
    }

    public final GXCiphering getCiphering() {
        return ciphering;
    }

    /**
     * @return Key Encrypting Key, also known as Master key.
     */
    public final byte[] getKek() {
        return getSettings().getKek();
    }

    /**
     * @param value
     *            Key Encrypting Key, also known as Master key.
     */
    public final void setKek(final byte[] value) {
        getSettings().setKek(value);
    }
}

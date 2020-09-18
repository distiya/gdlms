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

import org.distiya.protocol.gdlms.GXByteBuffer;
import org.distiya.protocol.gdlms.GXDLMSClient;
import org.distiya.protocol.gdlms.asn.GXAsn1Converter;
import org.distiya.protocol.gdlms.enums.Authentication;
import org.distiya.protocol.gdlms.enums.InterfaceType;
import org.distiya.protocol.gdlms.objects.GXDLMSSecuritySetup;
import org.distiya.protocol.gdlms.objects.enums.CertificateEntity;
import org.distiya.protocol.gdlms.objects.enums.CertificateType;
import org.distiya.protocol.gdlms.objects.enums.SecuritySuite;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

/**
 * GXDLMSSecureClient implements secure client where all messages are secured
 * using transport security.
 * 
 * @author Gurux Ltd.
 */
public class GXDLMSSecureClient extends GXDLMSClient {
    /**
     * Ciphering settings.
     */
    private GXCiphering ciphering;

    /**
     * Constructor.
     */
    public GXDLMSSecureClient() {
        this(false);
    }

    /**
     * Constructor.
     * 
     * @param useLogicalNameReferencing
     *            Is Logical Name referencing used.
     */
    public GXDLMSSecureClient(final boolean useLogicalNameReferencing) {
        this(useLogicalNameReferencing, 16, 1, Authentication.NONE, null,
                InterfaceType.HDLC);
    }

    /**
     * Constructor.
     * 
     * @param useLogicalNameReferencing
     *            Is Logical Name referencing used.
     * @param clientAddress
     *            Server address.
     * @param serverAddress
     *            Client address.
     * @param forAuthentication
     *            Authentication type.
     * @param password
     *            Password if authentication is used.
     * @param interfaceType
     *            Object type.
     */
    public GXDLMSSecureClient(final boolean useLogicalNameReferencing,
            final int clientAddress, final int serverAddress,
            final Authentication forAuthentication, final String password,
            final InterfaceType interfaceType) {
        super(useLogicalNameReferencing, clientAddress, serverAddress,
                forAuthentication, password, interfaceType);
        ciphering = new GXCiphering("ABCDEFGH".getBytes());
        setCipher(ciphering);
    }

    /**
     * @return Ciphering settings.
     */
    public final GXCiphering getCiphering() {
        return ciphering;
    }

    public static Cipher getCipher(final boolean encrypt, final byte[] kek)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException {
        GXByteBuffer iv = new GXByteBuffer();
        // iv.set(IV);

        // iv.set(p.getSystemTitle());
        // iv.setUInt32(p.getInvocationCounter());
        SecretKeySpec eks = new SecretKeySpec(kek, "AES");
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        int mode;
        if (encrypt) {
            mode = Cipher.ENCRYPT_MODE;
        } else {
            mode = Cipher.DECRYPT_MODE;
        }
        c.init(mode, eks, new GCMParameterSpec(12 * 8, iv.array()));
        return c;
    }

    /**
     * Encrypt data using Key Encrypting Key.
     * 
     * @param kek
     *            Key Encrypting Key, also known as Master key.
     * @param data
     *            Data to encrypt.
     * @return Encrypted data.
     * @throws NoSuchPaddingException
     *             No such padding exception.
     * @throws NoSuchAlgorithmException
     *             No such algorithm exception.
     * @throws InvalidAlgorithmParameterException
     *             Invalid algorithm parameter exception.
     * @throws InvalidKeyException
     *             Invalid key exception.
     * @throws BadPaddingException
     *             Bad padding exception.
     * @throws IllegalBlockSizeException
     *             Illegal block size exception.
     */
    public static byte[] encrypt(final byte[] kek, final byte[] data)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        return GXSecure.encryptAesKeyWrapping(data, kek);
    }

    /**
     * Decrypt data using Key Encrypting Key.
     * 
     * @param kek
     *            Key Encrypting Key, also known as Master key.
     * @param data
     *            Data to decrypt.
     * @return Decrypted data.
     */
    public static byte[] decrypt(final byte[] kek, final byte[] data) {
        if (kek == null) {
            throw new NullPointerException("Key Encrypting Key");
        }
        if (kek.length < 16) {
            throw new IllegalArgumentException("Key Encrypting Key");
        }
        if (kek.length % 8 != 0) {
            throw new IllegalArgumentException("Key Encrypting Key");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }
        if (data.length < 16) {
            throw new IllegalArgumentException("data");
        }
        if (data.length % 8 != 0) {
            throw new IllegalArgumentException("data");
        }
        try {
            return GXSecure.decryptAesKeyWrapping(data, kek);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * @return Used security suite.
     */
    public SecuritySuite getSecuritySuite() {
        return ciphering.getSecuritySuite();
    }

    /**
     * @param value
     *            Used security suite.
     */
    public void setSecuritySuite(final SecuritySuite value) {
        ciphering.setSecuritySuite(value);
    }

    /**
     * @return Server system title.
     */
    public byte[] getServerSystemTitle() {
        return getSettings().getSourceSystemTitle();
    }

    /**
     * @param value
     *            Server system title.
     */
    public void setServerSystemTitle(final byte[] value) {
        getSettings().setSourceSystemTitle(value);
    }

    /**
     * Exports an X.509 v3 certificate from the server using entity information.
     * 
     * @param ss
     *            Security Setup.
     * @param type
     *            Certificate type.
     * @return Generated action.
     * @throws NoSuchPaddingException
     *             No such padding exception.
     * @throws NoSuchAlgorithmException
     *             No such algorithm exception.
     * @throws InvalidAlgorithmParameterException
     *             Invalid algorithm parameter exception.
     * @throws InvalidKeyException
     *             Invalid key exception.
     * @throws BadPaddingException
     *             Bad padding exception.
     * @throws IllegalBlockSizeException
     *             Illegal block size exception.
     */
    public final byte[][] getServerCertificate(final GXDLMSSecuritySetup ss,
            final CertificateType type)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        return ss.exportCertificateByEntity(this, CertificateEntity.SERVER,
                type, getSettings().getSourceSystemTitle());
    }

    @SuppressWarnings("squid:S00112")
    public final void parseServerCertificate(final byte[] data,
            final PublicKey pk) {
        // ephemeral public key
        GXByteBuffer data2 = new GXByteBuffer(65);
        data2.setUInt8(0);
        data2.set(data, 0, 64);
        GXByteBuffer sign = new GXByteBuffer();
        sign.set(data, 64, 64);
        // PublicKey pk = GXAsn1Converter.getPublicKey(data2.subArray(1, 64));
        getSettings().setTargetEphemeralKey(pk);
        try {
            if (!GXSecure.validateEphemeralPublicKeySignature(data2.array(),
                    sign.array(), pk)) {
                throw new IllegalArgumentException("Key agreement failed.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Get Ephemeral Keys what server has sent.
     * 
     * @param data
     *            received data from the server.
     * @param sPk
     *            Server's public key.
     */
    @SuppressWarnings("squid:S00112")
    public final void getSharedSecret(final byte[] data, final PublicKey sPk) {
        // ephemeral public key
        GXByteBuffer data2 = new GXByteBuffer(65);
        data2.setUInt8(0);
        data2.set(data, 0, 64);
        GXByteBuffer sign = new GXByteBuffer();
        sign.set(data, 64, 64);
        getSettings().setTargetEphemeralKey(null);
        try {
            if (!GXSecure.validateEphemeralPublicKeySignature(data2.array(),
                    sign.array(), sPk)) {
                throw new IllegalArgumentException("Key agreement failed.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        PublicKey ePubKs = GXAsn1Converter.getPublicKey(data2.subArray(1, 64));
        // Get shared secret
        KeyAgreement ka;
        try {
            ka = KeyAgreement.getInstance("ECDH");
            ka.init(getCiphering().getEphemeralKeyPair().getPrivate());
            ka.doPhase(ePubKs, true);
            getSettings().getCipher().setSharedSecret(ka.generateSecret());
            getSettings().setTargetEphemeralKey(ePubKs);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}

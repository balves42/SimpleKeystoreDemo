package com.balves42.simplekeystoredemo;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

public class KeystoreUtil {

    private final String CIPHER = "RSA/ECB/PKCS1Padding";
    private final String PROVIDER = "AndroidKeyStore";
    private KeyStore keyStore;

    public KeystoreUtil() {
        try {
            keyStore = KeyStore.getInstance(PROVIDER);
            keyStore.load(null);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            //TODO: Deal with the error here
        }
    }

    public Enumeration<String> getAliases() throws KeyStoreException {
        return keyStore.aliases();
    }

    public boolean generateKeys(Context context, String alias) {
        try {
            // Create new key if needed
            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", PROVIDER);
                generator.initialize(spec);
                generator.generateKeyPair();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean deleteKey(String alias) {
        try {
            keyStore.deleteEntry(alias);
        } catch (KeyStoreException e) {
            return false;
        }
        return true;
    }

    public String cipherText(String initialText, String alias) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
            Cipher inCipher = Cipher.getInstance(CIPHER);
            inCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, inCipher);
            cipherOutputStream.write(initialText.getBytes(StandardCharsets.UTF_8));
            cipherOutputStream.close();
            byte[] vals = outputStream.toByteArray();
            return Base64.encodeToString(vals, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    public String decipherText(String cipherText, String alias) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
            Cipher output = Cipher.getInstance(CIPHER);
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
            CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1)
                values.add((byte) nextByte);
            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++)
                bytes[i] = values.get(i);
            return new String(bytes, 0, bytes.length, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }
}

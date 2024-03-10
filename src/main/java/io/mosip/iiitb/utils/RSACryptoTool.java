package io.mosip.iiitb.utils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class RSACryptoTool {

    final PrivateKey pk;
    Cipher cipher = null;


    public RSACryptoTool(
            String privateKeyFileLocation
    ) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        this.pk = loadPrivateKey(privateKeyFileLocation);
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, this.pk, oaepParams);
            this.cipher = cipher;
        } catch (NoSuchAlgorithmException ex) {
            System.err.println(ex);
            System.exit(1);
        } catch (NoSuchPaddingException ex) {
            System.err.println(ex);
            System.exit(1);
        } catch (InvalidKeyException ex) {
            System.err.println(ex);
            System.exit(1);
        } catch (InvalidAlgorithmParameterException ex) {
            System.err.println(ex);
            System.exit(1);
        }
    }
    public byte[] decryptData(byte[] encryptedData) throws Exception {
        return this.cipher.doFinal(encryptedData);
    }

    private PrivateKey loadPrivateKey(String privateKeyPath) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        Path privateKeyLocation = Paths.get(privateKeyPath);
        String pkContents = new String(Files.readAllBytes(privateKeyLocation))
                .replaceAll("\\n", "")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "");

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(
            Base64.getDecoder().decode(pkContents)
        );
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
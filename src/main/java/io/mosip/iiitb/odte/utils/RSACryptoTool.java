package io.mosip.iiitb.odte.utils;

import com.google.inject.Inject;
import io.mosip.iiitb.odte.config.OnDemandAppConfig;
import org.slf4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
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
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSACryptoTool {
    final PrivateKey pk;
    Cipher cipher = null;
    final Logger logger;

    @Inject
    public RSACryptoTool(
        OnDemandAppConfig onDemandAppConfig,
        Logger logger
    ) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        this.logger = logger;
        String privateKeyFileLocation = onDemandAppConfig.privateKeyFileLocation();
        this.pk = loadPrivateKey(privateKeyFileLocation);
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, this.pk, oaepParams);
            this.cipher = cipher;
        } catch (NoSuchAlgorithmException ex) {
            logger.error(ex.getMessage());
            System.exit(11);
        } catch (NoSuchPaddingException ex) {
            logger.error(ex.getMessage());
            System.exit(12);
        } catch (InvalidKeyException ex) {
            logger.error(ex.getMessage());
            System.exit(13);
        } catch (InvalidAlgorithmParameterException ex) {
            logger.error(ex.getMessage());
            System.exit(14);
        }
    }
    public byte[] decryptData(byte[] encryptedData) throws Exception {
        return this.cipher.doFinal(encryptedData);
    }

    public byte[] encryptData(
            byte[] data,
            String publicKeyPath
    ) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        PublicKey pubKey = loadPublicKey(publicKeyPath);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey, oaepParams);
        byte[] encrypted = cipher.doFinal(data);
        return encrypted;
    }

    private String readKeyContents(String keyPath) {
        Path privateKeyLocation = Paths.get(keyPath);
        try {
            byte[] pkContentsRaw = Files.readAllBytes(privateKeyLocation);
            String pkContents = new String(pkContentsRaw)
                    .replaceAll("\\n", "")
                    .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "");
            return pkContents;
        } catch (IOException ex) {
            this.logger.error(
                    "Likely failed to locate key in given path.\nPath = {} ",
                    keyPath
            );
            System.exit(30);
        }
        return "";
    }

    private PublicKey loadPublicKey(String publicKeyPath) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String keyContents = readKeyContents(publicKeyPath);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(
                Base64.getDecoder().decode(keyContents)
        );
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private PrivateKey loadPrivateKey(String privateKeyPath) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String pkContents = readKeyContents(privateKeyPath);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(
            Base64.getDecoder().decode(pkContents)
        );
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}

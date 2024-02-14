package io.mosip.iiitb.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utilities {
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    public static String bytesToHex(final byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = (byte) HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = (byte) HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    public static String generateIdHash(
            final String id,
            final String salt
    ) throws NoSuchAlgorithmException {
        String message = String.format("%s%s", id, salt);
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] utf8Message = message.getBytes(StandardCharsets.UTF_8);
        byte[] digest = sha256.digest(utf8Message);
        String idHash = bytesToHex(digest);
        return idHash;
    }
}

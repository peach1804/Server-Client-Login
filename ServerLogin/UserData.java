import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.*;

public class UserData {

    private static  HashMap<String, String> passwordMap = new HashMap<>();
    private static HashMap<String, String> saltMap = new HashMap<>();

    // create a salt and store as a byte array
    // generate hash for salted password and store as byte array
    // add hexadecimal salt and salted password hash to hashmaps
    public static void createUser(String username, String password) throws NoSuchAlgorithmException {

        byte[] newSalt = getSalt();
        byte[] newHash = getSaltedHash(password, newSalt);

        passwordMap.put(username, toHex(newHash));
        saltMap.put(username, toHex(newSalt));
    }

    public static boolean checkUser(String username) {
        return passwordMap.containsKey(username);
    }

    // convert the hexadecimal salt from string to byte array
    // generate salted password hash from password attempt and convert from byte array to hexadecimal string
    // if stored salted password hash is equal to the hash of password attempt + stored salt, return true
    public static boolean checkLogin(String username, String password) throws NoSuchAlgorithmException {

        String salt = saltMap.get(username);
        byte[] byteSalt = fromHex(salt);
        String saltedPasswordHash = toHex(getSaltedHash(password, byteSalt));

        if (saltedPasswordHash.equals(passwordMap.get(username))) {
            return true;
        } else {
            return false;
        }
    }

    // return byte array of randomly generated salt number
    public static byte[] getSalt() {

        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);

        return salt;
    }

    // use SHA-256 to create a hash of password string and salt byte array
    // return salted password hash as byte array
    public static byte[] getSaltedHash(String password, byte[] salt) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashBytes = md.digest(password.getBytes());
        md.reset();

        return hashBytes;
    }

    // convert a byte array to a hexadecimal number
    public static String toHex(byte[] bytes) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    // convert a hexadecimal number to a byte array
    public static byte[] fromHex(String hex) {

        byte[] hexBytes = new byte[hex.length() / 2];

        for (int i = 0; i < hexBytes.length; i++) {
            hexBytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return hexBytes;
    }
}

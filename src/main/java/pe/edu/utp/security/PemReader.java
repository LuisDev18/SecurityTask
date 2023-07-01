package pe.edu.utp.security;


import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class PemReader {
  private static final String PRIVATE_KEY = "JWT_RS512_PRIVATE_KEY";
  private static final String PUBLIC_KEY = "JWT_RS512_PUBLIC_KEY";

  public PrivateKey getPrivateKey() {
    String privatePemKey =
        System.getenv(PRIVATE_KEY)
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");

    byte[] binaryEncoded = Base64.getDecoder().decode(privatePemKey);

    try {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(binaryEncoded);
      return keyFactory.generatePrivate(spec);

    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }

  public PublicKey getPublicKey() {
    String publicPemKey =
        System.getenv(PUBLIC_KEY)
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");

    byte[] binaryEncoded = Base64.getDecoder().decode(publicPemKey);

    try {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec spec = new X509EncodedKeySpec(binaryEncoded);
      return keyFactory.generatePublic(spec);

    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }
}

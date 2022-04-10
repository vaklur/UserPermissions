package cz.vaklur.user_permissions

import android.util.Base64
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * Class that contains functions for symmetric and asymmetric cryptography
 */
class Cryptography(private val pubKeyFile: String) {

    /**
     * Function for encrypt data with RSA algorithm and saved public key
     */
    fun encryptData(dataToEncrypt: String): String {
        // public key generator
        val publicKeyPEM = pubKeyFile.replace("-----BEGIN PUBLIC KEY-----", "")
            .replace(System.lineSeparator(), "")
            .replace("-----END PUBLIC KEY-----", "")
        val encoded: ByteArray = Base64.decode(publicKeyPEM, Base64.DEFAULT)
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = X509EncodedKeySpec(encoded)
        val publicKey = keyFactory.generatePublic(keySpec)

        //Encrypt input data
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        var encryptedBytes =
            Base64.encodeToString(cipher.doFinal(dataToEncrypt.toByteArray()), Base64.DEFAULT)
        encryptedBytes = encryptedBytes.replace(System.lineSeparator(), "")
        return encryptedBytes
    }
}
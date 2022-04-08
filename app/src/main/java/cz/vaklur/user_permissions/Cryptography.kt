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
    fun encryptData(dataToEncrypt:String):String{
        // public key generator
        val publicKeyPEM = pubKeyFile.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace(System.lineSeparator(),"")
                .replace("-----END PUBLIC KEY-----", "")
        val encoded: ByteArray = Base64.decode(publicKeyPEM, Base64.DEFAULT)
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = X509EncodedKeySpec(encoded)
        val publicKey = keyFactory.generatePublic(keySpec)

        //Encrypt input data
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
        cipher.init(Cipher.ENCRYPT_MODE,publicKey)
        var encryptedBytes = Base64.encodeToString(cipher.doFinal(dataToEncrypt.toByteArray()), Base64.DEFAULT)
        encryptedBytes = encryptedBytes.replace(System.lineSeparator(),"")
        return encryptedBytes
    }


    // UPRAVIT!!!!
    /*
    fun generateAESKey(context: Context) {
        val generator = KeyGenerator.getInstance("AES")
        generator.init(256)
        val key = generator.generateKey()
        saveSecretKey(key,context)
    }

    fun saveSecretKey (secretKey: SecretKey,context:Context) {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(secretKey)
        val strToSave = String(android.util.Base64.encode(baos.toByteArray(), android.util.Base64.DEFAULT))
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPref.edit()
        editor.putString("secret_key", strToSave)
        editor.apply()
    }

    fun getSavedSecretKey(context: Context): SecretKey {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val strSecretKey = sharedPref.getString("secret_key", "")
        val bytes = android.util.Base64.decode(strSecretKey, android.util.Base64.DEFAULT)
        val ois = ObjectInputStream(ByteArrayInputStream(bytes))
        val secretKey = ois.readObject() as SecretKey
        return secretKey
    }

    fun encryptDataAES(dataToEncrypt: String,context: Context):String{
        val plainText = dataToEncrypt.toByteArray(Charsets.UTF_8)
        val key = getSavedSecretKey(context)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val cipherText = cipher.doFinal(plainText)

        val sb = StringBuilder()
        for (b in cipherText) {
            sb.append(b.toChar())
        }
        return sb.toString()
    }*/
}
package com.example.monitorserver.utils;




import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AesUtil {

    private static final String AES = "AES";
    private static final String UTF8="utf-8";

    public static String getAESKey(int size,String secure) throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance(AES);

        /**可以根据参数生成固定的秘钥*/
        if (null == secure || secure.equals("")){
            kgen.init(size, new SecureRandom());
        }else{
            kgen.init(size, new SecureRandom(secure.getBytes()));
        }

        /**生成秘钥并转base64编码*/
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        String encodeKey = Base64.getEncoder().encodeToString(enCodeFormat);
        return encodeKey;
    }



    public static String encrypt(String message, String key) throws Exception {
//        /**base64解码秘钥 并转换为AES专用密钥*/
//        SecretKeySpec key = new SecretKeySpec(Base64.getDecoder().decode(aesKey), AES);
//        Cipher cipher = Cipher.getInstance(AES);
//        byte[] byteContent = content.getBytes(UTF8);
//        /**初始化为加密模式的密码器 */
//        cipher.init(Cipher.ENCRYPT_MODE, key);
//        /**加密并转出base64返回密文*/
//        byte[] result = cipher.doFinal(byteContent);
//        return Base64.getEncoder().encodeToString(result);


//        byte[] raw = aesKey.getBytes();
//        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
//        Cipher cipher = Cipher.getInstance(AES);
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//        byte[] encrypted = cipher.doFinal(content.getBytes());
//        return Base64.getEncoder().encodeToString(encrypted);





        final String cipherMode = "AES/ECB/PKCS5Padding";
        final String charsetName = "UTF-8";
        try {
            byte[] content = new byte[0];
            content = message.getBytes(charsetName);
            //
            byte[] keyByte = key.getBytes(charsetName);
            SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");

            Cipher cipher = Cipher.getInstance(cipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] data = cipher.doFinal(content);
            final Base64.Encoder encoder = Base64.getEncoder();
            final String result = encoder.encodeToString(data);
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String decrypt(String content, String aesKey) throws Exception {
//        /**将秘钥,密文 base64字节*/
//        byte[] decodeKey = Base64.getDecoder().decode(aesKey);
//        byte[] decodeContent = Base64.getDecoder().decode(content);
//        /**转换为AES专用密钥*/
//        SecretKeySpec key = new SecretKeySpec(decodeKey, AES);
//        /**创建 初始化 容器*/
//        Cipher cipher = Cipher.getInstance(AES);
//        cipher.init(Cipher.DECRYPT_MODE, key);
//        byte[] result = cipher.doFinal(decodeContent);
//        /**返回明文*/
//        return new String(result,UTF8);



//        //base64格式的key字符串转byte
//        byte[] decodeBase64 = org.apache.tomcat.util.codec.binary.Base64.decodeBase64(content);
//
//        //设置Cipher对象
//        Cipher cipher = Cipher.getInstance(AES);
//        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey.getBytes(), AES));
//
//        //调用doFinal
//        // 转base64
//        return new String(cipher.doFinal(decodeBase64),UTF8);





//        byte[] raw = aesKey.getBytes();
//        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
//        Cipher cipher = Cipher.getInstance(AES);
//        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//        byte[] decodeContent = Base64.getDecoder().decode(content);
//        byte[] original = cipher.doFinal(decodeContent);
//        String originalString = new String(original);
//        return originalString;




        final String cipherMode = "AES/ECB/PKCS5Padding";
        final String charsetName = "UTF-8";
        try {
            final Base64.Decoder decoder = Base64.getDecoder();
            byte[] messageByte = decoder.decode(content);

            //
            byte[] keyByte = aesKey.getBytes(charsetName);
            SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");

            Cipher cipher = Cipher.getInstance(cipherMode);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] content1 = cipher.doFinal(messageByte);
            String result = new String(content1, charsetName);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
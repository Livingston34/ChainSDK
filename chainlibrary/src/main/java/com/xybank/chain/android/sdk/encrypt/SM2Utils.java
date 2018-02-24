package com.xybank.chain.android.sdk.encrypt;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;

public class SM2Utils {
    // 生成随机秘钥对
    public static KeyPair generateKeyPair() {
        SM2 sm2 = SM2.Instance();
        AsymmetricCipherKeyPair key = sm2.ecc_key_pair_generator.generateKeyPair();
        ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) key.getPrivate();
        ECPublicKeyParameters ecpub = (ECPublicKeyParameters) key.getPublic();
        BigInteger privateKey = ecpriv.getD();
        ECPoint publicKey = ecpub.getQ();
        KeyPair keyPair = new KeyPair();
        keyPair.setPrivateKey(Util.byteToHex(privateKey.toByteArray()));
        keyPair.setPublicKey(Util.byteToHex(publicKey.getEncoded()));
        return keyPair;
    }

    // 数据加密
    public static String encrypt(byte[] publicKey, byte[] data) throws IOException {
        if (publicKey == null || publicKey.length == 0) {
            return null;
        }

        if (data == null || data.length == 0) {
            return null;
        }

        byte[] source = new byte[data.length];
        System.arraycopy(data, 0, source, 0, data.length);

        Cipher cipher = new Cipher();
        SM2 sm2 = SM2.Instance();
        ECPoint userKey = sm2.ecc_curve.decodePoint(publicKey);

        ECPoint c1 = cipher.Init_enc(sm2, userKey);
        cipher.Encrypt(source);
        byte[] c3 = new byte[32];
        cipher.Dofinal(c3);

        // System.out.println("C1 " + Util.byteToHex(c1.getEncoded()));
        // System.out.println("C2 " + Util.byteToHex(source));
        // System.out.println("C3 " + Util.byteToHex(c3));
        // C1 C2 C3拼装成加密字串
        return Util.byteToHex(c1.getEncoded()) + Util.byteToHex(source) + Util.byteToHex(c3);

    }

    // 数据解密
    public static byte[] decrypt(byte[] privateKey, byte[] encryptedData) throws IOException {
        if (privateKey == null || privateKey.length == 0) {
            return null;
        }

        if (encryptedData == null || encryptedData.length == 0) {
            return null;
        }
        // 加密字节数组转换为十六进制的字符串 长度变为encryptedData.length * 2
        String data = Util.byteToHex(encryptedData);
        /***
         * 分解加密字串 （C1 = C1标志位2位 + C1实体部分128位 = 130） （C3 = C3实体部分64位 = 64） （C2 =
         * encryptedData.length * 2 - C1长度 - C2长度）
         */
        byte[] c1Bytes = Util.hexToByte(data.substring(0, 130));
        int c2Len = encryptedData.length - 97;
        byte[] c2 = Util.hexToByte(data.substring(130, 130 + 2 * c2Len));
        byte[] c3 = Util.hexToByte(data.substring(130 + 2 * c2Len, 194 + 2 * c2Len));

        SM2 sm2 = SM2.Instance();
        BigInteger userD = new BigInteger(1, privateKey);

        // 通过C1实体字节来生成ECPoint
        ECPoint c1 = sm2.ecc_curve.decodePoint(c1Bytes);
        Cipher cipher = new Cipher();
        cipher.Init_dec(userD, c1);
        cipher.Decrypt(c2);
        cipher.Dofinal(c3);

        // 返回解密结果
        return c2;
    }

    public static void main(String[] args) throws Exception {
        // 生成密钥对
        generateKeyPair();

        String plainText = "0412A25D8D7F3D9D339C94AB4BCFC5016D832C1CEB2BCCC22207B16C216CAFF5738271C536BB2F32C82E2FB80D6A5B364091828F258ED3C09160C9942F8DE4A39B1FEC4FBFB1A1DD1E581959BA1B6CFF7AD68DE43CDDB6EC432F5E878BB22E7E7F18D601CE1EDF8AAD74CC43961C19565976760C142A6086F1103F3568C6221D7A82702AED43310AE79913A1F8205791192AC8DB51BFD11BD4846B1D5A615936174AF2420E98DA50CE7472A3AEA2D6C2D43D71A3B3DE6BFFF65721F717D5422ADFE04AFEF6E6D8566CE111F8F8BB288EDCC9B932EC0836D1C2A05B8B44901D9846CA9234F78BFE91D8EAAAB90C61AE755302ED58FBB2DDACB594D222D1A64F2EE85829FBF31311A00E62BD85324E7590D14A84FB34F5347C45D195FFDD9B047CFD6462BD3824900EA239B4B9327B5424132956425E145512BEEBA500DA4D62FFDF43BBE2C72415D0363710AC3F5EDF8C40C08B1CC410B404D5484A5A0ECBBFE1C9094265A3D3A3073990624E096C2F51C1E784B83998725F89CDFA9452899B62E7F5366A771467AFA4F46A0F9CC5657AD38F5D2BB15D5E42F5357022DFECCFEDA3795A05D8C17F";
        byte[] sourceData = plainText.getBytes();
//
//        // 下面的秘钥可以使用generateKeyPair()生成的秘钥内容
//
//        // generateKeyPair();
//
//        SM2 sm2 = SM2.Instance();
//        AsymmetricCipherKeyPair key = sm2.ecc_key_pair_generator.generateKeyPair();
//        ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) key.getPrivate();
//        ECPublicKeyParameters ecpub = (ECPublicKeyParameters) key.getPublic();
//        BigInteger privateKey = ecpriv.getD();
//        ECPoint publicKey = ecpub.getQ();
//        
//        System.out.println(Util.byteToHex(privateKey.toByteArray()));
//        System.out.println(Util.byteToHex(publicKey.getEncoded()));
//        
        // 国密规范正式私钥
        String prik = "72A617B204A6870954354BDB8AAEF17783E7677F163960A0F844BF4831164356";//Util.byteToHex(privateKey.toByteArray());
//        System.out.println(prik);
        // 国密规范正式公钥
        String pubk = "041A511DDBD4ECF7AEFE15E66EB48E48FE1A8DA6C73AFB232B029FE604858E9F841A30B3878553B8892623334BDAA80A029AFA9439BD9C08BEBCF345CDFAFF1307";//Util.byteToHex(publicKey.getEncoded());
        System.out.println(pubk);
        // generateKeyPair();
        System.out.println("加密: ");
        String cipherText = SM2Utils.encrypt(Util.hexToByte(pubk), sourceData);
        System.out.println(cipherText);
        System.out.println("解密: ");
        plainText = new String(SM2Utils.decrypt(Util.hexToByte(prik), Util.hexToByte(plainText)));
        System.out.println(plainText);
    }
}

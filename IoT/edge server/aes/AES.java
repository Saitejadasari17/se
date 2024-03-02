package com;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
public class AES {
	
	public static byte[] encrypt(byte input[],byte keys[])throws Exception{
		byte akey[] = new byte[16];
		for(int i=0;i<16;i++){
			akey[i] = keys[i];
		}
		SecretKeySpec key = new SecretKeySpec(akey,0,akey.length,"AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
	    byte encbytes[] = cipher.doFinal(input);
		return encbytes;
	}
	public static byte[] decrypt(byte[] enc,byte keys[])throws Exception {
		byte akey[] = new byte[16];
		for(int i=0;i<16;i++){
			akey[i] = keys[i];
		}
		SecretKeySpec key = new SecretKeySpec(akey,0,akey.length,"AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte decbytes[] = cipher.doFinal(enc);
		return decbytes;
	}
	
}         
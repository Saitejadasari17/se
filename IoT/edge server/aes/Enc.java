package com;
import org.apache.commons.codec.binary.Base64;
public class Enc {  
public static String encrypt(String unencryptedString) {  
	String encryptedString = null;  
	try {  
		byte[] encodedBytes = Base64.encodeBase64(unencryptedString.getBytes());
		encryptedString = new String(encodedBytes);
	} catch (Exception e) {  
		System.out.println(e.getMessage()); 
	}  
		return encryptedString;  
}  
public static String decrypt(String encryptedString) {  
		String decryptedText=null;  
	try {  
		byte[] encodedBytes =encryptedString.getBytes();
		decryptedText = new String(Base64.decodeBase64(encodedBytes));
	} catch (Exception e) {  
		System.out.println(e.getMessage());  
	}  
		return decryptedText;  
}  
} 

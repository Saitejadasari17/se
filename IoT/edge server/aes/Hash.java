package com;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import org.apache.commons.codec.binary.Base64;
public class Hash{
public static String getHash(byte[] msg,byte key[])throws Exception{
	SecretKeySpec skey = new SecretKeySpec(key,"HmacSHA1");
	Mac mac = Mac.getInstance("HmacSHA1");
	mac.init(skey);
	byte[] bytes = mac.doFinal(msg);
	return new String(Base64.encodeBase64(bytes));
}
}
package com.example.travelhana.Util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class CryptoUtil {

	private static final String ALGORITHM = "AES";
	private static final String MODE_PADDING = "AES/CBC/PKCS5Padding";
	private static final int KEY_SIZE = 32; // 256 bit key
	private static final int IV_SIZE = 16; // 128 bit IV

	@Value("${HARD_CODED_KEY}")
	private String HARD_CODED_KEY;

	@Value("${HARD_CODED_IV}")
	private String HARD_CODED_IV;

	private SecretKeySpec keySpec;
	private IvParameterSpec ivParamSpec;

	@PostConstruct
	public void init() {
		this.keySpec = new SecretKeySpec(HARD_CODED_KEY.getBytes(), ALGORITHM);
		this.ivParamSpec = new IvParameterSpec(HARD_CODED_IV.getBytes());
	}

	private SecretKeySpec generateKey() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[KEY_SIZE];
		secureRandom.nextBytes(key);
		return new SecretKeySpec(key, ALGORITHM);
	}

	private IvParameterSpec generateIV() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] iv = new byte[IV_SIZE];
		secureRandom.nextBytes(iv);
		return new IvParameterSpec(iv);
	}

	private Cipher getCipher(int encryptMode) throws Exception {
		Cipher cipher = Cipher.getInstance(MODE_PADDING);
		cipher.init(encryptMode, this.keySpec, this.ivParamSpec);
		return cipher;
	}

	public String encrypt(String text) throws Exception {
		Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
		byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(encrypted);
	}

	public String decrypt(String cipherText) throws Exception {
		Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
		byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
		byte[] decrypted = cipher.doFinal(decodedBytes);
		return new String(decrypted, "UTF-8");
	}

}

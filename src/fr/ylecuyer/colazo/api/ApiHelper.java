package fr.ylecuyer.colazo.api;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.ylecuyer.colazo.BuildConfig;
import android.util.Log;

public class ApiHelper {

	public static final String URL = "http://www.bicimapa.com/api";
	
	public static String getPass() {

		String pass = "bicimapaApp";

		Date now = new Date();		
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd", Locale.UK);
		
		pass += formater.format(now);

		if (BuildConfig.DEBUG) {
			Log.d("ApiHelper", "Generated pass: " + sha1(pass));
		}
		
		return sha1(pass);
	}



	private static String sha1(String pass) {
		MessageDigest md;
		try {
			
			md = MessageDigest.getInstance("SHA-1");

			md.update(pass.getBytes()); 
			byte[] output = md.digest();
			
			return bytesToHex(output);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}
	
	

	private static String bytesToHex(byte[] b) {
		char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		StringBuffer buf = new StringBuffer();
		for (int j=0; j<b.length; j++) {
			buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
			buf.append(hexDigit[b[j] & 0x0f]);
		}
		return buf.toString();
	}

}

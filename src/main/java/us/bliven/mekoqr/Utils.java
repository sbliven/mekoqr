/*
 *                    BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 * Created on Nov 4, 2016
 * Author: blivens 
 *
 */
 
package us.bliven.mekoqr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
 
public final class Utils {
	private Utils() {}
	

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
		return bytesToHex(bytes,bytes.length);
	}
	public static String bytesToHex(byte[] bytes,int len) {
	    char[] hexChars = new char[len * 3];
	    for ( int j = 0; j < len; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 3] = hexArray[v >>> 4];
	        hexChars[j * 3 + 1] = hexArray[v & 0x0F];
	        hexChars[j * 3 + 2] = ' ';
	    }
	    return new String(hexChars);
	}
	
	/**
	 * Convert a string with [0-9A-F] characters into a byte array.
	 * Whitespace is ignored. Must contain an even number of characters.
	 * @param hex hex string, e.g. "4A0F 0007"
	 * @return byte array, e.g. [0x4A, 0xFF, 0x00, 0x07]
	 * @throws NumberFormatException
	 */
	public static byte[] hexToBytes(String hex) throws NumberFormatException {
		// remove spaces
		hex = hex.replaceAll("\\s", "");
		if( hex.length() %2 != 0) {
			throw new IllegalArgumentException("Not an even number of nibbles");
		}
		
		byte[] bytes = new byte[hex.length()/2];
		for(int i=0; i< hex.length(); i+=2) {
			bytes[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2),16);
		}
		return bytes;
	}
	
	public static String expandUserHome(String file) {
		if (file.startsWith("~" + File.separator)) {
			file = System.getProperty("user.home") + file.substring(1);
		}
		return file;
	}
	public static void writeFile(String filename, String data) throws IOException {
		if( filename == null || filename.isEmpty() || filename.equals("-")) {
			System.out.println(data);
		} else {
			filename = expandUserHome(filename);
			File file = new File(filename);
			try( PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file))) ) {
				writer.println(data);
			}
		}
	}
	public static void writeBytes(String filename, byte[] data) throws FileNotFoundException, IOException {
		writeBytes(filename, data,data.length);
	}
	public static void writeBytes(String filename, byte[] data, int len) throws FileNotFoundException, IOException {
		if( filename == null || filename.isEmpty() ) {
			return;
		}
		try( FileOutputStream out = new FileOutputStream(filename) ) {
			out.write(data, 0, len);
		}
	}
}

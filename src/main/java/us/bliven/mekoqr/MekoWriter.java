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
 * Created on Oct 24, 2016
 * Author: blivens 
 *
 */
 
package us.bliven.mekoqr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * Code for writing Mekorama level QR codes
 * @author blivens
 *
 */
public class MekoWriter {
	private QRCodeWriter writer;
	public MekoWriter() {
		writer = new QRCodeWriter();
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
	
	/**
	 * Write the data bytes to a QR code
	 * @param outfile Output file
	 * @param data bytes to write
	 * @throws WriterException for errors generating the code
	 * @throws IOException for errors writing the file
	 */
	public void write(File outfile, byte[] data) throws WriterException, IOException {
		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		
		// ISO-8859-1 is defined for all bytes, unlike UTF-8 or ASCII, so it won't mangle the data
		String contents = new String(data, Charset.forName("ISO-8859-1"));
		
		int width = 600;
		int height = 600;
		
		// create QR code
		BitMatrix matrix = writer.encode(contents, BarcodeFormat.QR_CODE, width,height, hints);
		
		// create image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, width,height);
		graphics.setColor(Color.BLACK);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (matrix.get(x, y)) {
					graphics.fillRect(x, y, 1, 1);
				}
			}
		}
		// output as PNG
		ImageIO.write(image, "png", outfile);

	}
	
	public static void main(String[] args) throws WriterException, IOException {
		MekoWriter writer = new MekoWriter();
		String dataHex;
		File outfile;
//		dataHex = "01 13 0D FC 78 01 ED C0 C1 09 00 10 00 00 40 1F F6 32 0A 2F 45 94 D8 DF 1E BA 8B 75 94 D9 53 BE A7 AD 1D 00 00 00 80 EF 3D 91 13 04 87 ";
//		outfile = new File("/Users/blivens/dev/mekorama/levels/gen/00_blank_regen.png"); 
		dataHex = "01 13 0D FC 78 01 ED CA BB 09 00 21 10 40 C1 BB F0 92 AB C1 56 CC C5 CC 12 16 04 65 05 F1 D3 BE 35 18 8A 6F E2 F9 BC 4C E3 64 48 FE 83 26 2D 53 8D ED 2D 96 FA 00 00 80 7B BD 5B 1B 00 00 9C 6C 01 4B 79 08 DE";
		outfile = new File("/Users/blivens/dev/mekorama/levels/gen/stone_010_incremented3.png"); 

		byte[] data = hexToBytes(dataHex);
		// Create QR code
		writer.write(outfile,data);
	}
}

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

import static us.bliven.mekoqr.MekoLevel.SIZE;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;

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
	private int pngSize = 600;
	public MekoWriter() {
		writer = new QRCodeWriter();
	}

	
	/**
	 * Write the data bytes to a QR code
	 * @param outfile Output file
	 * @param data bytes to write
	 * @throws WriterException for errors generating the code
	 * @throws IOException for errors writing the file
	 */
	public void write(File outfile, byte[] data, int len) throws WriterException, IOException {
		BufferedImage image = generateQR(data, len);
		// output as PNG
		ImageIO.write(image, "png", outfile);
	}
	public void write(OutputStream out, byte[] data, int len) throws WriterException, IOException {
		BufferedImage image = generateQR(data, len);
		// output as PNG
		ImageIO.write(image, "png", out);
	}

	private BufferedImage generateQR(byte[] data, int len)
			throws WriterException {
		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		
		// ISO-8859-1 is defined for all bytes, unlike UTF-8 or ASCII, so it won't mangle the data
		String contents = new String(data, 0, len, Charset.forName("ISO-8859-1"));
		
		int width = pngSize;
		int height = pngSize;
		
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
		return image;
	}
	
	public static int encodeLevel(MekoLevel level, byte[] uncompressed) {
		
		int len = 0;
		len = copyString(level.getTitle(), uncompressed, len);
		len = copyString(level.getAuthor(), uncompressed, len);
		for(int z = 0; z< SIZE; z++) {
			for(int y = 0; y< SIZE; y++) {
				for(int x = 0; x< SIZE; x++) {
					BlockType blk = level.getBlock(x, y, z);
					byte[] vals = blk.getValues();
					for(int i=0;i<vals.length;i++) {
						uncompressed[len] = vals[i];
						len++;
					}
				}
			}
		}

		return len;
	}
	private static int copyString(String str, byte[] bytes, int pos) {
		assert str.length() < 256;

		bytes[pos] = (byte) str.length();
		pos++;
		byte[] titleChars = str.getBytes(StandardCharsets.US_ASCII);
		for(int i=0;i<titleChars.length;i++) {
			bytes[pos] = titleChars[i];
			pos++;
		}
		return pos;
	}
	
	public void write(File file, MekoLevel level ) throws WriterException, IOException {
		byte[] encoded = new byte[17*2+SIZE*SIZE*SIZE*2];
		int len = encodeLevel(level, encoded);
		
		byte[] compressed = new byte[len+4];
		len = deflateWrapped(encoded, compressed);
		for(int i=len-1;i>=0;i--) {
			compressed[i+4] = compressed[i];
		}
		compressed[0] = 0x01;
		compressed[1] = 0x13;
		compressed[2] = 0x0D;
		compressed[3] = (byte)0xFC;
		
		write(file,compressed,len+4);
	}
	public void write(OutputStream out, MekoLevel level ) throws WriterException, IOException {
		byte[] encoded = new byte[17*2+SIZE*SIZE*SIZE*2];
		int len = encodeLevel(level, encoded);
		
		byte[] compressed = new byte[len+4];
		len = deflateWrapped(encoded, compressed);
		for(int i=len-1;i>=0;i--) {
			compressed[i+4] = compressed[i];
		}
		compressed[0] = 0x01;
		compressed[1] = 0x13;
		compressed[2] = 0x0D;
		compressed[3] = (byte)0xFC;
		
		write(out,compressed,len+4);
	}
	
	/**
	 * Compress into a zlib-wrapped DEFLATE data stream
	 * @param compressed
	 * @param uncompressed
	 * @return
	 */
	static int deflateWrapped( byte[] uncompressed, byte[] compressed) {
		Deflater compressor = new Deflater(1, false);
		compressor.setInput(uncompressed);
		compressor.finish();
		int compressedlen = compressor.deflate(compressed);
		return compressedlen;
	}



}

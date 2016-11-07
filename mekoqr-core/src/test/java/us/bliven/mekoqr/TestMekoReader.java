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
 * Created on Oct 27, 2016
 * Author: blivens 
 *
 */

package us.bliven.mekoqr;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.ZipException;

import org.junit.Test;

public class TestMekoReader {

	@Test
	public void testPristineLevel() throws ZipException, IOException {
		String compressedHex = "78 01 ED C0 41 0A 00 10 10 40 51 5B 1B 67 70 15 "
				+ "7B D9 39 C2 94 A2 51 C2 5C DF 3D F4 9F 2F 62 31 CB 95 11 AA 76 9D A6 31 "
				+ "9D DD E6 72 00 00 00 00 00 E0 7B 0F 43 81 08 DD ";
		String correctTitle = "New Level";
		String correctAuthor = "Unknown Author";
		
		byte[] compressed = Utils.hexToBytes(compressedHex);
		
		//uncompress
		int correctLen = correctTitle.length()+correctAuthor.length()+2+MekoLevel.SIZE*MekoLevel.SIZE*MekoLevel.SIZE;
		byte[] uncompressed = new byte[correctLen];
		int len = MekoReader.inflate(compressed, compressed.length,uncompressed);
		assertEquals("Wrong uncompressed len",correctLen,len);
		
		String title = new String(uncompressed,1,correctTitle.length());
		assertEquals("Wrong title",correctTitle,title);
		
		// We use different compression settings, so this won't match compressed exactly
		byte[] recompressed = new byte[len];
		int recomplen = MekoWriter.deflateWrapped(uncompressed, recompressed);
		assertTrue(recomplen>2);
		assertTrue(recompressed[0] == 0x78);
		assertTrue(recompressed[1] == 0x01);
		
		// Decompress yet again
		byte[] reuncompressed = new byte[correctLen];
		int len2 = MekoReader.inflate(recompressed, recomplen, reuncompressed);
		assertEquals(len,len2);
		assertEquals(Utils.bytesToHex(uncompressed,len), Utils.bytesToHex(reuncompressed,len2));
		
		
	}

	@Test
	public void testZlib() throws DataFormatException, IOException {
		// generate plaintext
		byte[] plaintext = new byte[256];
		for( int i = 0;i<plaintext.length;i++) {
			plaintext[i] = (byte)i;
		}
		
		// compress
		byte[] compressed = new byte[300];
		int compressedlen = MekoWriter.deflateWrapped(plaintext, compressed);
		assertTrue(compressedlen>0);
		
//		System.out.format("Compressed: (%d bytes)%n",compressedlen);
//		System.out.println(MekoReader.bytesToHex(compressed,compressedlen));

		// decompress. Both methods should work on this data
		byte[] uncompressed = new byte[300];
		int uncompressedlen;
		uncompressedlen = MekoReader.inflateWrapped(compressed, compressedlen,
				uncompressed);
		assertEquals(plaintext.length, uncompressedlen);
		assertEquals(Utils.bytesToHex(plaintext),Utils.bytesToHex(uncompressed,uncompressedlen));
		
//		System.out.format("Decompressed: (%d bytes)%n",uncompressedlen);
//		System.out.println(MekoReader.bytesToHex(uncompressed,uncompressedlen));

		uncompressed = new byte[300];
		uncompressedlen = MekoReader.inflate(compressed,compressedlen,uncompressed);
		assertEquals(plaintext.length, uncompressedlen);
		assertEquals(Utils.bytesToHex(plaintext),Utils.bytesToHex(uncompressed,uncompressedlen));

//		System.out.format("Decompressed2: (%d bytes)%n",uncompressedlen);
//		System.out.println(MekoReader.bytesToHex(uncompressed,uncompressedlen));

	}

}

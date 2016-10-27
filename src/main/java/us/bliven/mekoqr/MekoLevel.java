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
 * Created on Oct 26, 2016
 * Author: blivens 
 *
 */
 
package us.bliven.mekoqr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * Represents the contents of a Mekorama level.
 * 
 * The level coordinate system is defined relative to the thumbnail orientation.
 * The horizontal plane is defined as (x,z), with the origin at the back center,
 * x increasing to the right, and z increasing to the left. The vertical coordinate
 * is y and increases upwards.
 * 
 * <pre>
 *      y
 *      ^
 *      |
 *      .
 *     / \
 *    /   \
 *   z     x
 * </pre>
 * 
 * @author blivens
 *
 */
public class MekoLevel {
	private static final Logger logger = LoggerFactory.getLogger(MekoLevel.class);
	
	private byte[] rawdata; // raw qr code data
	private boolean dirty; // has the level been modified since setting the rawdata?
	private String title;
	private String author;
	private BlockType[] data;
	
	/** Maximum level size */
	public static final int SIZE = 16;
	
	/**
	 * Create a level from raw, compressed data
	 * @param raw
	 * @throws DataFormatException
	 */
	public MekoLevel(byte[] raw) throws DataFormatException {
		this.rawdata = raw;
		this.dirty = false;

		byte[] b = Arrays.copyOfRange(raw, 4, raw.length);

		// Level consists of two strings (1 + 16 bytes) and the blocks, which can be 1 or 2 bytes
		byte[] uncompressed = new byte[17*2+SIZE*SIZE*SIZE*2];
		int len = inflate(b,uncompressed);
		
		if(logger.isInfoEnabled()) {
			String hex = MekoReader.bytesToHex(uncompressed);
			logger.info("Decompressed {} bytes starting with {}{}",len, hex.substring(0, Math.min(len, 30)),len>30?"...":"");
		}

		// Parse data
		int pos = 0;
		
		// Title
		int titleLen = uncompressed[pos];
		pos += 1;
		try {
			this.title = new String(uncompressed,pos,titleLen,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new DataFormatException(String.format("Malformed title (%d bytes)",titleLen));
		}
		pos += titleLen;
		
		// Author
		int authorLen = uncompressed[pos];
		pos += 1;
		try {
			this.author = new String(uncompressed,pos,authorLen,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new DataFormatException(String.format("Malformed author (%d bytes)",authorLen));
		}
		pos += authorLen;
		
		this.data = new BlockType[SIZE*SIZE*SIZE];
		pos += parseData(uncompressed, pos, len, this.data);
		
		if(pos != len) {
			logger.error("{} bytes were not parsed: {}",len-pos,Arrays.copyOfRange(uncompressed, pos, len));
		}
	}
	
	/**
	 * Parses the main data 
	 * @param level 
	 * @return Number of bytes parsed
	 */
	private static int parseData(byte[] data, int start, int end, BlockType[] level) {
		assert level.length == SIZE*SIZE*SIZE;
		int pos = 0;
		
		int i;
		for(i=start;i<end && pos < level.length;i++) {
			byte val = data[i];
			BlockType blk = BlockType.fromByte(val);
			if( blk.hasSubtypes() ) {
				i++;
				val = data[i];
				try {
					blk = blk.getSubtype(val);
				} catch(IllegalArgumentException e) {
					logger.error(e.getMessage());
				}
			}
			level[pos] = blk;
			pos++;
		}
		return i-start;
	}

	/**
	 * Gets the block at the specified position.
	 * @param x
	 * @param y
	 * @param z
	 */
	public BlockType getBlock(int x, int y, int z) {
		int index = indexForBlock(x, y, z);
		return data[index];
	}
	
	public void setBlock(int x, int y, int z,BlockType blk) {
		dirty = true;
		int index = indexForBlock(x, y, z);
		data[index] = blk;
	}
	
	/**
	 * Converts block coordinates into a position within data
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private int indexForBlock(int x, int y, int z) {
		return (z & 0xf << 8) | (y & 0xf << 4) | (x & 0xf);
	}
//	/**
//	 * Get the x position for a particular index within data
//	 * @param index
//	 * @return
//	 */
//	private int getX(int index) {
//		return index & 0xf;
//	}
//	private int getY(int index) {
//		return (index >> 4) & 0xf;
//	}
//	private int getZ(int index) {
//		return (index >> 8) & 0xf;
//	}

	public byte[] getRawData() {
		if(dirty)
			updateRaw();
		return rawdata;
	}

	private void updateRaw() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented");
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}
	
	/**
	 * Uncompress a DEFLATE data stream
	 * @param compressed input compressed data
	 * @param uncompressed output array. Must be large enough
	 * @return length of uncompressed used
	 */
	private static int inflate(byte[] compressed, byte[] uncompressed) {
		try( InflaterInputStream inStream = new InflaterInputStream(new ByteArrayInputStream( compressed ) ) ) {
			int len = 0;
		    int readByte;
		    while((readByte = inStream.read()) != -1) {
		    	uncompressed[len] = (byte)readByte;
		    	len++;
		    }
		    return len; 
		} catch(IOException e) {
			logger.error("Internal error while uncompressing data",e);
			return 0;
		}
	}

	/**
	 * Uncompress a zlib-wrapped DEFLATE data stream
	 * @param compressed
	 * @param uncompressed
	 * @return
	 */
	private static int inflateWrapped(byte[] compressed, byte[] uncompressed) {
		Deflater compressor = new Deflater(1, false);
		compressor.setInput(uncompressed);
		compressor.finish();
		int compressedlen = compressor.deflate(compressed);
		return compressedlen;
	}
	
	public static void main(String[] args) throws DataFormatException, IOException {
		// Some zlib tests
		byte[] bytes = new byte[256];
		for( int i = 0;i<bytes.length;i++) {
			bytes[i] = (byte)i;
		}
		byte[] compressed = new byte[300];
		int compressedlen = inflateWrapped(compressed, bytes);
		
		System.out.format("Compressed: (%d bytes)%n",compressedlen);
		System.out.println(MekoReader.bytesToHex(compressed,compressedlen));
		
		Inflater decompresser = new Inflater(false);
		decompresser.setInput(compressed, 0, compressedlen);
		byte[] uncompressed = new byte[300];
		int uncompressedlen = decompresser.inflate(uncompressed);
		System.out.format("Decompressed: (%d bytes)%n",uncompressedlen);
		System.out.println(MekoReader.bytesToHex(uncompressed,uncompressedlen));

		uncompressed = new byte[300];
		uncompressedlen = inflate(compressed,uncompressed);
	    System.out.format("Decompressed2: (%d bytes)%n",uncompressedlen);
	    System.out.println(MekoReader.bytesToHex(uncompressed,uncompressedlen));

	}
	
	
	/**
	 * Simple text-based summary of the level
	 * @return
	 */
	public String summarize() {
		final String nl = System.getProperty("line.separator");
		//bytesToHex(level.getRawData())
		return new StringBuffer()
			.append("Title: \"").append(this.title).append("\"").append(nl)
			.append("Author: \"").append(this.author).append("\"").append(nl)
			.append(getSparseData())
			.toString();
	}

	/**
	 * line-based list of all non-air blocks in the level
	 * 
	 * @return
	 */
	private String getSparseData() {
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<data.length;i++) {
			if(this.data[i] != BlockType.AIR) {
				BlockType blk = this.data[i];
				buf.append(String.format("%03x:%s%n",i/*getX(i),getY(i),getZ(i)*/,blk.getName()));
			}
		}
		return buf.toString();
	}

}

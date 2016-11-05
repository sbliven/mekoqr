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

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
 
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
	//private static final Logger logger = LoggerFactory.getLogger(MekoLevel.class);
	
	private String title;
	private String author;
	private BlockType[] data;
	
	private byte[] rawData;
	private byte[] serializedData;
	
	/** Maximum level size */
	public static final int SIZE = 16;
	
	/**
	 * Create a level from raw, compressed data
	 * @param raw
	 * @throws DataFormatException
	 */
	public MekoLevel(String title, String author, BlockType[] data) throws DataFormatException {
		if( data.length != SIZE*SIZE*SIZE) {
			throw new IllegalArgumentException("Wrong level size");
		}
		this.title = title;
		this.author = author;
		this.data = data;
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
	public List<Block> getBlocks() {
		List<Block> blocks = new ArrayList<>(SIZE*SIZE*SIZE);
		for(int y=0;y<SIZE;y++) {
			for(int z=0;z<SIZE;z++) {
				for(int x=0;x<SIZE;x++) {
					BlockType blk = getBlock(x, y, z);
					if(blk != BlockType.AIR) {
						blocks.add(new Block(blk,x,y,z));
					}
				}
			}
		}
		return blocks;
	}
	public void setBlock(int x, int y, int z,BlockType blk) {
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
		int index = (z & 0xf) << 8 | (y & 0xf) << 4 | (x & 0xf);
		return index;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}



	public void setAuthor(String author) {
		this.author = author;
	}



	public String getAuthor() {
		return author;
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

	public String ascii() {
		final String nl = System.getProperty("line.separator");
		StringBuffer str = new StringBuffer();
		
		// header
		for(int i=0;i<20;i++)
			str.append('-');
		str.append(String.format("%n| %16s |%n",getTitle()));
		str.append(String.format("| %16s |%n","  by"));
		str.append(String.format("| %16s |%n",getAuthor()));
		for(int i=0;i<20;i++)
			str.append('-');
		str.append(nl);
		
		int[] bounds = getBounds();
		int minX = bounds[0];
		int maxX = bounds[1];
		int minY = bounds[2];
		int maxY = bounds[3];
		int minZ = bounds[4];
		int maxZ = bounds[5];
		
		for(int y=maxY;y>=minY;y--) {
			//str.append("y=").append(y).append(nl);
			// horizontal slice
			for(int x = minX;x<=maxX+2;x++) {
				str.append("-");
			}
			str.append(nl);
			for(int z=minZ;z<=maxZ;z++) {
				str.append("|");
				for(int x = minX;x<=maxX;x++) {
					BlockType blk = getBlock(x, y, z);
					String name = blk.getShortName();
					assert name.length() == 1 : blk.getName();
					str.append(name);
				}
				str.append("|").append(nl);
			}
			for(int x = minX;x<=maxX+2;x++) {
				str.append("-");
			}
			str.append(nl);
		}
		
		return str.toString();
	}

	/**
	 * Get the bounds of solid blocks in the level
	 * @return a length-6 array with [minX,maxX, minY,maxY, minZ,maxZ]
	 */
	public int[] getBounds() {
		int minX = SIZE;
		int maxX = 0;
		int minY = SIZE;
		int maxY = 0;
		int minZ = SIZE;
		int maxZ = 0;
		for(int y=0;y<SIZE;y++) {
			for(int z=0;z<SIZE;z++) {
				for(int x=0;x<SIZE;x++) {
					BlockType blk = getBlock(x, y, z);
					if(blk != BlockType.AIR) {
						if(minX > x) minX = x;
						if(maxX < x) maxX = x;
						if(minY > y) minY = y;
						if(maxY < y) maxY = y;
						if(minZ > z) minZ = z;
						if(maxZ < z) maxZ = z;
					}
				}
			}
		}
		return new int[] {minX,maxX,minY,maxY,minZ,maxZ};
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



	public byte[] getRawData() {
		return rawData;
	}



	public void setRawData(byte[] rawData) {
		this.rawData = rawData;
	}



	public byte[] getSerializedData() {
		return serializedData;
	}



	public void setSerializedData(byte[] serializedData) {
		this.serializedData = serializedData;
	}




}

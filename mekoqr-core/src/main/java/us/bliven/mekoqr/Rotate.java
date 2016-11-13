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

import static us.bliven.mekoqr.BlockType.*;
import static us.bliven.mekoqr.MekoLevel.SIZE;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;

public class Rotate {
	private static final Logger logger = LoggerFactory.getLogger(Rotate.class);

	public static final Map<BlockType, Map<BlockType,BlockType>> rotatedBlocks = initRotatedBlocks();
	
	public static void main(String[] args) {
		if(args.length != 3) {
			System.err.println("usage: inputfile outputfile rotations");
			System.exit(2);
			return;
		}
		String inputFile = args[0];
		String outputFile = args[1];
		int rotations = Integer.parseInt(args[2]);
		
		// read level
		File in = new File(inputFile);
		MekoReader reader = new MekoReader();
		MekoLevel level;
		try {
			level = reader.readQR(in);
		} catch (NotFoundException | ChecksumException | FormatException
				| IOException | DataFormatException e) {
			System.err.println("Error reading file: "+e.getMessage());
			System.exit(1); return;
		}
		
		level.setAuthor("Rotated "+90*(rotations%4));
		
		rotate(level, rotations);
		
		MekoWriter writer = new MekoWriter();
		File out = new File(outputFile);
		try {
			writer.write(out, level);
		} catch (WriterException | IOException e) {
			System.err.println("Error writing rotated file: "+e.getMessage());
			System.exit(1); return;
		}
	}

	/**
	 * Rotate the data matrix clockwise
	 * 
	 * @param level Level (rotated in place)
	 * @param rotations number of rotations (clockwise when viewed from above)
	 */
	public static void rotate(MekoLevel level, int rotations) {
		// Swap (x,z) for (SIZE-z,x)
		for(int x=0;x<SIZE/2;x++) {
			for(int z=0;z<SIZE/2;z++) {
				for(int y=0;y<SIZE;y++) {
					BlockType pos1 = level.getBlock(x, y, z);
					BlockType pos2 = level.getBlock(SIZE-1-z,y,x);
					BlockType pos3 = level.getBlock(SIZE-1-x,y,SIZE-1-z);
					BlockType pos4 = level.getBlock(z,y,SIZE-1-x);

					for(int r=0;r<rotations%4;r++) {
						BlockType tmp = rotateBlock(pos4);
						pos4 = rotateBlock(pos3);
						pos3 = rotateBlock(pos2);
						pos2 = rotateBlock(pos1);
						pos1 = tmp;
					}

					level.setBlock(x, y, z, pos1);
					level.setBlock(SIZE-1-z,y,x, pos2);
					level.setBlock(SIZE-1-x,y,SIZE-1-z, pos3);
					level.setBlock(z,y,SIZE-1-x, pos4);
				}
			}
		}
	}

	private static Map<BlockType, Map<BlockType, BlockType>> initRotatedBlocks() {
		// Map parent -> Subtype -> rotated subtype
		Map<BlockType, Map<BlockType, BlockType>> parentMap = new HashMap<>();
		Map<BlockType, BlockType> subMap;
		BlockType parent;
		
		parent = STONE_STAIR;
		subMap = new HashMap<BlockType, BlockType>();
		subMap.put(parent.getSubtype( (byte) 0x00 ), parent.getSubtype( (byte) 0x03 ));
		subMap.put(parent.getSubtype( (byte) 0x03 ), parent.getSubtype( (byte) 0x02 ));
		subMap.put(parent.getSubtype( (byte) 0x02 ), parent.getSubtype( (byte) 0x01 ));
		subMap.put(parent.getSubtype( (byte) 0x01 ), parent.getSubtype( (byte) 0x00 ));
		subMap.put(parent.getSubtype( (byte) 0x04 ), parent.getSubtype( (byte) 0x07 ));
		subMap.put(parent.getSubtype( (byte) 0x07 ), parent.getSubtype( (byte) 0x06 ));
		subMap.put(parent.getSubtype( (byte) 0x06 ), parent.getSubtype( (byte) 0x05 ));
		subMap.put(parent.getSubtype( (byte) 0x05 ), parent.getSubtype( (byte) 0x04 ));
		subMap.put(parent.getSubtype( (byte) 0x10 ), parent.getSubtype( (byte) 0x13 ));
		subMap.put(parent.getSubtype( (byte) 0x13 ), parent.getSubtype( (byte) 0x12 ));
		subMap.put(parent.getSubtype( (byte) 0x12 ), parent.getSubtype( (byte) 0x11 ));
		subMap.put(parent.getSubtype( (byte) 0x11 ), parent.getSubtype( (byte) 0x10 ));
		parentMap.put(parent, subMap);

		parent = STONE_WEDGE;
		subMap = new HashMap<BlockType, BlockType>();
		subMap.put(parent.getSubtype( (byte) 0x00 ), parent.getSubtype( (byte) 0x03 ));
		subMap.put(parent.getSubtype( (byte) 0x03 ), parent.getSubtype( (byte) 0x02 ));
		subMap.put(parent.getSubtype( (byte) 0x02 ), parent.getSubtype( (byte) 0x01 ));
		subMap.put(parent.getSubtype( (byte) 0x01 ), parent.getSubtype( (byte) 0x00 ));
		subMap.put(parent.getSubtype( (byte) 0x04 ), parent.getSubtype( (byte) 0x07 ));
		subMap.put(parent.getSubtype( (byte) 0x07 ), parent.getSubtype( (byte) 0x06 ));
		subMap.put(parent.getSubtype( (byte) 0x06 ), parent.getSubtype( (byte) 0x05 ));
		subMap.put(parent.getSubtype( (byte) 0x05 ), parent.getSubtype( (byte) 0x04 ));
		subMap.put(parent.getSubtype( (byte) 0x10 ), parent.getSubtype( (byte) 0x13 ));
		subMap.put(parent.getSubtype( (byte) 0x13 ), parent.getSubtype( (byte) 0x12 ));
		subMap.put(parent.getSubtype( (byte) 0x12 ), parent.getSubtype( (byte) 0x11 ));
		subMap.put(parent.getSubtype( (byte) 0x11 ), parent.getSubtype( (byte) 0x10 ));
		parentMap.put(parent, subMap);

		parent = SLIDER;
		subMap = new HashMap<BlockType, BlockType>();
		subMap.put(parent.getSubtype( (byte) 0x00 ), parent.getSubtype( (byte) 0x03 ));
		subMap.put(parent.getSubtype( (byte) 0x03 ), parent.getSubtype( (byte) 0x00 ));
		subMap.put(parent.getSubtype( (byte) 0x14 ), parent.getSubtype( (byte) 0x14 ));
		parentMap.put(parent, subMap);

		parent = STONE_PILLAR;
		subMap = new HashMap<BlockType, BlockType>();
		subMap.put(parent.getSubtype( (byte) 0x00 ), parent.getSubtype( (byte) 0x00 ));
		subMap.put(parent.getSubtype( (byte) 0x0C ), parent.getSubtype( (byte) 0x14 ));
		subMap.put(parent.getSubtype( (byte) 0x14 ), parent.getSubtype( (byte) 0x0C ));
		parentMap.put(parent, subMap);

		parent = RAIL;
		subMap = new HashMap<BlockType, BlockType>();
		subMap.put(parent.getSubtype( (byte) 0x00 ), parent.getSubtype( (byte) 0x03 ));
		subMap.put(parent.getSubtype( (byte) 0x03 ), parent.getSubtype( (byte) 0x00 ));
		subMap.put(parent.getSubtype( (byte) 0x14 ), parent.getSubtype( (byte) 0x14 ));
		parentMap.put(parent, subMap);

		parent = FENCE;
		subMap = new HashMap<BlockType, BlockType>();
		subMap.put(parent.getSubtype( (byte) 0x00 ), parent.getSubtype( (byte) 0x03 ));
		subMap.put(parent.getSubtype( (byte) 0x03 ), parent.getSubtype( (byte) 0x00 ));
		subMap.put(parent.getSubtype( (byte) 0x04 ), parent.getSubtype( (byte) 0x07 ));
		subMap.put(parent.getSubtype( (byte) 0x07 ), parent.getSubtype( (byte) 0x04 ));
		subMap.put(parent.getSubtype( (byte) 0x10 ), parent.getSubtype( (byte) 0x13 ));
		subMap.put(parent.getSubtype( (byte) 0x13 ), parent.getSubtype( (byte) 0x10 ));
		parentMap.put(parent, subMap);

		parent = CURVED_RAIL;
		subMap = new HashMap<BlockType, BlockType>();
		subMap.put(parent.getSubtype( (byte) 0x00 ), parent.getSubtype( (byte) 0x03 ));
		subMap.put(parent.getSubtype( (byte) 0x03 ), parent.getSubtype( (byte) 0x02 ));
		subMap.put(parent.getSubtype( (byte) 0x02 ), parent.getSubtype( (byte) 0x01 ));
		subMap.put(parent.getSubtype( (byte) 0x01 ), parent.getSubtype( (byte) 0x00 ));
		subMap.put(parent.getSubtype( (byte) 0x04 ), parent.getSubtype( (byte) 0x07 ));
		subMap.put(parent.getSubtype( (byte) 0x07 ), parent.getSubtype( (byte) 0x06 ));
		subMap.put(parent.getSubtype( (byte) 0x06 ), parent.getSubtype( (byte) 0x05 ));
		subMap.put(parent.getSubtype( (byte) 0x05 ), parent.getSubtype( (byte) 0x04 ));
		subMap.put(parent.getSubtype( (byte) 0x10 ), parent.getSubtype( (byte) 0x13 ));
		subMap.put(parent.getSubtype( (byte) 0x13 ), parent.getSubtype( (byte) 0x12 ));
		subMap.put(parent.getSubtype( (byte) 0x12 ), parent.getSubtype( (byte) 0x11 ));
		subMap.put(parent.getSubtype( (byte) 0x11 ), parent.getSubtype( (byte) 0x10 ));
		parentMap.put(parent, subMap);

		parent = R_BOT;
		subMap = new HashMap<BlockType, BlockType>();
		subMap.put(parent.getSubtype( (byte) 0x00 ), parent.getSubtype( (byte) 0x03 ));
		subMap.put(parent.getSubtype( (byte) 0x03 ), parent.getSubtype( (byte) 0x02 ));
		subMap.put(parent.getSubtype( (byte) 0x02 ), parent.getSubtype( (byte) 0x01 ));
		subMap.put(parent.getSubtype( (byte) 0x01 ), parent.getSubtype( (byte) 0x00 ));
		parentMap.put(parent, subMap);

		parent = B_BOT;
		subMap = new HashMap<BlockType, BlockType>();
		subMap.put(parent.getSubtype( (byte) 0x00 ), parent.getSubtype( (byte) 0x03 ));
		subMap.put(parent.getSubtype( (byte) 0x03 ), parent.getSubtype( (byte) 0x02 ));
		subMap.put(parent.getSubtype( (byte) 0x02 ), parent.getSubtype( (byte) 0x01 ));
		subMap.put(parent.getSubtype( (byte) 0x01 ), parent.getSubtype( (byte) 0x00 ));
		parentMap.put(parent, subMap);

		parent = MOTOR;
		subMap = new HashMap<BlockType, BlockType>();
		subMap.put(parent.getSubtype( (byte) 0x00 ), parent.getSubtype( (byte) 0x03 ));
		subMap.put(parent.getSubtype( (byte) 0x03 ), parent.getSubtype( (byte) 0x02 ));
		subMap.put(parent.getSubtype( (byte) 0x02 ), parent.getSubtype( (byte) 0x01 ));
		subMap.put(parent.getSubtype( (byte) 0x01 ), parent.getSubtype( (byte) 0x00 ));
		subMap.put(parent.getSubtype( (byte) 0x04 ), parent.getSubtype( (byte) 0x04 ));
		subMap.put(parent.getSubtype( (byte) 0x0C ), parent.getSubtype( (byte) 0x0C ));
		parentMap.put(parent, subMap);

		return parentMap;
	}

	private static BlockType rotateBlock(BlockType blk) {
		BlockType parent = blk.getParent();
		// Top-level block; no variants
		if(parent == null) {
			return blk;
		}
		// Check whether map has variants
		if( !rotatedBlocks.containsKey(parent) ) {
			// No variants mapped
			return blk;
		}
		
		Map<BlockType, BlockType> variants = rotatedBlocks.get(parent);
		if(!variants.containsKey(blk)) {
			logger.error("Unknown subtype {} of {}",blk.getName(),parent.getName());
			return blk;
		}
		return variants.get(blk);
	}
}

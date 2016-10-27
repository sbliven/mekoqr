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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
 
/**
 * Represents a type of block, e.g. stone, pillar, etc.
 * 
 * Blocks with several orientations are represented by a subtype.
 * 
 * BlockType is immutable.
 * @author blivens
 *
 */
public class BlockType {
	public static final BlockType AIR = new BlockType(0,"Air"," ");
	public static final BlockType STONE = new BlockType(1,"Stone","S");
	public static final BlockType BRICK = new BlockType(2,"Brick","K");
	public static final BlockType WATER = new BlockType(11,"Water","~");
	public static final BlockType GRASS = new BlockType(12,"Grass","G");
	public static final BlockType DESERT = new BlockType(18,"Desert","g");
	public static final BlockType METAL = new BlockType(25,"Metal","M");
	public static final BlockType PILLAR = new BlockType(35,"Pillar","P", new BlockType[] {
			new BlockType(20,"Pillar(X)",">"),
			new BlockType(0,"Pillar(Y)","^"),
			new BlockType(12,"Pillar(Z)","<"),
	});

	private static final Map<Byte,BlockType> knownTypes = new HashMap<>();
	static {
		Arrays.asList(
				AIR,
				STONE,
				BRICK,
				WATER,
				GRASS,
				DESERT,
				METAL,
				PILLAR
			).stream()
			.forEach((blk) -> knownTypes.put(blk.getValue(),blk) );
	}
	
	private final byte value;
	private final String name;
	private final String shortName;//1 char name
	private final BlockType[] subtypes;
	
	private BlockType(int value, String name, String shortName) {
		this((byte)value,name,shortName);
	}
	private BlockType(int value, String name, String shortName,BlockType[] subtypes) {
		this((byte)value,name,shortName,subtypes);
	}
	private BlockType(byte value, String name, String shortName) {
		this(value,name,shortName,new BlockType[0]);
	}
	private BlockType(byte value, String name, String shortName,BlockType[] subtypes) {
		this.value = value;
		this.name = name;
		if(shortName.length() > 1) {
			throw new IllegalArgumentException("Short name too long");
		}
		this.shortName = shortName;
		this.subtypes = subtypes;
	}

	public static BlockType fromByte(byte b) {
		if(knownTypes.containsKey(b)) {
			return knownTypes.get(b);
		} else {
			BlockType t = new BlockType(b, String.format("Blk%d",b),"?");
			knownTypes.put(b,t);
			return t;
		}
	}
	public byte getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
	public String getShortName() {
		return shortName;
	}
	public boolean hasSubtypes() {
		return subtypes != null && subtypes.length > 0;
	}
	public BlockType[] getSubtypes() {
		return subtypes;
	}
	public BlockType getSubtype(byte val) {
		if( hasSubtypes() ) {
			for(BlockType sub : subtypes) {
				if(sub.getValue() == val) {
					return sub;
				}
			}
		}
		throw new IllegalArgumentException(String.format("Unrecognized subtype %d of %s",val, getName()));
	}
}

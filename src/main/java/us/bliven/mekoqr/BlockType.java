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
	public static final BlockType WIN = new BlockType(4,"Win","!");
	public static final BlockType STAIR = new BlockType(5,"Stair","/", new BlockType[] {
			new BlockType( 0x00,"Stair(00)","/"),
			new BlockType( 0x01,"Stair(01)","/"),
			new BlockType( 0x02,"Stair(02)","/"),
			new BlockType( 0x03,"Stair(03)","/"),
			new BlockType( 0x04,"Stair(04)","/"),
			new BlockType( 0x05,"Stair(05)","/"),
			new BlockType( 0x06,"Stair(06)","/"),
			new BlockType( 0x07,"Stair(07)","/"),
			new BlockType( 0x10,"Stair(10)","/"),
			new BlockType( 0x11,"Stair(11)","/"),
			new BlockType( 0x12,"Stair(12)","/"),
			new BlockType( 0x13,"Stair(13)","/"),
	});
	public static final BlockType WEDGE = new BlockType(7,"Wedge","W", new BlockType[] {
			new BlockType( 0x00,"Wedge(00)","W"),
			new BlockType( 0x01,"Wedge(01)","W"),
			new BlockType( 0x02,"Wedge(02)","W"),
			new BlockType( 0x03,"Wedge(03)","W"),
			new BlockType( 0x04,"Wedge(04)","W"),
			new BlockType( 0x05,"Wedge(05)","W"),
			new BlockType( 0x06,"Wedge(06)","W"),
			new BlockType( 0x07,"Wedge(07)","W"),
			new BlockType( 0x10,"Wedge(10)","W"),
			new BlockType( 0x11,"Wedge(11)","W"),
			new BlockType( 0x12,"Wedge(12)","W"),
			new BlockType( 0x13,"Wedge(13)","W"),
	});
	public static final BlockType WATER = new BlockType(11,"Water","~");
	public static final BlockType GRASS = new BlockType(12,"Grass","G");
	public static final BlockType B_BOT = new BlockType(15,"B-bot","B", new BlockType[] {
			new BlockType( 0x00,"B-bot(S)","B"),
			new BlockType( 0x01,"B-bot(E)","B"),
			new BlockType( 0x02,"B-bot(N)","B"),
			new BlockType( 0x03,"B-bot(W)","B"),
	});
	public static final BlockType ZAPPER = new BlockType(16,"Zapper","Z");
	public static final BlockType DRAGGABLE = new BlockType(17,"Draggable","D");
	public static final BlockType DESERT = new BlockType(18,"Desert","g");
	public static final BlockType MOTOR = new BlockType(22,"Motor","@", new BlockType[] {
			new BlockType( 0x00,"Motor(S)","@"),
			new BlockType( 0x01,"Motor(E)","@"),
			new BlockType( 0x02,"Motor(N)","@"),
			new BlockType( 0x03,"Motor(W)","@"),
			new BlockType( 0x04,"Motor(D)","@"),
			new BlockType( 0x0c,"Motor(U)","@"),
	});
	public static final BlockType METAL = new BlockType(25,"Metal","M");
	public static final BlockType R_BOT = new BlockType(26,"R-bot","R", new BlockType[] {
			new BlockType( 0x00,"R-bot(S)","R"),
			new BlockType( 0x01,"R-bot(E)","R"),
			new BlockType( 0x02,"R-bot(N)","R"),
			new BlockType( 0x03,"R-bot(W)","R"),
	});
	public static final BlockType EYE = new BlockType(27,"Eye",":");
	public static final BlockType CURVED_RAIL = new BlockType(30,"CurvedRail","-", new BlockType[] {
			new BlockType( 0x00,"CurvedRail(00)","-"),
			new BlockType( 0x01,"CurvedRail(01)","-"),
			new BlockType( 0x02,"CurvedRail(02)","-"),
			new BlockType( 0x03,"CurvedRail(03)","-"),
			new BlockType( 0x04,"CurvedRail(04)","-"),
			new BlockType( 0x05,"CurvedRail(05)","-"),
			new BlockType( 0x06,"CurvedRail(06)","-"),
			new BlockType( 0x07,"CurvedRail(07)","-"),
			new BlockType( 0x10,"CurvedRail(10)","-"),
			new BlockType( 0x11,"CurvedRail(11)","-"),
			new BlockType( 0x12,"CurvedRail(12)","-"),
			new BlockType( 0x13,"CurvedRail(13)","-"),
	});
	public static final BlockType HALF = new BlockType(32,"HalfPillar","H", new BlockType[] {
			new BlockType( 0x00,"HalfPillar(00)","H"),
			new BlockType( 0x01,"HalfPillar(01)","H"),
			new BlockType( 0x02,"HalfPillar(02)","H"),
			new BlockType( 0x03,"HalfPillar(03)","H"),
			new BlockType( 0x04,"HalfPillar(04)","H"),
			new BlockType( 0x14,"HalfPillar(14)","H"),
			new BlockType( 0x15,"HalfPillar(15)","H"),
			new BlockType( 0x16,"HalfPillar(16)","H"),
			new BlockType( 0x17,"HalfPillar(17)","H"),
			new BlockType( 0x0c,"HalfPillar(0c)","H"),
			new BlockType( 0x05,"HalfPillar(05)","H"),
			new BlockType( 0x06,"HalfPillar(06)","H"),
			new BlockType( 0x0f,"HalfPillar(0f)","H"),
	});
	public static final BlockType RAIL = new BlockType(33,"Rail","-", new BlockType[] {
			new BlockType( 0x00,"Rail(X)","-"),
			new BlockType( 0x03,"Rail(Z)","-"),
			new BlockType( 0x14,"Rail(Y)","-"),
	});

	public static final BlockType PILLAR = new BlockType(35,"Pillar","P", new BlockType[] {
			new BlockType(0x14,"Pillar(X)","P"),
			new BlockType(0x00,"Pillar(Y)","P"),
			new BlockType(0x0c,"Pillar(Z)","P"),
	});
	public static final BlockType BALL = new BlockType(37,"Ball","O");
	public static final BlockType SLIDER = new BlockType(41,"Slider","L", new BlockType[] {
			new BlockType(0x00,"Slider(X)","L"),
			new BlockType(0x03,"Slider(Z)","L"),
			new BlockType(0x14,"Slider(Y)","L"),
	});
	public static final BlockType FENCE = new BlockType(43,"Fence","F", new BlockType[] {
			new BlockType( 0x00,"Fence(00)","F"),
			new BlockType( 0x03,"Fence(03)","F"),
			new BlockType( 0x04,"Fence(04)","F"),
			new BlockType( 0x07,"Fence(07)","F"),
			new BlockType( 0x10,"Fence(10)","F"),
			new BlockType( 0x13,"Fence(13)","F"),
	});


	private static final Map<Byte,BlockType> knownTypes = new HashMap<>();
	static {
		Arrays.asList(
				AIR,
				STONE,
				BRICK,
				WIN,
				STAIR,
				WEDGE,
				WATER,
				GRASS,
				B_BOT,
				ZAPPER,
				DRAGGABLE,
				DESERT,
				MOTOR,
				METAL,
				R_BOT,
				EYE,
				CURVED_RAIL,
				HALF,
				RAIL,
				PILLAR,
				SLIDER,
				BALL,
				FENCE
			).stream()
			.forEach((blk) -> knownTypes.put(blk.getValue(),blk) );
	}
	
	private BlockType parent;
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
		this.parent = null;
		if(subtypes != null) {
			for(BlockType sub : subtypes) {
				sub.parent = this;
			}
		}
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
	public byte[] getValues() {
		if(parent == null) {
			return new byte[] {value};
		} else {
			byte[] par = parent.getValues();
			byte[] vals = Arrays.copyOf(par, par.length+1);
			vals[par.length] = value;
			return vals;
		}
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
		throw new IllegalArgumentException(String.format("Unrecognized subtype 0x%x of %s",val, getName()));
	}
	
	public BlockType getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		return name;
	}
}

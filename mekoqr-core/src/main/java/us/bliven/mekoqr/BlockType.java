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
	public static final BlockType AIR = new BlockType(0x00,"Air"," ");
	public static final BlockType STONE = new BlockType(0x01,"Stone","#");
	public static final BlockType BRICK = new BlockType(0x02,"BrickRed","X");
	public static final BlockType STONE_03 = new BlockType(0x03,"Stone03","#",new BlockType[0],false);
	public static final BlockType WIN = new BlockType(0x04,"Win","W");
	public static final BlockType STONE_STAIR = new BlockType(0x05,"StoneStair","s", new BlockType[] {
			new BlockType( 0x00,"StoneStair(00)","s"),
			new BlockType( 0x01,"StoneStair(01)","s"),
			new BlockType( 0x02,"StoneStair(02)","s"),
			new BlockType( 0x03,"StoneStair(03)","s"),
			new BlockType( 0x04,"StoneStair(04)","s"),
			new BlockType( 0x05,"StoneStair(05)","s"),
			new BlockType( 0x06,"StoneStair(06)","s"),
			new BlockType( 0x07,"StoneStair(07)","s"),
			new BlockType( 0x10,"StoneStair(10)","s"),
			new BlockType( 0x11,"StoneStair(11)","s"),
			new BlockType( 0x12,"StoneStair(12)","s"),
			new BlockType( 0x13,"StoneStair(13)","s"),
	});
	public static final BlockType TRASH = new BlockType(0x06,"Trash","t",new BlockType[0],false);
	public static final BlockType STONE_WEDGE = new BlockType(0x07,"StoneWedge","/", new BlockType[] {
			new BlockType( 0x00,"StoneWedge(00)","/"),
			new BlockType( 0x01,"StoneWedge(01)","/"),
			new BlockType( 0x02,"StoneWedge(02)","/"),
			new BlockType( 0x03,"StoneWedge(03)","/"),
			new BlockType( 0x04,"StoneWedge(04)","/"),
			new BlockType( 0x05,"StoneWedge(05)","/"),
			new BlockType( 0x06,"StoneWedge(06)","/"),
			new BlockType( 0x07,"StoneWedge(07)","/"),
			new BlockType( 0x10,"StoneWedge(10)","/"),
			new BlockType( 0x11,"StoneWedge(11)","/"),
			new BlockType( 0x12,"StoneWedge(12)","/"),
			new BlockType( 0x13,"StoneWedge(13)","/"),
	});
	// Actually more like desert?
	public static final BlockType GRASS_WEDGE = new BlockType(0x08,"GrassWedge","/", new BlockType[] {
			new BlockType( 0x00,"GrassWedge(00)","/"),
			new BlockType( 0x01,"GrassWedge(01)","/"),
			new BlockType( 0x02,"GrassWedge(02)","/"),
			new BlockType( 0x03,"GrassWedge(03)","/"),
			new BlockType( 0x04,"GrassWedge(04)","/"),
			new BlockType( 0x05,"GrassWedge(05)","/"),
			new BlockType( 0x06,"GrassWedge(06)","/"),
			new BlockType( 0x07,"GrassWedge(07)","/"),
			new BlockType( 0x10,"GrassWedge(10)","/"),
			new BlockType( 0x11,"GrassWedge(11)","/"),
			new BlockType( 0x12,"GrassWedge(12)","/"),
			new BlockType( 0x13,"GrassWedge(13)","/"),
	},false);
	public static final BlockType GOLDEN_BALL = new BlockType(0x09,"GoldenBall","*",new BlockType[0],false);
	// Win without star. Checkpoint?
	public static final BlockType METAL_WIN = new BlockType(0x0a,"MetalWin","W", new BlockType[] {
			new BlockType( 0x00,"MetalWin(S)","W"),
			new BlockType( 0x01,"MetalWin(E)","W"),
			new BlockType( 0x02,"MetalWin(N)","W"),
			new BlockType( 0x03,"MetalWin(W)","W"),
			new BlockType( 0x04,"MetalWin(D)","W"),
			new BlockType( 0x0c,"MetalWin(U)","W"),
	},false);
	public static final BlockType WATER = new BlockType(0x0b,"Water","~");
	public static final BlockType GRASS = new BlockType(0x0c,"Grass","#");
	// Not a good name. A black, free-moving cylinder
	public static final BlockType BLACK_PILLAR = new BlockType(0x0d,"BlackPillar","X",new BlockType[0],false);
	public static final BlockType STONE_QUARTER = new BlockType(0x0e,"StoneQuarterPillar","#", new BlockType[] {
			new BlockType( 0x00,"StoneQuarterPillar(00)","#"),
			new BlockType( 0x01,"StoneQuarterPillar(01)","#"),
			new BlockType( 0x02,"StoneQuarterPillar(02)","#"),
			new BlockType( 0x03,"StoneQuarterPillar(03)","#"),
			new BlockType( 0x04,"StoneQuarterPillar(04)","#"),
			new BlockType( 0x05,"StoneQuarterPillar(05)","#"),
			new BlockType( 0x06,"StoneQuarterPillar(06)","#"),
			new BlockType( 0x07,"StoneQuarterPillar(07)","#"),
			new BlockType( 0x10,"StoneQuarterPillar(10)","#"),
			new BlockType( 0x11,"StoneQuarterPillar(11)","#"),
			new BlockType( 0x12,"StoneQuarterPillar(12)","#"),
			new BlockType( 0x13,"StoneQuarterPillar(13)","#"),
	},false);
	public static final BlockType B_BOT = new BlockType(0x0f,"B-bot","B", new BlockType[] {
			new BlockType( 0x00,"B-bot(S)","B"),
			new BlockType( 0x01,"B-bot(E)","B"),
			new BlockType( 0x02,"B-bot(N)","B"),
			new BlockType( 0x03,"B-bot(W)","B"),
	});
	public static final BlockType ZAPPER = new BlockType(0x10,"Zapper","Z");
	public static final BlockType DRAGGABLE = new BlockType(0x11,"Draggable","d");
	public static final BlockType DESERT = new BlockType(0x12,"Desert","#");
	public static final BlockType WHEEL = new BlockType(0x13,"Wheel","o", new BlockType[] {
			new BlockType( 0x00,"Wheel(S)","o"),
			new BlockType( 0x01,"Wheel(E)","o"),
			new BlockType( 0x02,"Wheel(N)","o"),
			new BlockType( 0x03,"Wheel(W)","o"),
			new BlockType( 0x04,"Wheel(D)","o"),
			new BlockType( 0x0c,"Wheel(U)","o"),
	}, false);
	public static final BlockType METAL_STAIR = new BlockType(0x14,"MetalStair","/", new BlockType[] {
			new BlockType( 0x00,"MetalStair(00)","/"),
			new BlockType( 0x01,"MetalStair(01)","/"),
			new BlockType( 0x02,"MetalStair(02)","/"),
			new BlockType( 0x03,"MetalStair(03)","/"),
			new BlockType( 0x04,"MetalStair(04)","/"),
			new BlockType( 0x05,"MetalStair(05)","/"),
			new BlockType( 0x06,"MetalStair(06)","/"),
			new BlockType( 0x07,"MetalStair(07)","/"),
			new BlockType( 0x10,"MetalStair(10)","/"),
			new BlockType( 0x11,"MetalStair(11)","/"),
			new BlockType( 0x12,"MetalStair(12)","/"),
			new BlockType( 0x13,"MetalStair(13)","/"),
	},false);
	public static final BlockType METAL_QUARTER = new BlockType(0x15,"MetalQuarterPillar","+", new BlockType[] {
			new BlockType( 0x00,"MetalQuarterPillar(00)","+"),
			new BlockType( 0x01,"MetalQuarterPillar(01)","+"),
			new BlockType( 0x02,"MetalQuarterPillar(02)","+"),
			new BlockType( 0x03,"MetalQuarterPillar(03)","+"),
			new BlockType( 0x04,"MetalQuarterPillar(04)","+"),
			new BlockType( 0x05,"MetalQuarterPillar(05)","+"),
			new BlockType( 0x06,"MetalQuarterPillar(06)","+"),
			new BlockType( 0x07,"MetalQuarterPillar(07)","+"),
			new BlockType( 0x10,"MetalQuarterPillar(10)","+"),
			new BlockType( 0x11,"MetalQuarterPillar(11)","+"),
			new BlockType( 0x12,"MetalQuarterPillar(12)","+"),
			new BlockType( 0x13,"MetalQuarterPillar(13)","+"),
	},false);
	public static final BlockType MOTOR = new BlockType(0x16,"Motor","m", new BlockType[] {
			new BlockType( 0x00,"Motor(S)","@"),
			new BlockType( 0x01,"Motor(E)","@"),
			new BlockType( 0x02,"Motor(N)","@"),
			new BlockType( 0x03,"Motor(W)","@"),
			new BlockType( 0x04,"Motor(D)","@"),
			new BlockType( 0x0c,"Motor(U)","@"),
	});
	// Slightly different metal texture
	public static final BlockType METAL_17 = new BlockType(0x17,"Metal17","+", new BlockType[0], false);
	public static final BlockType STONE_18 = new BlockType(0x18,"Stone18","#", new BlockType[0], false);
	public static final BlockType METAL = new BlockType(0x19,"Metal","+");
	public static final BlockType R_BOT = new BlockType(0x1a,"R-bot","R", new BlockType[] {
			new BlockType( 0x00,"R-bot(S)","R"),
			new BlockType( 0x01,"R-bot(E)","R"),
			new BlockType( 0x02,"R-bot(N)","R"),
			new BlockType( 0x03,"R-bot(W)","R"),
	});
	public static final BlockType EYE = new BlockType(0x1b,"Eye","0");
	// Function is not understood. Possibly buggy.
	public static final BlockType BUGGY_1C = new BlockType(0x1c,"Unknown1C","?", new BlockType[0], false);
	public static final BlockType STONE_1D = new BlockType(0x1d,"Stone1D","#", new BlockType[0], false);
	public static final BlockType CURVED_RAIL = new BlockType(0x1e,"CurvedRail","j", new BlockType[] {
			new BlockType( 0x00,"CurvedRail(00)","j"),
			new BlockType( 0x01,"CurvedRail(01)","j"),
			new BlockType( 0x02,"CurvedRail(02)","j"),
			new BlockType( 0x03,"CurvedRail(03)","j"),
			new BlockType( 0x04,"CurvedRail(04)","j"),
			new BlockType( 0x05,"CurvedRail(05)","j"),
			new BlockType( 0x06,"CurvedRail(06)","j"),
			new BlockType( 0x07,"CurvedRail(07)","j"),
			new BlockType( 0x10,"CurvedRail(10)","j"),
			new BlockType( 0x11,"CurvedRail(11)","j"),
			new BlockType( 0x12,"CurvedRail(12)","j"),
			new BlockType( 0x13,"CurvedRail(13)","j"),
	});
	public static final BlockType STONE_PILLAR_1F = new BlockType(0x1f,"StonePillar1F","#", new BlockType[] {
			new BlockType(0x14,"StonePillar1F(X)","#"),
			new BlockType(0x00,"StonePillar1F(Y)","#"),
			new BlockType(0x0c,"StonePillar1F(Z)","#"),
	},false);
	public static final BlockType METAL_HALF = new BlockType(0x20,"MetalHalfPillar","+", new BlockType[] {
			new BlockType( 0x00,"MetalHalfPillar(00)","+"),
			new BlockType( 0x01,"MetalHalfPillar(01)","+"),
			new BlockType( 0x02,"MetalHalfPillar(02)","+"),
			new BlockType( 0x03,"MetalHalfPillar(03)","+"),
			new BlockType( 0x04,"MetalHalfPillar(04)","+"),
			new BlockType( 0x14,"MetalHalfPillar(14)","+"),
			new BlockType( 0x15,"MetalHalfPillar(15)","+"),
			new BlockType( 0x16,"MetalHalfPillar(16)","+"),
			new BlockType( 0x17,"MetalHalfPillar(17)","+"),
			new BlockType( 0x0c,"MetalHalfPillar(0c)","+"),
			new BlockType( 0x05,"MetalHalfPillar(05)","+"),
			new BlockType( 0x06,"MetalHalfPillar(06)","+"),
			new BlockType( 0x0f,"MetalHalfPillar(0f)","+"),
	});
	public static final BlockType RAIL = new BlockType(0x21,"Rail","|", new BlockType[] {
			new BlockType( 0x00,"Rail(X)","|"),
			new BlockType( 0x03,"Rail(Z)","|"),
			new BlockType( 0x14,"Rail(Y)","|"),
	});
	public static final BlockType STONE_HALF = new BlockType(0x22,"StoneHalfPillar","#", new BlockType[] {
			new BlockType( 0x00,"StoneHalfPillar(00)","#"),
			new BlockType( 0x01,"StoneHalfPillar(01)","#"),
			new BlockType( 0x02,"StoneHalfPillar(02)","#"),
			new BlockType( 0x03,"StoneHalfPillar(03)","#"),
			new BlockType( 0x04,"StoneHalfPillar(04)","#"),
			new BlockType( 0x14,"StoneHalfPillar(14)","#"),
			new BlockType( 0x15,"StoneHalfPillar(15)","#"),
			new BlockType( 0x16,"StoneHalfPillar(16)","#"),
			new BlockType( 0x17,"StoneHalfPillar(17)","#"),
			new BlockType( 0x0c,"StoneHalfPillar(0c)","#"),
			new BlockType( 0x05,"StoneHalfPillar(05)","#"),
			new BlockType( 0x06,"StoneHalfPillar(06)","#"),
			new BlockType( 0x0f,"StoneHalfPillar(0f)","#"),
	},false);

	public static final BlockType STONE_PILLAR = new BlockType(0x23,"StonePillar","P", new BlockType[] {
			new BlockType(0x14,"StonePillar(X)","P"),
			new BlockType(0x00,"StonePillar(Y)","P"),
			new BlockType(0x0c,"StonePillar(Z)","P"),
	});
	public static final BlockType DRAGGABLE_PILLAR = new BlockType(0x24,"DraggablePillar","d", new BlockType[] {
			new BlockType(0x14,"DraggablePillar(X)","d"),
			new BlockType(0x00,"DraggablePillar(Y)","d"),
			new BlockType(0x0c,"DraggablePillar(Z)","d"),
	},false);
	public static final BlockType BALL = new BlockType(0x25,"Ball","O");
	public static final BlockType STONE_26 = new BlockType(0x26,"Stone26","#", new BlockType[0], false);
	public static final BlockType METAL_PILLAR = new BlockType(0x27,"MetalPillar","P", new BlockType[] {
			new BlockType(0x14,"MetalPillar(X)","P"),
			new BlockType(0x00,"MetalPillar(Y)","P"),
			new BlockType(0x0c,"MetalPillar(Z)","P"),
	});
	public static final BlockType UNKNOWN_28 = new BlockType(0x28,"Unknown28","?", new BlockType[0], false);
	public static final BlockType SLIDER = new BlockType(0x29,"Slider","L", new BlockType[] {
			new BlockType(0x00,"Slider(X)","L"),
			new BlockType(0x03,"Slider(Z)","L"),
			new BlockType(0x14,"Slider(Y)","L"),
	});
	public static final BlockType UNKNOWN_2A = new BlockType(0x2a,"Unknown2A","?", new BlockType[0], false);
	public static final BlockType FENCE = new BlockType(0x2b,"Fence","F", new BlockType[] {
			new BlockType( 0x00,"Fence(00)","F"),
			new BlockType( 0x03,"Fence(03)","F"),
			new BlockType( 0x04,"Fence(04)","F"),
			new BlockType( 0x07,"Fence(07)","F"),
			new BlockType( 0x10,"Fence(10)","F"),
			new BlockType( 0x13,"Fence(13)","F"),
	});
	public static final BlockType UNKNOWN_2C = new BlockType(0x2C,"Unknown2C","?", new BlockType[0], false);
	public static final BlockType UNKNOWN_2D = new BlockType(0x2D,"Unknown2D","?", new BlockType[0], false);
	public static final BlockType UNKNOWN_2E = new BlockType(0x2E,"Unknown2E","?", new BlockType[0], false);
	public static final BlockType UNKNOWN_2F = new BlockType(0x2F,"Unknown2F","?", new BlockType[0], false);
	// Might crash app?
	public static final BlockType UNKNOWN_30 = new BlockType(0x30,"Unknown30","?", new BlockType[0], false);


	private static final Map<Byte,BlockType> knownTypes = new HashMap<>();
	static {
		Arrays.asList(
				AIR,
				STONE,
				BRICK,
				STONE_03,
				WIN,
				STONE_STAIR,
				TRASH,
				STONE_WEDGE,
				GRASS_WEDGE,
				GOLDEN_BALL,
				METAL_WIN,
				WATER,
				GRASS,
				BLACK_PILLAR,
				STONE_QUARTER,
				B_BOT,
				ZAPPER,
				DRAGGABLE,
				DESERT,
				WHEEL,
				METAL_STAIR,
				METAL_QUARTER,
				MOTOR,
				METAL_17,
				STONE_18,
				METAL,
				R_BOT,
				EYE,
				BUGGY_1C,
				STONE_1D,
				CURVED_RAIL,
				STONE_PILLAR_1F,
				METAL_HALF,
				RAIL,
				STONE_HALF,
				STONE_PILLAR,
				DRAGGABLE_PILLAR,
				BALL,
				STONE_26,
				METAL_PILLAR,
				UNKNOWN_28,
				SLIDER,
				UNKNOWN_2A,
				FENCE,
				UNKNOWN_2C,
				UNKNOWN_2D,
				UNKNOWN_2E,
				UNKNOWN_2F,
				UNKNOWN_30
			).stream()
			.forEach((blk) -> knownTypes.put(blk.getValue(),blk) );
	}
	
	private BlockType parent;
	private final byte value;
	private final String name;
	private final String shortName;//1 char name
	private final BlockType[] subtypes;
	private final boolean standard;
	
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
		this(value, name, shortName, subtypes, true);
	}
	private BlockType(int value, String name, String shortName,BlockType[] subtypes,boolean standard) {
		this((byte)value, name, shortName, subtypes, standard);
	}
	private BlockType(byte value, String name, String shortName,BlockType[] subtypes,boolean standard) {
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
		this.standard = standard;
	}

	public static BlockType fromByte(byte b) {
		if(knownTypes.containsKey(b)) {
			return knownTypes.get(b);
		} else {
			BlockType t = new BlockType(b, String.format("Blk%02x",b),"?");
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
		if(standard)
			return name;
		else
			return name+"*";
	}
	public static boolean isSolid(BlockType blk) {
		return blk != BlockType.AIR &&
				blk != BlockType.WATER &&
				blk != BlockType.B_BOT &&
				blk != BlockType.R_BOT;
	}
	public boolean isStandard() {
		return standard;
	}
}

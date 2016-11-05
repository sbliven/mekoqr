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
 
/**
 * Represents a specific instance of a block
 * @author blivens
 *
 */
public class Block {
	final private int x,y,z;
	final private BlockType type;
	
	public Block(BlockType type, int x, int y, int z) {
		super();
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockType getType() {
		return type;
	}

	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getZ() {
		return z;
	}
	
	@Override
	public String toString() {
		return String.format("(%d,%d,%d)->%s",x,y,z,type.getName());
	}
}

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

import static us.bliven.mekoqr.MekoLevel.SIZE;
import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;

public class Rotate {
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
		
		level.setTitle("Customized");
		level.setAuthor("Level");
		
		for(int x=0;x<SIZE;x++) {
			for(int z=0;z<SIZE;z++) {
				if( (x+z)% 2 == 0) {
					level.setBlock(x, 5, z, BlockType.BRICK);
				} else {
					level.setBlock(x, 4, z, BlockType.GRASS);
				}
			}
		}
		
		MekoWriter writer = new MekoWriter();
		File out = new File(outputFile);
		try {
			writer.write(out, level);
		} catch (WriterException | IOException e) {
			System.err.println("Error writing rotated file: "+e.getMessage());
			System.exit(1); return;
		}
	}
}

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
 
package us.bliven.mekoqr.json;

import us.bliven.mekoqr.BlockType;
import us.bliven.mekoqr.MekoLevel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
 
public class JsonTransformer {
	private Gson gson;

	public JsonTransformer() {
		super();
		gson = new GsonBuilder()
		.registerTypeAdapter(MekoLevel.class, new MekoLevelSerializer())
		.registerTypeAdapter(BlockType.class, new BlockTypeSerializer())
		.setPrettyPrinting()
		.create();
	}

	public String render(Object model) {
		return gson.toJson(model);
	}
}

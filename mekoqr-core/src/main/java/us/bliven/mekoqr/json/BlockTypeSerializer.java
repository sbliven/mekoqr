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

import java.lang.reflect.Type;

import us.bliven.mekoqr.BlockType;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class BlockTypeSerializer implements JsonSerializer<BlockType> {
	//private static final Logger logger = LoggerFactory.getLogger(BlockTypeSerializer.class);

	@Override
	public JsonElement serialize(BlockType src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		//json.addProperty("multipleAlignment", MultipleAlignmentWriter.toAlignedResidues(src.getMultipleAlignment()));
		json.addProperty("value", src.getValue());
		json.addProperty("name",src.getName());
		// Avoid recursion by serializing parent and not subtypes
		if(src.getParent() != null) {
			json.add("parent", context.serialize(src.getParent()));
		}
		return json;
	}
}

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

import us.bliven.mekoqr.MekoLevel;
import us.bliven.mekoqr.Utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MekoLevelSerializer implements JsonSerializer<MekoLevel> {
	//private static final Logger logger = LoggerFactory.getLogger(MekoLevelSerializer.class);

	@Override
	public JsonElement serialize(MekoLevel src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		//json.addProperty("multipleAlignment", MultipleAlignmentWriter.toAlignedResidues(src.getMultipleAlignment()));
		json.addProperty("title",src.getTitle());
		json.addProperty("author", src.getAuthor());
		if(src.getRawData() != null) {
			json.addProperty("rawData", Utils.bytesToHex(src.getRawData()));
		}
		if(src.getSerializedData() != null) {
			json.addProperty("serializedData", Utils.bytesToHex(src.getSerializedData()));
		}
		json.add("data",context.serialize(src.getBlocks()));
		return json;
	}
}

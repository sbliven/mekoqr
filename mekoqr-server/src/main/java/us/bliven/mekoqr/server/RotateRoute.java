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
 * Created on Nov 8, 2016
 * Author: blivens 
 *
 */
 
package us.bliven.mekoqr.server;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.WriterException;

import spark.Request;
import spark.Response;
import spark.Route;
import us.bliven.mekoqr.MekoLevel;
import us.bliven.mekoqr.MekoWriter;
import us.bliven.mekoqr.Rotate;
import static spark.Spark.halt;
 
public class RotateRoute implements Route {
	private static final Logger logger = LoggerFactory.getLogger(RotateRoute.class);

	private MekoLevelRoute levelRoute = new MekoLevelRoute();
	private MekoWriter writer = new MekoWriter();

	@Override
	public Object handle(Request request, Response response) {
		// Parse level from request
		MekoLevel level = levelRoute.handle(request, response);
		if(level == null) {
			return null;
		}
		
		// Rotate level
		int rotations;
		String rotationsParam = request.queryParams("rotation");
		if( rotationsParam == null) {
			logger.warn("Require parameter rotation=[1,2,3]");
			halt(400,"Require parameter rotation=[1,2,3]");
			return null;
		}
		try {
			
			rotations = Integer.parseInt(rotationsParam);
		} catch( NumberFormatException e) {
			logger.warn("Invalid rotation parameter {}",rotationsParam);
			halt(400,"Invalid rotation parameter");
			return null;
		}
		Rotate.rotate(level, rotations);
		
		// Generate output image
		try( ServletOutputStream out = response.raw().getOutputStream() ) {
			writer.write(out, level);
		} catch (IOException | WriterException e) {
			logger.error("Error generating QR code",e);
			halt(400,"Unable to generate QR code");
			return null;
		}

		response.type("image/png");
		
		return ""; //Value overridden by the output stream
	}
}

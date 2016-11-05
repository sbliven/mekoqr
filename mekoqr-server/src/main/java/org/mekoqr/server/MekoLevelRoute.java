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
 * Created on Nov 5, 2016
 * Author: blivens 
 *
 */
 
package org.mekoqr.server;

import static spark.Spark.halt;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;
import spark.Route;
import us.bliven.mekoqr.MekoLevel;
import us.bliven.mekoqr.MekoReader;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
 
public class MekoLevelRoute implements Route {
	private static final Logger logger = LoggerFactory.getLogger(MekoLevelRoute.class);

	@Override
	public MekoLevel handle(Request request, Response response) {
		logger.info("Requested json");
	    request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/cache"));
	    
	    try {
	    	Part part = request.raw().getPart("uploaded_file");
	    	logger.info("Got part {}",part != null);
	    	try (InputStream is = part.getInputStream()) {
	    		logger.info("Got input stream with {} available",is.available());

	    		MekoReader reader = new MekoReader(true, true);
	    		MekoLevel level = reader.readQR(is);
	    		logger.info("Parsed the level");
	    		return level;
	    	}
	    } catch (IOException | ServletException | NotFoundException | ChecksumException | FormatException | DataFormatException e) {
	    	logger.error(e.getMessage(),e);
	    	halt(599,e.getMessage());
	    	return null;
		}
	}
}

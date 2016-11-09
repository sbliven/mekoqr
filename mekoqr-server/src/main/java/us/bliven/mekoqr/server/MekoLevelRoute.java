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
import java.util.zip.ZipException;

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
	    request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/cache"));
	    
	    try {
	    	Part part = request.raw().getPart("uploaded_file");
	    	try (InputStream is = part.getInputStream()) {
	    		logger.debug("Got input stream with {} available",is.available());

	    		MekoReader reader = new MekoReader(true, true);
	    		MekoLevel level = reader.readQR(is);
	    		logger.debug("Parsed the level");
	    		return level;
	    	}
	    } catch(NotFoundException e) {
	    	// NotFoundExceptions don't have message or stack trace
	    	logger.error("No valid QR code found");
	    	halt(500,"No valid QR code found");
	    	return null;
	    } catch (ZipException e) {
	    	logger.error("Invalid QR code");
	    	halt(500,"QR code is not a Mekorama level.");
	    	return null;
	    } catch (IOException | ServletException | ChecksumException | FormatException | DataFormatException e) {
	    	String msg = e.getMessage();
	    	if(msg == null) {
	    		msg = e.getClass().getSimpleName();
	    	}
	    	logger.error(msg,e);
	    	halt(500,msg);
	    	return null;
		}
	}
}

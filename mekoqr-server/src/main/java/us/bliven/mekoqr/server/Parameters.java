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
 * Created on Nov 9, 2016
 * Author: blivens 
 *
 */
 
package us.bliven.mekoqr.server;
 
public class Parameters {

	private int port = 8888;
	private String webroot = "/mekoqr";
	private boolean localhost = false;
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getWebroot() {
		return webroot;
	}
	public void setWebroot(String webroot) {
		this.webroot = webroot;
	}
	public boolean isLocalhost() {
		return localhost;
	}
	public void setLocalhost(boolean localhost) {
		this.localhost = localhost;
	}
	
}

package us.bliven.mekoqr.server;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.ModelAndView;
import spark.ResponseTransformer;
import spark.resource.AbstractFileResolvingResource;
import spark.resource.ExternalResource;
import spark.staticfiles.MimeType;
import spark.template.handlebars.HandlebarsTemplateEngine;

/**
 * Hello world!
 *
 */
public class Main 
{
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	private static class JsonTransformer extends  us.bliven.mekoqr.json.JsonTransformer implements ResponseTransformer {}
	
    public static void main( String[] args )
    {
    	Parameters params = new Parameters();
    	port(params.getPort());
    	String webroot = params.getWebroot();

    	if( webroot.isEmpty()) {
	    	if (params.isLocalhost() ) {
	    	    String projectDir = System.getProperty("user.dir");
	    	    String staticDir = "/src/main/resources/static";
	    	    File statics = new File(projectDir,staticDir);
	    	    if( statics.exists()) {
	    	    	staticFiles.externalLocation(projectDir + staticDir);
	    	    } else {
	        	    staticFiles.location("/static");
	    	    }
	    	} else {
	    	    staticFiles.location("/static");
	    	}
    	} else {
    		try {
				findResources("static", (root,path) -> {
					String route = webroot+"/"+path;
					String resourcePath = "/static/"+path.toString();
					logger.debug("Mapping {} to {}",route, resourcePath);
					get(webroot+"/"+path, (req,res) -> {
						Files.copy(root.resolve(path), res.raw().getOutputStream());
						AbstractFileResolvingResource resource = new ExternalResource(resourcePath);
						String contentType = MimeType.fromResource(resource);
						res.type(contentType );
						return "";
					} );
				});
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // must lack leading slash
    	}
    	

    	HandlebarsTemplateEngine hbars = new HandlebarsTemplateEngine("/templates");
    	//Handlebars handlebars = getHandlebars(hbars);
    	
    	get(webroot+"/", (req, res) -> {res.redirect(webroot+Routes.ROOT);return "Moved";});
    	get("/", (req, res) -> {res.redirect(webroot+Routes.ROOT);return "Moved";});
    	get(webroot+Routes.ROOT,
    			(req, res) -> new ModelAndView(params, "index.html.hbs"),
    			hbars);
    	
		get(webroot+Routes.DECODE,
				(req, res) -> new ModelAndView(params, "decode.html.hbs"),
				hbars);
		post(webroot+Routes.DECODE_JSON, new MekoLevelRoute(),new JsonTransformer());
		
		get(webroot+Routes.ROTATE,
				(req, res) -> new ModelAndView(params, "rotate.html.hbs"),
				hbars);
		post(webroot+Routes.ROTATE_PNG, new RotateRoute());
		
		// Better exception handling, for debugging
//		if(params.isLocalhost())
		exception(Exception.class, (exception, request, response) -> {
			String trace;
			try (StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw) ) {
				exception.printStackTrace(pw);
				trace = sw.toString();
			} catch(IOException e) {
				trace=exception.getClass().getSimpleName();
			}
			String body = String.format("<html><body>"
					+ "<h1>500 error</h1>"
					+ "<h2>%s</h2>"
					+ "<pre>%s</pre>"
					+ "</body></html>",exception.getMessage(),trace.toString());
			response.body(body);
			response.status(500);
		});

    }

    /**
     * Find all resources within a particular resource directory
     * @param root base resource directory. Should omit the leading / (e.g. "" for all resources)
     * @param fn Function called for each resource with a Path corresponding to root and a relative path to the resource.
     * @throws URISyntaxException
     */
	public static void findResources(String root,BiConsumer<Path,Path> fn) throws URISyntaxException {
		ClassLoader cl = Main.class.getClassLoader();
		URL url = cl.getResource(root);		
		assert "file".equals(url.getProtocol());
		logger.debug("Static files loaded from {}",root);
		Path p = Paths.get(url.toURI());
		findAllFiles(p, (path) -> fn.accept(p,p.relativize(path)) );
	}
	/**
	 * Recursively search over a directory, running the specified function for every regular file.
	 * @param root Root directory
	 * @param fn Function that gets passed a Path for each regular file
	 */
	private static void findAllFiles(Path root, Consumer<Path> fn) {
        try( DirectoryStream<Path> directoryStream = Files.newDirectoryStream(root)) {
        	for(Path path : directoryStream) {
            	if(Files.isDirectory(path)) {
            		findAllFiles(path, fn);
            	} else {
            		fn.accept(path);
            	}
            }
        } catch (IOException ex) {}
	}

//	/**
//	 * workaround to get the underlying hbars instance.
//	 * Future versions of spark-handlebars will make it easier to access without reflection.
//	 * @param hbars
//	 * @return
//	 */
//	private static Handlebars getHandlebars(HandlebarsTemplateEngine hbars) {
//		try {
//			Field member = HandlebarsTemplateEngine.class.getDeclaredField("handlebars");
//			member.setAccessible(true);
//			return (Handlebars) member.get(hbars);
//		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//			return null;
//		}
//		
//	}
}

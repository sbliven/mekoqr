package us.bliven.mekoqr.server;

import static spark.Spark.*;
import spark.ResponseTransformer;

/**
 * Hello world!
 *
 */
public class Main 
{
	private static class JsonTransformer extends  us.bliven.mekoqr.json.JsonTransformer implements ResponseTransformer {}
	
    public static void main( String[] args )
    {
    	boolean localhost = true;
    	port(8888);
    	
    	
    	if (localhost) {
    	    String projectDir = System.getProperty("user.dir");
    	    String staticDir = "/src/main/resources/static";
    	    staticFiles.externalLocation(projectDir + staticDir);
    	} else {
    	    staticFiles.location("/static");
    	}

    	get("/", (req, res) -> {res.redirect("/index.html");return "Moved";});
    	    	
		post("/json", new MekoLevelRoute(),new JsonTransformer());
		
		post("/rotate/:rotations", new RotateRoute());
    }
}

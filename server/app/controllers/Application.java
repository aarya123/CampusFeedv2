package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok("hello world");
    }
    
    public static Result create_user() {
    	return TODO;
    }

}

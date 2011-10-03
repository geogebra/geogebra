package geogebra.plugin;

import geogebra.main.Application;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class CallJavaScript {
	public static void evalScript(Application app, String script, String arg) {
		//Application.debug(app.getKernel().getLibraryJavaScript() + script);
        Context cx = Context.enter();
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.
            Scriptable scope = cx.initStandardObjects();

            // initialise the JavaScript variable applet so that we can call
            // GgbApi functions, eg ggbApplet.evalCommand()
            GeoGebraGlobal.initStandardObjects(app, scope, arg, false);

            // JavaScript to execute
            //String s = "ggbApplet.evalCommand('F=(2,3)')";
            
            // No class loader for unsigned applets so don't try and optimize.
            // http://www.mail-archive.com/batik-dev@xmlgraphics.apache.org/msg00108.html
            if (!app.hasFullPermissions()) {
            	cx.setOptimizationLevel(-1);
            	Context.setCachingEnabled(false);
            }
            // Now evaluate the string we've collected.
            Object result = cx.evaluateString(scope, app.getKernel().getLibraryJavaScript() + script , app.getPlain("ErrorAtLine"), 1, null);

            // Convert the result to a string and print it.
            //Application.debug("script result: "+(Context.toString(result)));
        
		
}


}

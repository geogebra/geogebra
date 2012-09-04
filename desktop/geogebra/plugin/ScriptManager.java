package geogebra.plugin;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.main.AppD;

//import org.concord.framework.data.stream.DataListener;
//import org.concord.framework.data.stream.DataStreamEvent;
//import org.concord.sensor.SensorDataProducer;
//import org.mozilla.javascript.Context;
//import org.mozilla.javascript.Scriptable;


public class ScriptManager extends geogebra.common.plugin.ScriptManagerCommon {
	
	
	// library of functions that is available to all JavaScript calls
	// init() is called when GeoGebra starts up (eg to start listeners)
	/*
	private String libraryScriptxxx ="function ggbOnInit() {}";
	private String libraryScriptxx ="function ggbOnInit() {"+
		"ggbApplet.evalCommand('A=(1,2)');" +
	//"ggbApplet.registerAddListener('listener');" +
	"ggbApplet.registerObjectUpdateListener('A','listener');" +
			"}" +
			"function listener() {//java.lang.System.out.println('add listener called');\n" +
			"var x = ggbApplet.getXcoord('A');" +
			"var y = ggbApplet.getYcoord('A');" +
			"var len = Math.sqrt(x*x + y*y);" +
			"if (len > 5) { x=x*5/len; y=y*5/len; }" +
			"" +
			"ggbApplet.unregisterObjectUpdateListener('A');" +
			"ggbApplet.setCoords('A',x,y);" +
			"ggbApplet.registerObjectUpdateListener('A','listener');" +
			"}";*/
	
	public ScriptManager(App app) {
		this.app = app;
		
		//evalScript("ggbOnInit();");
	}
	
	public void ggbOnInit() {
		
		try {
			// call only if libraryJavaScript is not the default (ie do nothing)
			if (!((AppD)app).getKernel().getLibraryJavaScript().equals(Kernel.defaultLibraryJavaScript))
				CallJavaScript.evalScript(((AppD)app), "ggbOnInit();", null);
		} catch (Exception e) {
			App.debug("Error calling ggbOnInit(): "+e.getMessage());
		}
		
		// Python
		String libraryPythonScript = app.getKernel().getLibraryPythonScript();
		if (!libraryPythonScript.equals(Kernel.defaultLibraryPythonScript)) {
			((AppD)app).getPythonBridge().execScript();
		}

	}
	



	
	public synchronized void initJavaScript() {
		
		if (app.isApplet()) {
			((AppD)app).getApplet().initJavaScript();
		}
	}
	
	public void callJavaScript(String jsFunction, Object [] args) {		
		
		if (app.isApplet() && app.useBrowserForJavaScript()) {
			((AppD)app).getApplet().callJavaScript(jsFunction, args);
		} else {

			
			StringBuilder sb = new StringBuilder();
			sb.append(jsFunction);
			sb.append("(");
			for (int i = 0 ; i < args.length ; i++) {
				sb.append('"');
				sb.append(args[i].toString());
				sb.append('"');
				if (i < args.length - 1) sb.append(",");
			}
			sb.append(");");
			
			App.debug(sb.toString());
			
			CallJavaScript.evalScript(app, sb.toString(), null);

		}
	}

	USBFunctions usb = null;
	
	public USBFunctions getUSBFunctions() {
		if (usb == null) usb = new USBFunctions(this);
		
		return usb;
	}


}

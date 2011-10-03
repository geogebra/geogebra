package geogebra.plugin;


/*
 * class to call JavaScript methods from a Timer
 * to avoid security problems
 * eg reading geogebra_properties.jar (when there's an error)
 * eg reading the undo.ggb files
 *
public class JavaScriptMethodHandler implements ActionListener{

	String methodName;
	Object [] params;
	Object methodResult;

	private Timer timer;

	Kernel kernel;
	GgbAPI ggbapi = null;

	public JavaScriptMethodHandler(Kernel kernel) {
		this.kernel = kernel;
		timer = new Timer(1, this);
		timer.stop();
	}

	public synchronized Object processMethod(String methodName, Object [] params) {   
		// remember methodName and params into private variables of JavaScriptMethodHandler
		this.methodName = methodName;
		this.params = params;

		// delete previous result, private Object methodResult in JavaScriptMethodHandler
		this.methodResult = null; 

		// start timer to process method		
		//   timer will then process the methodName and params and 
		//   store the result into methodResult
		timer.start();

		// wait for timer to put result into methodResult
		while (methodResult == null) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
		}

		timer.stop();
		return methodResult;
	}

	/**
	 * Called by Timer to process the method set in processMethod and
	 * put back  
	 *
	public void actionPerformed(ActionEvent arg0) {
		timer.stop();

		ggbapi = kernel.getApplication().getGgbApi();
		
		Application.debug("Background " + methodName+": "+(String)params[0]);

		if (methodName.equals("evalCommand")) {
			methodResult = new MyBoolean(ggbapi.evalCommand((String)params[0]));
		}
		else {
			// failsafe 
			methodResult = "ErrorFromJavaScriptMethodHandler";
			Application.debug((String)methodResult+" "+methodName);
		}
	}
}*/

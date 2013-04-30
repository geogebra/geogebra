package geogebra.cas.giac;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.Evaluate;
import geogebra.common.cas.giac.CASgiac;
import geogebra.common.kernel.AsynchronousCommand;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.main.AppD;
import javagiac.context;
import javagiac.gen;
import javagiac.giac;

public class CASgiacD extends CASgiac implements Evaluate {

	private AppD app;

	public CASgiacD(CASparser casParser, CasParserTools t, Kernel k) {
		super(casParser);
		
		this.app = (AppD) k.getApplication();
		
		App.setCASVersionString("GIAC");
	}
	
	static boolean giacLoaded = false;

	static {
		try {
			App.debug("Loading Giac dynamic library");
			String file;
			if ("AMD64".equals(System.getenv("PROCESSOR_ARCHITECTURE"))) {
				file = "javagiac64";
			} else {
				file = "javagiac";
			}
			
			System.loadLibrary(file);
			
			giacLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
			App.debug("Failed to load Giac dynamic library");
		}
	}

	private context C;

	public String evaluate(String exp) throws Throwable {

		String ret;
		
		App.debug("giac  input: "+exp);		

		if (app.isApplet() && (!AppD.hasFullPermissions() || !giacLoaded)) {
			// can't load DLLs in unsigned applet
			// so use JavaScript version instead
			
			String[] args = { exp };
			
			Object jsRet = app.getApplet().callJavaScript("_ggbCallGiac", args);
			
			if (jsRet instanceof String) {
				ret = (String) jsRet;
			} else {
				ret = "?";
				String type = (jsRet == null) ? "*null*" : jsRet.getClass()+"";
				App.debug("wrong type returned from JS: " + type);
			}
			
		} else {
			initialize();

			gen g = new gen(exp, C);
			g = giac._eval(g, C);
			ret = g.print(C);
		}

		App.debug("giac output: " + ret);		

		return ret;
	}

	public String evaluate(String exp, long timeoutMilliseconds) throws Throwable {
		return evaluate(exp);
	}

	public void initialize() throws Throwable {
		if (C == null) {
			C = new context();
		}

	}

	public void initCAS() {
		App.error("unimplemented");
		
	}

	public void evaluateGeoGebraCASAsync(AsynchronousCommand c) {
		App.error("unimplemented");
		
	}

	@Override
	public String evaluateCAS(String exp) {
		try {
			return evaluate(exp);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}


}

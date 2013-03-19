package geogebra.cas.giac;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.Evaluate;
import geogebra.common.cas.giac.CASgiac;
import geogebra.common.kernel.AsynchronousCommand;
import geogebra.common.main.App;
import javagiac.context;
import javagiac.gen;
import javagiac.giac;

public class CASgiacD extends CASgiac implements Evaluate {

	public CASgiacD(CASparser casParser, CasParserTools t) {
		super(casParser);
	}

	static {
		try {
			App.debug("Loading giac java interface");
			if ("AMD64".equals(System.getenv("PROCESSOR_ARCHITECTURE"))) {
				System.loadLibrary( "javagiac64" );
			} else {
				System.loadLibrary( "javagiac" );
			}
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
			System.err.println("Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help.\n" + e);
			System.exit(1);
		}
	}

	private context C;

	public String evaluate(String exp) throws Throwable {

		App.error("giac  input: "+exp);		

		initialize();

		gen g = new gen(exp, C);
		g = giac._eval(g, C);
		String ret = g.print(C);

		App.error("giac output: " + ret);		

		return ret;
	}

	public String evaluate(String exp, long timeoutMilliseconds) throws Throwable {
		return evaluate(exp);
	}

	public void initialize() throws Throwable {
		if (C == null) {
			C=new context();
		}

	}

	public void initCAS() {
		App.error("unimplemented");
		
	}

	public void evaluateGeoGebraCASAsync(AsynchronousCommand c) {
		App.error("unimplemented");
		
	}

	public void loadPackagesFor(String string) {
		// not needed for giac
	}

	public void loadGroebner() {
		// not needed for giac	
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

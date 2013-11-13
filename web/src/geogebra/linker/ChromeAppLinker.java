package geogebra.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.linker.SingleScriptLinker;

/**
 * @author gabor
 *	Makes files for chrome app load nice.
 */
public class ChromeAppLinker extends SingleScriptLinker {
	@Override
	  protected String getSelectionScriptTemplate(TreeLogger logger,
	      LinkerContext context) throws UnableToCompleteException {
	    return "ChromeAppTemplate.js";
	  }
}

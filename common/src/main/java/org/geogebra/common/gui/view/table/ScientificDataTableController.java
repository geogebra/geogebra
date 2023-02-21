package org.geogebra.common.gui.view.table;

import org.geogebra.common.gui.dialog.handler.DefineFunctionHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Controller for the data table functionality in Scientific Calculator
 * (<a href="https://geogebra-jira.atlassian.net/browse/APPS-4323">epic link</a>).
 */
public final class ScientificDataTableController {

	private Kernel kernel;
	private DefineFunctionHandler fHandler;
	private DefineFunctionHandler gHandler;
	private GeoFunction f;
	private GeoFunction g;

	/**
	 * Creates a new instance.
	 *
	 * This has no side effects.
	 *
	 * @param kernel The kernel.
	 */
	public ScientificDataTableController(Kernel kernel) {
		this.kernel = kernel;
		fHandler = new DefineFunctionHandler(kernel);
		gHandler = new DefineFunctionHandler(kernel);
	}

	/**
	 * Creates two functions f and g in the kernel's construction,
	 * and adds them to the table of values.
	 *
	 * @param table Functions f and g will be added as columns to this TableValues view.
	 */
	public void setup(TableValues table, boolean resetUndoInfo) {
		f = createFunction(kernel.getConstruction(), "f");
		g = createFunction(kernel.getConstruction(), "g");

		table.addAndShow(f);
//		int fIndex = table.getColumn(f); // in case we would need this at some point
		table.addAndShow(g);
//		int gIndex = table.getColumn(g); // in case we would need this at some point
		try {
			table.setValues(-2, 2, 1);
		} catch (InvalidValuesException e) {
			throw new RuntimeException(e);
		} finally {
			if (resetUndoInfo) {
				kernel.getConstruction().getUndoManager().clearUndoInfo();
				kernel.getConstruction().storeUndoInfo();
			}
		}
	}

	private GeoFunction createFunction(Construction construction, String label) {
		GeoFunction function = new GeoFunction(construction);
		function.setAuxiliaryObject(true);
		function.rename(label);
		return function;
	}

	/**
	 * (Re)defines functions f and g using the given inputs.
	 *
	 * Creates an undo point if the definition of either f or g did change
	 * in the kernel.
	 *
	 * @param fInput The new definition for f. Can be null.
	 * @param gInput The new definition for g. Can be null.
	 * @return False if an error has occurred, true in case of no errors. Use
	 * hasFDefinitionErrorOccurred() and hasGDefinitionErrorOccurred() to find
	 * out which input caused an error.
	 */
	public boolean defineFunctions(String fInput, String gInput) {
		fHandler.resetError();
		gHandler.resetError();
		boolean fChanged = false;
		if (fInput != null) {
			fChanged = defineFunction(fHandler, f, fInput);
		}
		boolean gChanged = false;
		if (gInput != null) {
			gChanged = defineFunction(gHandler, g, gInput);
		}
		if (fChanged || gChanged) {
			kernel.storeUndoInfo();
		}
		return !fHandler.hasErrorOccurred() && !gHandler.hasErrorOccurred();
	}

	/**
	 * (Re)defines a function.
	 * @param handler The DefineFunctionHandler for this function.
	 * @param function The function to (re)define.
	 * @param input The new definition for the function.
	 * @return True if the definition of the function did change, false otherwise.
	 */
	private boolean defineFunction(DefineFunctionHandler handler, GeoFunction function, String input) {
		String oldDefinition = getDefinitionOf(function);
		handler.handle(input, function);
		String newDefinition = getDefinitionOf(function);
		if (oldDefinition == null && newDefinition == null) {
			return false;
		}
		if (((oldDefinition != null) && !oldDefinition.equals(newDefinition)) ||
				((newDefinition != null) && !newDefinition.equals(oldDefinition))) {
			return true;
		}
		return false;
	}

	/**
	 * @return True if the last call to defineFunctions() resulted in an error
	 * for the definition of f.
	 */
	public boolean hasFDefinitionErrorOccurred() {
		return fHandler.hasErrorOccurred();
	}

	/**
	 * @return True if the last call to defineFunctions() resulted in an error
	 * for the definition of g.
	 */
	public boolean hasGDefinitionErrorOccurred() {
		return gHandler.hasErrorOccurred();
	}

	/**
	 * @return The current definition of f in the construction.
	 */
	public GeoFunction getFunctionF() {
		return getFunction(f, true);
	}

	/**
	 * @return The current definition of g in the construction.
	 */
	public GeoFunction getFunctionG() {
		return getFunction(g, true);
	}

	private GeoFunction getFunction(GeoFunction function, boolean returnNullIfUndefined) {
		if (function == null) {
			return null;
		}
		if (returnNullIfUndefined && !function.isDefined()) {
			return null;
		}
		return function;
	}

	/**
	 * @return The (right-hand side of) the current definition of f (i.e., without
	 * the "f(x) = "), or null if f is undefined.
	 */
	public String getDefinitionOfF() {
		return getDefinitionOf(getFunction(f, false));
	}

	/**
	 * @return True if f is defined (i.e., getDefinitionOfF() returns a non-null value), false otherwise.
	 */
	public boolean isFDefined() {
		return getDefinitionOfF() != null;
	}

	/**
	 * @return The (right-hand side of) the current definition of g (i.e., without
	 * the "g(x) = "), or null if g is undefined.
	 */
	public String getDefinitionOfG() {
		return getDefinitionOf(getFunction(g, false));
	}

	/**
	 * @return True if g is defined (i.e., getDefinitionOfG() returns a non-null value), false otherwise.
	 */
	public boolean isGDefined() {
		return getDefinitionOfG() != null;
	}

	private String getDefinitionOf(GeoFunction function) {
		if (function == null) {
			return null;
		}
		String definition = rhs(function.toString(StringTemplate.defaultTemplate));
		return "?".equals(definition) ? null : definition;
	}

	private String rhs(String definition) {
		if (definition == null) {
			return definition;
		}
		int equalSignIndex = definition.indexOf("=");
		return definition.substring(equalSignIndex + 1).trim();
	}
}

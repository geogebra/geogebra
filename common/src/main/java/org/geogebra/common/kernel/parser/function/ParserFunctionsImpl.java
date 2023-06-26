package org.geogebra.common.kernel.parser.function;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.kernel.arithmetic.ArcTrigReplacer;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

/**
 * Handles function references for Parser.
 *
 * @author zbynek
 */
class ParserFunctionsImpl implements ParserFunctions {

	private static class FunctionReference {

		private String name;
		private int size;
		private String arguments;
		private Operation operation;

		FunctionReference(String name, int size, String arguments, Operation operation) {
			this.name = name;
			this.size = size;
			this.arguments = arguments;
			this.operation = operation;
		}
	}

	private final List<FunctionReference> translatables = new ArrayList<>();
	private final FunctionReferences references = new FunctionReferences();
	private FunctionReferences localizedReferences = new FunctionReferences();

	private boolean inverseTrig = false;

	/**
	 * Add a function reference.
	 *
	 * @param name name of the function
	 * @param size number of arguments
	 * @param args the format of the arguments (e.g. <code>"< (x) >"</code>
	 * @param op the operation
	 */
	void add(String name, int size, String args, Operation op) {
		references.put(size, name, op, args);
	}

	/**
	 * Add a reserved function name.
	 *
	 * @param name reserved function name
	 */
	void addReserved(String name) {
		references.putReserved(name);
	}

	/**
	 * Add a translatable function name. This will be added to the
	 * collection of functions when updateLocale is called.
	 *
	 * @param name name of the translatable function
	 * @param size number of arguments
	 * @param args the format of the arguments (e.g. <code>"< (x) >"</code>)
	 * @param op the operation
	 */
	void addTranslatable(String name, int size, String args, Operation op) {
		translatables.add(new FunctionReference(name, size, args, op));
	}

	/**
	 * Adds translatable single argument operation
	 * @param fn function name
	 * @param arg argument (for autcomplete)
	 */
	public void addTranslatable(String fn, String arg) {
		addTranslatable(fn, 1, arg, get(fn ,1));
	}

	@Override
	public void updateLocale(Localization loc) {
		localizedReferences = new FunctionReferences();

		for (FunctionReference reference: translatables) {
			String localized = loc.getFunction(reference.name, reference.size != 1);
			localizedReferences.put(reference.size, localized, reference.operation, reference.arguments);
		}
	}

	@Override
	public Operation get(String name, int size) {
		Operation operation = localizedReferences.get(name, size);
		operation = operation == null ? references.get(name, size) : operation;

		if (!inverseTrig || operation == null) {
			return operation;
		}
		return ArcTrigReplacer.getDegreeInverseTrigOp(operation);
	}

	@Override
	public boolean isReserved(String name) {
		return references.isReserved(name) || localizedReferences.isReserved(name);
	}

	@Override
	public ArrayList<String> getCompletions(String prefix) {
		TreeSet<String> completions = new TreeSet<String>();
		references.getCompletions(prefix, completions);
		localizedReferences.getCompletions(prefix, completions);
		return new ArrayList<>(completions);
	}

	@Override
	public String getInternal(Localization localization, String string) {
		for (FunctionReference reference: translatables) {
			if (localization.getFunction(reference.name).equals(string)) {
				return reference.name;
			}
		}
		return null;
	}

	@Override
	public boolean isTranslatableFunction(String string) {
		for (FunctionReference reference: translatables) {
			if (reference.name.equals(string)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setInverseTrig(boolean inverseTrig) {
		this.inverseTrig = inverseTrig;
	}

	@Override
	public Operation getSingleArgumentOp(String leftImg) {
		Operation op = get(leftImg, 1);
		if (op == Operation.XCOORD || op == Operation.YCOORD || op == Operation.ZCOORD) {
			return null;
		}
		return op;
	}

	@Override
	public String toEditorAutocomplete(String text, Localization loc) {
		if (text.equals(loc.getFunction("nroot") + NROOT_SUFFIX) ||
				text.equals("nroot" + NROOT_SUFFIX)) {
			return "nroot(";
		}
		return text;
	}
}

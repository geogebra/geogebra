package org.geogebra.common.kernel.parser.cashandlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

/**
 * Handles function references for Parser.
 * 
 * @author zbynek
 */
public class ParserFunctions {

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

	public void add(String name, int size, String args, Operation op) {
		references.put(size, name, op, args);
	}

	public void addReserved(String name) {
		references.putReserved(name);
	}

	public void addTranslatable(String name, int size, String args, Operation op) {
		translatables.add(new FunctionReference(name, size, args, op));
	}

	/**
	 * Updates local names of functions
	 * 
	 * @param loc
	 *            localization
	 */
	public void updateLocale(Localization loc) {
		localizedReferences = new FunctionReferences();

		for (FunctionReference reference: translatables) {
			String localized = loc.getFunction(reference.name, reference.size != 1);
			localizedReferences.put(reference.size, localized, reference.operation, reference.arguments);
		}
	}

	/**
	 * @param name
	 *            function name
	 * @param size
	 *            number of arguments
	 * @return operation
	 */
	public Operation get(String name, int size) {
		Operation operation = localizedReferences.get(name, size);
		operation = operation == null ? references.get(name, size) : operation;

		if (!inverseTrig || operation == null) {
			return operation;
		}
		switch (operation) {
		case ARCSIN:
			return Operation.ARCSIND;
		case ARCTAN:
			return Operation.ARCTAND;
		case ARCCOS:
			return Operation.ARCCOSD;
		case ARCTAN2:
			return Operation.ARCTAN2D;
		default:
			return operation;
		}
	}

	/**
	 * Some names cannot be used for elements because of collision with
	 * predefined functions these should also be documented here:
	 * http://wiki.geogebra.org/en/Manual:Naming_Objects
	 * 
	 * @param name
	 *            label
	 * @return true if label is reserved
	 */
	public boolean isReserved(String name) {
		return references.isReserved(name) || localizedReferences.isReserved(name);
	}

	/**
	 * Find completions for a given prefix (Arnaud 03/10/2011)
	 * 
	 * @param prefix
	 *            the wanted prefix
	 * @return all the built-in functions starting with this prefix (with
	 *         brackets at the end)
	 */
	public ArrayList<String> getCompletions(String prefix) {
		ArrayList<String> completions = references.getCompletions(prefix);
		ArrayList<String> localized = localizedReferences.getCompletions(prefix);
		completions.addAll(localized);
		Collections.sort(completions);
		return completions;
	}

	/**
	 * @param localization
	 *            localization
	 * @param string
	 *            translated function
	 * @return English function name
	 */
	public String getInternal(Localization localization, String string) {
		for (FunctionReference reference: translatables) {
			if (localization.getFunction(reference.name).equals(string)) {
				return reference.name;
			}
		}
		return null;
	}

	/**
	 * @param string
	 *            english function name
	 * @return whether this is a translatable function
	 */
	public boolean isTranslatableFunction(String string) {
		for (FunctionReference reference: translatables) {
			if (reference.name.equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param inverseTrig
	 *            whether inverse trig functions should be replaced by deg
	 *            variants
	 */
	public void setInverseTrig(boolean inverseTrig) {
		this.inverseTrig = inverseTrig;
	}
}

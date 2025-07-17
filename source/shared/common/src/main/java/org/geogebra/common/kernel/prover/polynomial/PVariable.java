package org.geogebra.common.kernel.prover.polynomial;

import java.util.HashMap;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * A simple class for variables.
 * 
 * @author Simon Weitzhofer
 * @author Damien Desfontaines
 *
 */
public class PVariable implements Comparable<PVariable> {
	// private int nextAvailableNumber = 0;
	// private HashMap<String,Integer> nameToId;
	private HashMap<Integer, PVariable> twins = new HashMap<>();

	private GeoElement parent;
	// private final String name;
	private final int id;

	/**
	 * Creates a new variable
	 * 
	 * @param kernel
	 *            current kernel
	 */
	public PVariable(Kernel kernel) {
		// nextAvailableNumber++;
		// name = "v".concat(Integer.toString(nextAvailableNumber));
		// nameToId.put(name,n);
		id = kernel.getApplication().getNextVariableID();
	}

	/**
	 * Creates a new variable and sets the parent GeoElement
	 * 
	 * @param parent
	 *            the GeoElement which defines the variable
	 */
	public PVariable(GeoElement parent) {
		this(parent.getKernel());
		this.parent = parent;
	}

	/**
	 * Returns the variable v
	 * 
	 * @param v
	 *            the name of the variable
	 */
	// Removed to speed up the Variable class
	/*
	 * public Variable(String v) { if (nameToId.containsKey(v)) { name = v; id =
	 * nameToId.get(name); } else { n++; name = v; nameToId.put(name,n); id = n;
	 * } }
	 */

	/**
	 * Copies a variable
	 * 
	 * @param fv
	 *            the variable to copy
	 */
	protected PVariable(PVariable fv) {
		// name = fv.getName();
		id = fv.getId();
	}

	/**
	 * Returns the unique id of the variable
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(PVariable v) {
		int vId = v.getId();
		if (id < vId) {
			return 1;
		}
		if (id > vId) {
			return -1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PVariable) {
			return id == ((PVariable) o).getId();
		}
		return false;
	}

	/**
	 * Returns the name of the variable.
	 * 
	 * @return the name
	 */
	public String getName() {
		// return name;
		return "v".concat(Integer.toString(id));
	}

	/**
	 * Exports the variable into LaTeX
	 * 
	 * @return the LaTeX formatted variable
	 */
	public String toTeX() {
		return "v_{".concat(Integer.toString(id)).concat("}");
	}

	@Override
	public int hashCode() {
		return id;
	}

	/**
	 * Returns the Variable which describes the other coordinate of the same
	 * point
	 * 
	 * @return the Variable
	 */
	public PVariable getTwin() {
		return twins.get(id);
	}

	/**
	 * Sets the Variable which describes the other coordinate of the same point
	 * 
	 * @param twin
	 *            the Variable. Is null if there is no twin.
	 */
	public void setTwin(PVariable twin) {
		twins.put(id, twin);
	}

	/**
	 * Returns the parent GeoElement
	 * 
	 * @return the parent GeoElement
	 */
	public GeoElement getParent() {
		return parent;
	}

	/**
	 * Sets the parent GeoElement
	 * 
	 * @param parent
	 *            the parent GeoElement
	 */
	public void setParent(final GeoElement parent) {
		this.parent = parent;
	}

}

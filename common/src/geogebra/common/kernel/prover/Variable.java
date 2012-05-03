package geogebra.common.kernel.prover;

import java.util.TreeMap;

/**
 * A simple class for variables.
 * @author Simon Weitzhofer
 *
 */
public class Variable implements Comparable<Variable> {
	private static int n = 0;
	private static TreeMap<Integer,String> names;
	private Variable twin;
	private final int id;

	static {
		names=new TreeMap<Integer, String>();
	}
	
	/**
	 * Creates a new variable
	 */
	
	public Variable() {
		n++;
		id = n;
		if (id<27){
			setName(String.valueOf("xyzabcdefghijklmnopqrstuvw".charAt(id-1)));
		} else
			setName("v" + (id-26)); // v1,v2,v3,... when no more single letters available
	}

	/**
	 * Copies a variable
	 * @param fv the variable to copy
	 */
	protected Variable(Variable fv) {
		id = fv.getId();
	}

	/**
	 * Returns the unique id of the variable
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return getName();
	}

	public int compareTo(Variable v) {
		if (id < v.getId()) {
			return 1;
		}
		if (id > v.getId()) {
			return -1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Variable) {
			return id == ((Variable) o).id;
		}
		return super.equals(o);
	}

	/**
	 * Returns the name of the variable.
	 * @return the name
	 */
	public String getName() {
		return names.get(id);
	}

	/**
	 * Sets the name of the variable. Prints a warning if two different variables have the same name.
	 * @param name the name of the variable.
	 */
	public void setName(final String name) {
//		if (names.containsValue(name) && !name.equals(names.get(id))){
//			System.err.println("warning: two different variables with same name: "+name);
//		}
		names.put(id, name);
	}

	@Override
	public int hashCode() {
		return id;
	}

	/**
	 * Returns the Variable which describes the other coordinate of the same point
	 * @return the Variable
	 */
	public Variable getTwin() {
		return twin;
	}

	/**
	 * Sets the Variable which describes the other coordinate of the same point
	 * @param twin the Variable. Is null if there is no twin.
	 */
	public void setTwin(Variable twin) {
		this.twin = twin;
	}

}

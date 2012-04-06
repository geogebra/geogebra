package geogebra.common.kernel.prover;

import java.math.BigInteger;
import java.util.TreeMap;

/**
 * A simple class for variables.
 * @author Simon Weitzhofer
 *
 */
public class FreeVariable implements Comparable<FreeVariable> {
	private static int n = 0;
	private static TreeMap<Integer,String> names;
	private final int id;
	private BigInteger value;

	static {
		names=new TreeMap<Integer, String>();
	}
	
	/**
	 * Creates a new variable
	 */
	
	public FreeVariable() {
		n++;
		id = n;
	}

	/**
	 * Copies a variable
	 * @param fv the variable to copy
	 */
	protected FreeVariable(FreeVariable fv) {
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
		if (getName() == null) {
			return "freevar" + id;
		}
		return getName();
	}

	/**
	 * Returns the value associated with the variable
	 * @return the value
	 */
	public BigInteger getValue() {
		return value;
	}

	/**
	 * Sets the value associated with the variable
	 * @param value the value
	 */
	public void setValue(BigInteger value) {
		this.value = value;
	}

	public int compareTo(FreeVariable v) {
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
		if (o instanceof FreeVariable) {
			return id == ((FreeVariable) o).id;
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

}

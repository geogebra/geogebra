package geogebra.common.kernel.prover;

import java.util.TreeMap;

/**
 * A simple class for variables.
 * @author Simon Weitzhofer
 *
 */
public class FreeVariable implements Comparable<FreeVariable> {
	private static int n = 0;
	private static TreeMap<Integer,String> names;
	private FreeVariable twin;
	private final int id;

	static {
		names=new TreeMap<Integer, String>();
	}
	
	/**
	 * Creates a new variable
	 */
	
	public FreeVariable() {
		n++;
		id = n;
		if (id<26){
			setName(String.valueOf(" xyzabcdefghijklmnopqrstuvw".charAt(id)));
		} else
			setName("v" + (id-25)); // v1,v2,v3,... when no more single letters available
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

	/**
	 * Returns the FreeVariable which describes the other coordinate of the same point
	 * @return the FreeVariable
	 */
	public FreeVariable getTwin() {
		return twin;
	}

	/**
	 * Sets the FreeVariable which describes the other coordinate of the same point
	 * @param twin the FreeVariable. Is null if there is no twin.
	 */
	public void setTwin(FreeVariable twin) {
		this.twin = twin;
	}

}

package geogebra.common.kernel.prover;

import geogebra.common.kernel.geos.GeoElement;

import java.util.HashMap;

/**
 * A simple class for variables.
 * @author Simon Weitzhofer
 * @author Damien Desfontaines
 *
 */
public class Variable implements Comparable<Variable> {
	private static int n = 0;
    //private static int nextAvailableNumber = 0;
	//private static HashMap<String,Integer> nameToId;
    private static HashMap<Integer,Variable> twins;
    
    private GeoElement parent;
	//private final String name;
    private final int id;

	static {
		//nameToId = new HashMap<String, Integer>();
		twins = new HashMap<Integer, Variable>();
	}
	
	/**
	 * Creates a new variable
	 */
	public Variable() {
        n++;
        //nextAvailableNumber++;
        //name = "v".concat(Integer.toString(nextAvailableNumber));
        //nameToId.put(name,n);
        id = n;
	}
	
	/**
	 * Creates a new variable and sets the parent GeoElement
	 * @param parent the GeoElement which defines the variable
	 */
	public Variable(GeoElement parent){
		this();
		this.parent=parent;
	}

    /**
     * Returns the variable v
     * @param v the name of the variable
     */
    // Removed to speed up the Variable class
    /*
    public Variable(String v) {
        if (nameToId.containsKey(v)) {
            name = v;
            id = nameToId.get(name);
        }
        else {
            n++;
            name = v;
            nameToId.put(name,n);
            id = n;
        }
    }
    */

	/**
	 * Copies a variable
	 * @param fv the variable to copy
	 */
	protected Variable(Variable fv) {
		//name = fv.getName();
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
		if (o instanceof Variable) {
			return id == ((Variable) o).getId();
		}
		return super.equals(o);
	}

	/**
	 * Returns the name of the variable.
	 * @return the name
	 */
	public String getName() {
		//return name;
        return "v".concat(Integer.toString(id));

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
		return twins.get(id);
	}

	/**
	 * Sets the Variable which describes the other coordinate of the same point
	 * @param twin the Variable. Is null if there is no twin.
	 */
	public void setTwin(Variable twin) {
		twins.put(id,twin);
	}

	/**
	 * Returns the parent GeoElement
	 * @return the parent GeoElement
	 */
	public GeoElement getParent() {
		return parent;
	}

	/**
	 * Sets the parent GeoElement
	 * @param parent the parent GeoElement
	 */
	public void setParent(final GeoElement parent) {
		this.parent = parent;
	}
}

package org.rosuda.REngine;

/** Basic class representing an object of any type in R. Each type in R in represented by a specific subclass.
 <p>
 This class defines basic accessor methods (<tt>as</tt><i>XXX</i>), type check methods (<tt>is</tt><i>XXX</i>), gives access to attributes ({@link #getAttribute}, {@link #hasAttribute}) as well as several convenience methods. If a given method is not applicable to a particular type, it will throw the {@link REXPMismatchException} exception.
 <p>This root class will throw on any accessor call and returns <code>false</code> for all type methods. This allows subclasses to override accessor and type methods selectively.
 */
public class REXP {
	/** attribute list. This attribute should never be accessed directly. */
	protected REXPList attr;

	/** public root contrsuctor, same as <tt>new REXP(null)</tt> */
	public REXP() { }
	/** public root constructor
	 @param attr attribute list object (can be <code>null</code> */
	public REXP(REXPList attr) { this.attr=attr; }

	// type checks
	/** check whether the <code>REXP</code> object is a character vector (string)
	 @return <code>true</code> if the receiver is a character vector, <code>false</code> otherwise */
	public boolean isString() { return false; }
	/** check whether the <code>REXP</code> object is a numeric vector
	 @return <code>true</code> if the receiver is a numeric vector, <code>false</code> otherwise */
	public boolean isNumeric() { return false; }
	/** check whether the <code>REXP</code> object is an integer vector
	 @return <code>true</code> if the receiver is an integer vector, <code>false</code> otherwise */
	public boolean isInteger() { return false; }
	/** check whether the <code>REXP</code> object is NULL
	 @return <code>true</code> if the receiver is NULL, <code>false</code> otherwise */
	public boolean isNull() { return false; }
	/** check whether the <code>REXP</code> object is a factor
	 @return <code>true</code> if the receiver is a factor, <code>false</code> otherwise */
	public boolean isFactor() { return false; }
	/** check whether the <code>REXP</code> object is a list (either generic vector or a pairlist - i.e. {@link #asList()} will succeed)
	 @return <code>true</code> if the receiver is a generic vector or a pair-list, <code>false</code> otherwise */
	public boolean isList() { return false; }
	/** check whether the <code>REXP</code> object is a pair-list
	 @return <code>true</code> if the receiver is a pair-list, <code>false</code> otherwise */
	public boolean isPairList() { return false; }
	/** check whether the <code>REXP</code> object is a logical vector
	 @return <code>true</code> if the receiver is a logical vector, <code>false</code> otherwise */
	public boolean isLogical() { return false; }
	/** check whether the <code>REXP</code> object is an environment
	 @return <code>true</code> if the receiver is an environment, <code>false</code> otherwise */
	public boolean isEnvironment() { return false; }
	/** check whether the <code>REXP</code> object is a language object
	 @return <code>true</code> if the receiver is a language object, <code>false</code> otherwise */
	public boolean isLanguage() { return false; }
	/** check whether the <code>REXP</code> object is an expression vector
	 @return <code>true</code> if the receiver is an expression vector, <code>false</code> otherwise */
	public boolean isExpression() { return false; }
	/** check whether the <code>REXP</code> object is a symbol
	 @return <code>true</code> if the receiver is a symbol, <code>false</code> otherwise */
	public boolean isSymbol() { return false; }
	/** check whether the <code>REXP</code> object is a vector
	 @return <code>true</code> if the receiver is a vector, <code>false</code> otherwise */
	public boolean isVector() { return false; }
	/** check whether the <code>REXP</code> object is a raw vector
	 @return <code>true</code> if the receiver is a raw vector, <code>false</code> otherwise */
	public boolean isRaw() { return false; }
	/** check whether the <code>REXP</code> object is a complex vector
	 @return <code>true</code> if the receiver is a complex vector, <code>false</code> otherwise */
	public boolean isComplex() { return false; }
	/** check whether the <code>REXP</code> object is a recursive obejct
	 @return <code>true</code> if the receiver is a recursive object, <code>false</code> otherwise */
	public boolean isRecursive() { return false; }
	/** check whether the <code>REXP</code> object is a reference to an R object
	 @return <code>true</code> if the receiver is a reference, <code>false</code> otherwise */
	public boolean isReference() { return false; }

	// basic accessor methods
	/** returns the contents as an array of Strings (if supported by the represented object) */
	public String[] asStrings() throws REXPMismatchException { throw new REXPMismatchException(this, "String"); }
	/** returns the contents as an array of integers (if supported by the represented object) */
	public int[] asIntegers() throws REXPMismatchException { throw new REXPMismatchException(this, "int"); }
	/** returns the contents as an array of doubles (if supported by the represented object) */
	public double[] asDoubles() throws REXPMismatchException { throw new REXPMismatchException(this, "double"); }
	/** returns the contents as an array of bytes (if supported by the represented object) */
	public byte[] asBytes() throws REXPMismatchException { throw new REXPMismatchException(this, "byte"); }
	/** returns the contents as a (named) list (if supported by the represented object) */
	public RList asList() throws REXPMismatchException { throw new REXPMismatchException(this, "list"); }
	/** returns the contents as a factor (if supported by the represented object) */
	public RFactor asFactor() throws REXPMismatchException { throw new REXPMismatchException(this, "factor"); }
	/** attempt to represent the REXP by a native Java object and return it. Note that this may lead to loss of information (e.g., factors may be returned as a string array) and attributes are ignored. Not all R types can be converted to native Java objects. Also note that R has no concept of scalars, so vectors of length 1 will always be returned as an arrays (i.e., <code>int[1]</code> and not <code>Integer</code>). */
	public Object asNativeJavaObject() throws REXPMismatchException { throw new REXPMismatchException(this, "native Java Object"); }

	/** returns the length of a vector object. Note that we use R semantics here, i.e. a matrix will have a length of <i>m * n</i> since it is represented by a single vector (see {@link #dim} for retrieving matrix and multidimentional-array dimensions).
	 * @return length (number of elements) in a vector object
	 * @throws REXPMismatchException if this is not a vector object */
	public int length() throws REXPMismatchException { throw new REXPMismatchException(this, "vector"); }

	/** returns a boolean vector of the same length as this vector with <code>true</code> for NA values and <code>false</code> for any other values
	 *  @return a boolean vector of the same length as this vector with <code>true</code> for NA values and <code>false</code> for any other values
	 * @throws REXPMismatchException if this is not a vector object */
	public boolean[] isNA() throws REXPMismatchException { throw new REXPMismatchException(this, "vector"); }
	
	// convenience accessor methods
	/** convenience method corresponding to <code>asIntegers()[0]</code>
	 @return first entry returned by {@link #asInteger} */
	public int asInteger() throws REXPMismatchException { int[] i = asIntegers(); return i[0]; }
	/** convenience method corresponding to <code>asDoubles()[0]</code>
	 @return first entry returned by {@link #asDoubles} */
	public double asDouble() throws REXPMismatchException { double[] d = asDoubles(); return d[0]; }
	/** convenience method corresponding to <code>asStrings()[0]</code>
	 @return first entry returned by {@link #asStrings} */
	public String asString() throws REXPMismatchException { String[] s = asStrings(); return s[0]; }

	// methods common to all REXPs
	
	/** retrieve an attribute of the given name from this object
	 * @param name attribute name
	 * @return attribute value or <code>null</code> if the attribute does not exist */
	public REXP getAttribute(String name) {
		final REXPList a = _attr();
		if (a==null || !a.isList()) return null;
		return a.asList().at(name);
	}
	
	/** checks whether this obejct has a given attribute
	 * @param name attribute name
	 * @return <code>true</code> if the attribute exists, <code>false</code> otherwise */
	public boolean hasAttribute(String name) {
		final REXPList a = _attr();
		return (a!=null && a.isList() && a.asList().at(name)!=null);
	}
	
	
	// helper methods common to all REXPs
	
	/** returns dimensions of the object (as determined by the "<code>dim</code>" attribute)
	 * @return an array of integers with corresponding dimensions or <code>null</code> if the object has no dimension attribute */
	public int[] dim() {
		try {
			return hasAttribute("dim")?_attr().asList().at("dim").asIntegers():null;
		} catch (REXPMismatchException me) {
		}
		return null;
	}
	
	/** determines whether this object inherits from a given class in the same fashion as the <code>inherits()</code> function in R does (i.e. ignoring S4 inheritance)
	 * @param klass class name
	 * @return <code>true</code> if this object is of the class <code>klass</code>, <code>false</code> otherwise */
	public boolean inherits(String klass) {
		if (!hasAttribute("class")) return false;
		try {
			String c[] = getAttribute("class").asStrings();
			if (c != null) {
				int i = 0;
				while (i < c.length) {
					if (c[i]!=null && c[i].equals(klass)) return true;
					i++;
				}
			}
		} catch (REXPMismatchException me) {
		}
		return false;
	}

	/** this method allows a limited access to object's attributes - <b>{@link #getAttribute} should be used instead to access specific attributes</b>!. Note that the {@link #attr} attribute should never be used directly incase the REXP implements a lazy access (e.g. via a reference)
	    @return list of attributes or <code>null</code> if the object has no attributes
	 */
	public REXPList _attr() { return attr; }
	
	/** returns a string description of the object
	 @return string describing the object - it can be of an arbitrary form and used only for debugging (do not confuse with {@link #asString()} for accessing string REXPs) */
	public String toString() {
		return super.toString()+((attr!=null)?"+":"");
	}
	
	/** returns representation that it useful for debugging (e.g. it includes attributes and may include vector values -- see {@link #maxDebugItems})
	 @return extended description of the obejct -- it may include vector values
	 */
	public String toDebugString() {
		return (attr!=null)?(("<"+attr.toDebugString()+">")+super.toString()):super.toString();
	}
	
	//======= complex convenience methods
	/** returns the content of the REXP as a matrix of doubles (2D-array: m[rows][cols]). This is the same form as used by popular math packages for Java, such as JAMA. This means that following leads to desired results:<br>
	 <code>Matrix m=new Matrix(c.eval("matrix(c(1,2,3,4,5,6),2,3)").asDoubleMatrix());</code><br>
	 @return 2D array of doubles in the form double[rows][cols] or <code>null</code> if the contents is no 2-dimensional matrix of doubles */
	public double[][] asDoubleMatrix() throws REXPMismatchException {
		double[] ct = asDoubles();
		REXP dim = getAttribute("dim");
		if (dim == null) throw new REXPMismatchException(this, "matrix (dim attribute missing)");
		int[] ds = dim.asIntegers();
		if (ds.length != 2) throw new REXPMismatchException(this, "matrix (wrong dimensionality)");
		int m = ds[0], n = ds[1];
		
		double[][] r = new double[m][n];
		// R stores matrices as matrix(c(1,2,3,4),2,2) = col1:(1,2), col2:(3,4)
		// we need to copy everything, since we create 2d array from 1d array
		int k = 0;
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++) 
				r[j][i] = ct[k++];
		return r;
	}
	
	/** creates a REXP that represents a double matrix in R based on matrix of doubles (2D-array: m[rows][cols]). This is the same form as used by popular math packages for Java, such as JAMA. The result of this function can be used in {@link REngine.assign} to store a matrix in R.
	 @param matrix array <code>double[rows][colums]</code> containing the matrix to convert into a REXP. If <code>matrix</code> is <code>null</code> or either of the dimensions is 0 then the resulting matrix will have the dimensions <code>0 x 0</code> (Note: Java cannot represent <code>0 x n</code> matrices for <code>n &gt; 0</code>, so special matrices with one dimension of 0 can only be created by setting dimensions directly).
	 @return <code>REXPDouble</code> with "dim" attribute which constitutes a matrix in R */
	public static REXP createDoubleMatrix(double[][] matrix) {
		int m = 0, n = 0;
		double a[];
		if (matrix != null && matrix.length != 0 && matrix[0].length != 0) {
			m = matrix.length;
			n = matrix[0].length;
			a = new double[m * n];
			int k = 0;
			for (int j = 0; j < n; j++)
				for (int i = 0; i < m; i++)
					a[k++] = matrix[i][j];
		} else a = new double[0];
		return new REXPDouble(a,
				      new REXPList(
						   new RList(
							     new REXP[] { new REXPInteger(new int[] { m, n }) },
							     new String[] { "dim" })
						   )
				      );
	}
	
	//======= tools
	/** creates a data frame object from a list object using integer row names
	 *  @param l a (named) list of vectors ({@link REXPVector} subclasses), each element corresponds to a column and all elements must have the same length
	 *  @return a data frame object
	 *  @throws REXPMismatchException if the list is empty or any of the elements is not a vector */
	public static REXP createDataFrame(RList l) throws REXPMismatchException {
		if (l == null || l.size() < 1) throw new REXPMismatchException(new REXPList(l), "data frame (must have dim>0)");
		if (!(l.at(0) instanceof REXPVector)) throw new REXPMismatchException(new REXPList(l), "data frame (contents must be vectors)");
		REXPVector fe = (REXPVector) l.at(0);
		return
		new REXPGenericVector(l,
							  new REXPList(
									new RList(
										   new REXP[] {
											   new REXPString("data.frame"),
											   new REXPString(l.keys()),
											   new REXPInteger(new int[] { REXPInteger.NA, -fe.length() })
										   },
										   new String[] {
											   "class",
											   "names",
											   "row.names"
										   })));
	}
	
	/** specifies how many items of a vector or list will be displayed in {@link #toDebugString} */
	public static int maxDebugItems = 32;
}

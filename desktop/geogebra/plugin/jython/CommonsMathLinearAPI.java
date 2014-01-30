package geogebra.plugin.jython;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

// TODO: not in webapache
//import org.apache.commons.math.linear.SingularValueDecomposition;
//import org.apache.commons.math.linear.SingularValueDecompositionImpl;

/**
 * API for obfuscation-proof interaction between Python and apache.commons.math.linear
 * This class should not be obfuscated!
 * @author arno
 */
public class CommonsMathLinearAPI {
	
	/* Classes that we need to know */
	
	@SuppressWarnings("javadoc")
	public static final Class<RealVector> RealVectorClass = RealVector.class;
	@SuppressWarnings("javadoc")
	public static final Class<ArrayRealVector> ArrayRealVectorClass = ArrayRealVector.class;
	@SuppressWarnings("javadoc")
	public static final Class<RealMatrix> RealMatrixClass = RealMatrix.class;
	@SuppressWarnings("javadoc")
	public static final Class<Array2DRowRealMatrix> Array2DRowRealMatrixClass = Array2DRowRealMatrix.class;
	//@SuppressWarnings("javadoc")
	//public static final Class<SingularValueDecompositionImpl> SingularValueDecompositionImplClass = SingularValueDecompositionImpl.class;
	
	@SuppressWarnings("javadoc")
	public static final Class<MathRuntimeException> MathRuntimeExceptionClass = MathRuntimeException.class;
	
	/* Creating new objects */
	
	/**
	 * Create a new zero-length ArrayRealVector
	 * @return newly created ArrayRealVector object
	 */
	public static final ArrayRealVector newArrayRealVector() {
		return new ArrayRealVector();
	}
	
	/**
	 * create a new ArrayRealVector with given data array
	 * @param values array of values
	 * @return newly created ArrayRealVector object
	 */
	public static final ArrayRealVector newArrayRealVector(double values[]) {
		return new ArrayRealVector(values);
	}
	
	/**
	 * Create a new Array2DRowRealMatrix with zero rows and zero columns
	 * @return newly created Array2DRowRealMatrix object
	 */
	public static final Array2DRowRealMatrix newArray2DRowRealMatrix() {
		return new Array2DRowRealMatrix();
	}
	
	/**
	 * Create a new Array2DRowRealMatrix of given size
	 * @param rows number of rows for the new matrix
	 * @param columns number of columns for the new matrix
	 * @return newly created Array2DRowRealMatrix object
	 */
	public static final Array2DRowRealMatrix newArray2DRowRealMatrix(int rows, int columns) {
		return new Array2DRowRealMatrix(rows, columns);
	}
	
	/**
	 * Create a new Array2DRowRealMatrix with given data
	 * @param values array of rows of data
	 * @return newly created Array2DRowRealMatrix object
	 */
	public static final Array2DRowRealMatrix newArray2DRowRealMatrix(double values[][]) {
		return new Array2DRowRealMatrix(values);
	}
	
	/**
	 * Create a new SingularValueDecompositionImpl
	 * @param mat the matrix to be decomposed
	 * @return newly created SingularValueDecompositionImpl for mat
	 */
	//public static final SingularValueDecompositionImpl newSingularValueDecompositionImpl(RealMatrix mat) {
	//	return new SingularValueDecompositionImpl(mat);
	//}
	
	/* Real Vector operations */
	
	/**
	 * Add vectors
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return v1 + v2
	 */
	public static final RealVector add(RealVector v1, RealVector v2) {
		return v1.add(v2);
	}
	
	/**
	 * Subtract vectors
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return v1 - v2
	 */
	public static final RealVector subtract(RealVector v1, RealVector v2) {
		return v1.subtract(v2);
	}
	
	/**
	 * Multiply vector by scalar
	 * @param v the vector
	 * @param x the scalar
	 * @return v*x
	 */
	public static final RealVector mapMultiply(RealVector v, double x) {
		return v.mapMultiply(x);
	}
	
	/**
	 * Dot product of two vectors
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return v1.v2
	 */
	public static final double dotProduct(RealVector v1, RealVector v2) {
		return v1.dotProduct(v2);
	}
	
	/**
	 * Calculate the max-norm of a vector
	 * @param v the vector
	 * @return the norm of the vector
	 */
	public static final double getNorm(RealVector v) {
		return v.getNorm();
	}
	
	/**
	 * Return the length of a vector
	 * @param v the vector
	 * @return the length of v
	 */
	public static final int getDimension(RealVector v) {
		return v.getDimension();
	}
	
	/**
	 * Change the value at a given index in a vector
	 * @param v the vector
	 * @param i the index of the value to change
	 * @param value the new value
	 */
	public static final void setEntry(RealVector v, int i, double value) {
		v.setEntry(i, value);
	}
	
	/**
	 * Get the value at a given index in a vector
	 * @param v the vector
	 * @param i the index
	 * @return v_i
	 */
	public static final double getEntry(RealVector v, int i) {
		return v.getEntry(i);
	}
	
	
	/* Real Matrix operations */
	
	/**
	 * Add matrices
	 * @param m1 first matrix
	 * @param m2 second matrix
	 * @return m1 + m2
	 */
	public static final RealMatrix add(RealMatrix m1, RealMatrix m2) {
		return m1.add(m2);
	}
	
	/**
	 * Add matrix and scalar
	 * @param m the matrix
	 * @param x the scalar
	 * @return the matrix (m_ij + x)
	 */
	public static final RealMatrix scalarAdd(RealMatrix m, double x) {
		return m.scalarAdd(x);
	}
	
	/**
	 * Subtract matrices
	 * @param m1 first matrix
	 * @param m2 second matrix
	 * @return m1 - m2
	 */
	public static final RealMatrix subtract(RealMatrix m1, RealMatrix m2) {
		return m1.subtract(m2);
	}

	/**
	 * Multiply matrices
	 * @param m1 first matrix
	 * @param m2 second matrix
	 * @return m1*m2
	 */
	public static final RealMatrix multiply(RealMatrix m1, RealMatrix m2) {
		return m1.multiply(m2);
	}
	
	/**
	 * Multiply matrix by scalar
	 * @param m the matrix
	 * @param x the scalar
	 * @return (x*m_ij)
	 */
	public static final RealMatrix scalarMultiply(RealMatrix m, double x) {
		return m.scalarMultiply(x);
	}
	
	/**
	 * Multiply a matrix by a vector
	 * @param m the matrix
	 * @param v the vector
	 * @return m*v
	 */
	public static final RealVector operate(RealMatrix m, RealVector v) {
		return m.operate(v);
	}
	
	/* This doesn't seem to be implemented in this version of commons
	public static final RealMatrix power(RealMatrix m, int n) {
		return m.power(n)
	}
	*/
	
	/**
	 * Calculate the L_infty norm of a matrix
	 * @param m the matrix
	 * @return the L_infty norm of m
	 */
	public static final double getNorm(RealMatrix m) {
		return m.getNorm();
	}
	
	/**
	 * Calculate the trace of a matrix
	 * @param m the matrix
	 * @return tr(m)
	 */
	public static final double getTrace(RealMatrix m) {
		return m.getTrace();
	}
	
	/**
	 * Return the number of rows in a matrix
	 * @param m the matrix
	 * @return the number of rows in m
	 */
	public static final int getRowDimension(RealMatrix m) {
		return m.getRowDimension();
	}
	
	/**
	 * Return the number of columns in a matrix
	 * @param m the matrix
	 * @return the number of columns in m
	 */
	public static final int getColumnDimension(RealMatrix m) {
		return m.getColumnDimension();
	}
	
	/**
	 * Set the value of a cell in a matrix
	 * @param m the matrix
	 * @param i the row index
	 * @param j the column index
	 * @param val the new value
	 */
	public static final void setEntry(RealMatrix m, int i, int j, double val) {
		m.setEntry(i, j, val);
	}
	
	/**
	 * Get the value of a cell in a matrix
	 * @param m the matrix
	 * @param i the row index
	 * @param j the column index
	 * @return the value of m_ij
	 */
	public static final double getEntry(RealMatrix m, int i, int j) {
		return m.getEntry(i, j);
	}
	
	/**
	 * Increase the value of a cell in a matrix
	 * @param m the matrix
	 * @param i the row index
	 * @param j the column index
	 * @param increment the amount to increase the cell by
	 */
	public static final void addToEntry(RealMatrix m, int i, int j, double increment) {
		m.addToEntry(i, j, increment);
	}
	
	/* Decomposition */
	
	/**
	 * Get the solver from a SingularValueDecomposition
	 * @param dec the decomposition
	 * @return the solver for dec
	 */
	//public static final DecompositionSolver getSolver(SingularValueDecomposition dec) {
	//	return dec.getSolver();
	//}
	
	/* Decomposition Solver */
	
	/**
	 * Solve a matrix with a given solver
	 * @param s the solver
	 * @param m the matrix
	 * @return the solution matrix 
	 */
	public static final RealMatrix solve(DecompositionSolver s, RealMatrix m) {
		return s.solve(m);
	}
	
	/**
	 * Solve a vector with a given solver
	 * @param s the solver
	 * @param v the vector
	 * @return the solution vector 
	 */
	public static final RealVector solve(DecompositionSolver s, RealVector v) {
		return s.solve(v);
	}
}

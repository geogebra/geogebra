package com.vividsolutions.jts.geom.util;

/**
 * Implements some 2D matrix operations 
 * (in particular, solving systems of linear equations).
 * 
 * @author Martin Davis
 *
 */
public class Matrix
{
  private static void swapRows(double[][] m, int i, int j)
  {
    if (i == j) return;
    for (int col = 0; col < m[0].length; col++) {
      double temp = m[i][col];
      m[i][col] = m[j][col];
      m[j][col] = temp;
    }
  }
  
  private static void swapRows(double[] m, int i, int j)
  {
    if (i == j) return;
    double temp = m[i];
    m[i] = m[j];
    m[j] = temp;
  }
  
  /**
   * Solves a system of equations using Gaussian Elimination.
   * In order to avoid overhead the algorithm runs in-place
   * on A - if A should not be modified the client must supply a copy.
   * 
   * @param A an nxn matrix in row/column order )modified by this method)
   * @param b a vector of length n
   * 
   * @return a vector containing the solution (if any)
   * @return null if the system has no or no unique solution
   * 
   * @throws IllegalArgumentException if the matrix is the wrong size 
   */
  public static double[] solve( double[][] a, double[] b )
  {
    int n = b.length;
    if ( a.length != n || a[0].length != n )
      throw new IllegalArgumentException("Matrix A is incorrectly sized");
    
    // Use Gaussian Elimination with partial pivoting.
    // Iterate over each row
    for (int i = 0; i < n; i++ ) {
      // Find the largest pivot in the rows below the current one.
      int maxElementRow = i;
      for (int j = i + 1; j < n; j++ )
        if ( Math.abs( a[j][i] ) > Math.abs( a[maxElementRow][i] ) )
          maxElementRow = j;
        
      if ( a[maxElementRow][i] == 0.0 )
        return null;
      
      // Exchange current row and maxElementRow in A and b.
      swapRows(a, i, maxElementRow );
      swapRows(b, i, maxElementRow );
      
      // Eliminate using row i
      for (int j = i + 1; j < n; j++ ) {
        double rowFactor = a[j][i] / a[i][i];
        for (int k = n - 1; k >= i; k-- )
          a[j][k] -= a[i][k] * rowFactor;
        b[j] -= b[i] * rowFactor;
      }
    }
    
    /**
     * A is now (virtually) in upper-triangular form.
     * The solution vector is determined by back-substitution.
     */
    double[] solution = new double[n];
    for (int j = n - 1; j >= 0; j-- ) {
      double t = 0.0;
      for (int k = j + 1; k < n; k++ )
        t += a[j][k] * solution[k];
      solution[j] = ( b[j] - t ) / a[j][j];
    }
    return solution;
  }
  
}
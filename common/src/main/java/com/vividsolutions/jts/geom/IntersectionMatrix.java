

/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */
package com.vividsolutions.jts.geom;

/**
 * Models a Dimensionally Extended Nine-Intersection Model (DE-9IM) matrix. 
 * This class is used to represent intersection matrices (such as "212FF1FF2")
 * capturing the topological relationship between two {@link Geometry}s. 
 * It can also be represent patterns (such as "T*T******")for matching 
 * existing matrices.
 *
 *  Methods are provided to:
 *  <UL>
 *    <LI> set and query the elements of the matrix in a convenient fashion
 *    <LI> convert to and from the standard string representation (specified in
 *    SFS Section 2.1.13.2).
 *    <LI> test to see if a matrix matches a given pattern string.
 *  </UL>
 *  <P>
 *
 *  For a description of the DE-9IM, see the <A
 *  HREF="http://www.opengis.org/techno/specs.htm">OpenGIS Simple Features
 *  Specification for SQL</A>.
 *  
 * The entries of the matrix are defined by the constants in the {@link Dimension} class.
 * The indices of the matrix represent the topological locations 
 * that occur in a geometry (Interior, Boundary, Exterior).  
 * These are provided as constants in the {@link Location} class.
 *  
 *
 *@version 1.7
 */
public class IntersectionMatrix implements Cloneable {
  /**
   *  Internal representation of this <code>IntersectionMatrix</code>.
   */
  private int[][] matrix;

  /**
   *  Creates an <code>IntersectionMatrix</code> with <code>FALSE</code>
   *  dimension values.
   */
  public IntersectionMatrix() {
    matrix = new int[3][3];
    setAll(Dimension.FALSE);
  }

  /**
   *  Creates an <code>IntersectionMatrix</code> with the given dimension
   *  symbols.
   *
   *@param  elements  a String of nine dimension symbols in row major order
   */
  public IntersectionMatrix(String elements) {
    this();
    set(elements);
  }

  /**
   *  Creates an <code>IntersectionMatrix</code> with the same elements as
   *  <code>other</code>.
   *
   *@param  other  an <code>IntersectionMatrix</code> to copy
   */
  public IntersectionMatrix(IntersectionMatrix other) {
    this();
    matrix[Location.INTERIOR][Location.INTERIOR] = other.matrix[Location.INTERIOR][Location.INTERIOR];
    matrix[Location.INTERIOR][Location.BOUNDARY] = other.matrix[Location.INTERIOR][Location.BOUNDARY];
    matrix[Location.INTERIOR][Location.EXTERIOR] = other.matrix[Location.INTERIOR][Location.EXTERIOR];
    matrix[Location.BOUNDARY][Location.INTERIOR] = other.matrix[Location.BOUNDARY][Location.INTERIOR];
    matrix[Location.BOUNDARY][Location.BOUNDARY] = other.matrix[Location.BOUNDARY][Location.BOUNDARY];
    matrix[Location.BOUNDARY][Location.EXTERIOR] = other.matrix[Location.BOUNDARY][Location.EXTERIOR];
    matrix[Location.EXTERIOR][Location.INTERIOR] = other.matrix[Location.EXTERIOR][Location.INTERIOR];
    matrix[Location.EXTERIOR][Location.BOUNDARY] = other.matrix[Location.EXTERIOR][Location.BOUNDARY];
    matrix[Location.EXTERIOR][Location.EXTERIOR] = other.matrix[Location.EXTERIOR][Location.EXTERIOR];
  }

  /**
   * Adds one matrix to another.
   * Addition is defined by taking the maximum dimension value of each position
   * in the summand matrices.
   *
   * @param im the matrix to add
   */
  public void add(IntersectionMatrix im)
  {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        setAtLeast(i, j, im.get(i, j));
      }
    }
  }

  /**
   *  Returns true if the dimension value satisfies the dimension symbol.
   *
   *@param  actualDimensionValue     a number that can be stored in the <code>IntersectionMatrix</code>
   *      . Possible values are <code>{TRUE, FALSE, DONTCARE, 0, 1, 2}</code>.
   *@param  requiredDimensionSymbol  a character used in the string
   *      representation of an <code>IntersectionMatrix</code>. Possible values
   *      are <code>{T, F, * , 0, 1, 2}</code>.
   *@return                          true if the dimension symbol encompasses
   *      the dimension value
   */
  public static boolean matches(int actualDimensionValue, char requiredDimensionSymbol) {
    if (requiredDimensionSymbol == '*') {
      return true;
    }
    if (requiredDimensionSymbol == 'T' && (actualDimensionValue >= 0 || actualDimensionValue
         == Dimension.TRUE)) {
      return true;
    }
    if (requiredDimensionSymbol == 'F' && actualDimensionValue == Dimension.FALSE) {
      return true;
    }
    if (requiredDimensionSymbol == '0' && actualDimensionValue == Dimension.P) {
      return true;
    }
    if (requiredDimensionSymbol == '1' && actualDimensionValue == Dimension.L) {
      return true;
    }
    if (requiredDimensionSymbol == '2' && actualDimensionValue == Dimension.A) {
      return true;
    }
    return false;
  }

  /**
   *  Returns true if each of the actual dimension symbols satisfies the
   *  corresponding required dimension symbol.
   *
   *@param  actualDimensionSymbols    nine dimension symbols to validate.
   *      Possible values are <code>{T, F, * , 0, 1, 2}</code>.
   *@param  requiredDimensionSymbols  nine dimension symbols to validate
   *      against. Possible values are <code>{T, F, * , 0, 1, 2}</code>.
   *@return                           true if each of the required dimension
   *      symbols encompass the corresponding actual dimension symbol
   */
  public static boolean matches(String actualDimensionSymbols, String requiredDimensionSymbols) {
    IntersectionMatrix m = new IntersectionMatrix(actualDimensionSymbols);
    return m.matches(requiredDimensionSymbols);
  }

  /**
   *  Changes the value of one of this <code>IntersectionMatrix</code>s
   *  elements.
   *
   *@param  row             the row of this <code>IntersectionMatrix</code>,
   *      indicating the interior, boundary or exterior of the first <code>Geometry</code>
   *@param  column          the column of this <code>IntersectionMatrix</code>,
   *      indicating the interior, boundary or exterior of the second <code>Geometry</code>
   *@param  dimensionValue  the new value of the element
   */
  public void set(int row, int column, int dimensionValue) {
    matrix[row][column] = dimensionValue;
  }

  /**
   *  Changes the elements of this <code>IntersectionMatrix</code> to the
   *  dimension symbols in <code>dimensionSymbols</code>.
   *
   *@param  dimensionSymbols  nine dimension symbols to which to set this <code>IntersectionMatrix</code>
   *      s elements. Possible values are <code>{T, F, * , 0, 1, 2}</code>
   */
  public void set(String dimensionSymbols) {
    for (int i = 0; i < dimensionSymbols.length(); i++) {
      int row = i / 3;
      int col = i % 3;
      matrix[row][col] = Dimension.toDimensionValue(dimensionSymbols.charAt(i));
    }
  }

  /**
   *  Changes the specified element to <code>minimumDimensionValue</code> if the
   *  element is less.
   *
   *@param  row                    the row of this <code>IntersectionMatrix</code>
   *      , indicating the interior, boundary or exterior of the first <code>Geometry</code>
   *@param  column                 the column of this <code>IntersectionMatrix</code>
   *      , indicating the interior, boundary or exterior of the second <code>Geometry</code>
   *@param  minimumDimensionValue  the dimension value with which to compare the
   *      element. The order of dimension values from least to greatest is
   *      <code>{DONTCARE, TRUE, FALSE, 0, 1, 2}</code>.
   */
  public void setAtLeast(int row, int column, int minimumDimensionValue) {
    if (matrix[row][column] < minimumDimensionValue) {
      matrix[row][column] = minimumDimensionValue;
    }
  }

  /**
   *  If row >= 0 and column >= 0, changes the specified element to <code>minimumDimensionValue</code>
   *  if the element is less. Does nothing if row <0 or column < 0.
   *
   *@param  row                    the row of this <code>IntersectionMatrix</code>
   *      , indicating the interior, boundary or exterior of the first <code>Geometry</code>
   *@param  column                 the column of this <code>IntersectionMatrix</code>
   *      , indicating the interior, boundary or exterior of the second <code>Geometry</code>
   *@param  minimumDimensionValue  the dimension value with which to compare the
   *      element. The order of dimension values from least to greatest is
   *      <code>{DONTCARE, TRUE, FALSE, 0, 1, 2}</code>.
   */
  public void setAtLeastIfValid(int row, int column, int minimumDimensionValue) {
    if (row >= 0 && column >= 0) {
      setAtLeast(row, column, minimumDimensionValue);
    }
  }

  /**
   *  For each element in this <code>IntersectionMatrix</code>, changes the
   *  element to the corresponding minimum dimension symbol if the element is
   *  less.
   *
   *@param  minimumDimensionSymbols  nine dimension symbols with which to
   *      compare the elements of this <code>IntersectionMatrix</code>. The
   *      order of dimension values from least to greatest is <code>{DONTCARE, TRUE, FALSE, 0, 1, 2}</code>
   *      .
   */
  public void setAtLeast(String minimumDimensionSymbols) {
    for (int i = 0; i < minimumDimensionSymbols.length(); i++) {
      int row = i / 3;
      int col = i % 3;
      setAtLeast(row, col, Dimension.toDimensionValue(minimumDimensionSymbols.charAt(i)));
    }
  }

  /**
   *  Changes the elements of this <code>IntersectionMatrix</code> to <code>dimensionValue</code>
   *  .
   *
   *@param  dimensionValue  the dimension value to which to set this <code>IntersectionMatrix</code>
   *      s elements. Possible values <code>{TRUE, FALSE, DONTCARE, 0, 1, 2}</code>
   *      .
   */
  public void setAll(int dimensionValue) {
    for (int ai = 0; ai < 3; ai++) {
      for (int bi = 0; bi < 3; bi++) {
        matrix[ai][bi] = dimensionValue;
      }
    }
  }

  /**
   *  Returns the value of one of this matrix
   *  entries.
   *  The value of the provided index is one of the 
   *  values from the {@link Location} class.  
   *  The value returned is a constant 
   *  from the {@link Dimension} class.
   *
   *@param  row     the row of this <code>IntersectionMatrix</code>, indicating
   *      the interior, boundary or exterior of the first <code>Geometry</code>
   *@param  column  the column of this <code>IntersectionMatrix</code>,
   *      indicating the interior, boundary or exterior of the second <code>Geometry</code>
   *@return         the dimension value at the given matrix position.
   */
  public int get(int row, int column) {
    return matrix[row][column];
  }

  /**
   *  Returns <code>true</code> if this <code>IntersectionMatrix</code> is
   *  FF*FF****.
   *
   *@return    <code>true</code> if the two <code>Geometry</code>s related by
   *      this <code>IntersectionMatrix</code> are disjoint
   */
  public boolean isDisjoint() {
    return
        matrix[Location.INTERIOR][Location.INTERIOR] == Dimension.FALSE &&
        matrix[Location.INTERIOR][Location.BOUNDARY] == Dimension.FALSE &&
        matrix[Location.BOUNDARY][Location.INTERIOR] == Dimension.FALSE &&
        matrix[Location.BOUNDARY][Location.BOUNDARY] == Dimension.FALSE;
  }

  /**
   *  Returns <code>true</code> if <code>isDisjoint</code> returns false.
   *
   *@return    <code>true</code> if the two <code>Geometry</code>s related by
   *      this <code>IntersectionMatrix</code> intersect
   */
  public boolean isIntersects() {
    return ! isDisjoint();
  }

  /**
   *  Returns <code>true</code> if this <code>IntersectionMatrix</code> is
   *  FT*******, F**T***** or F***T****.
   *
   *@param  dimensionOfGeometryA  the dimension of the first <code>Geometry</code>
   *@param  dimensionOfGeometryB  the dimension of the second <code>Geometry</code>
   *@return                       <code>true</code> if the two <code>Geometry</code>
   *      s related by this <code>IntersectionMatrix</code> touch; Returns false
   *      if both <code>Geometry</code>s are points.
   */
  public boolean isTouches(int dimensionOfGeometryA, int dimensionOfGeometryB) {
    if (dimensionOfGeometryA > dimensionOfGeometryB) {
      //no need to get transpose because pattern matrix is symmetrical
      return isTouches(dimensionOfGeometryB, dimensionOfGeometryA);
    }
    if ((dimensionOfGeometryA == Dimension.A && dimensionOfGeometryB == Dimension.A) ||
        (dimensionOfGeometryA == Dimension.L && dimensionOfGeometryB == Dimension.L) ||
        (dimensionOfGeometryA == Dimension.L && dimensionOfGeometryB == Dimension.A) ||
        (dimensionOfGeometryA == Dimension.P && dimensionOfGeometryB == Dimension.A) ||
        (dimensionOfGeometryA == Dimension.P && dimensionOfGeometryB == Dimension.L)) {
      return matrix[Location.INTERIOR][Location.INTERIOR] == Dimension.FALSE &&
          (matches(matrix[Location.INTERIOR][Location.BOUNDARY], 'T')
           || matches(matrix[Location.BOUNDARY][Location.INTERIOR], 'T')
           || matches(matrix[Location.BOUNDARY][Location.BOUNDARY], 'T'));
    }
    return false;
  }

  /**
   * Tests whether this geometry crosses the
   * specified geometry.
   * <p>
   * The <code>crosses</code> predicate has the following equivalent definitions:
   * <ul>
   * <li>The geometries have some but not all interior points in common.
   * <li>The DE-9IM Intersection Matrix for the two geometries is
   *   <ul>
   *    <li>T*T****** (for P/L, P/A, and L/A situations)
   *    <li>T*****T** (for L/P, L/A, and A/L situations)
   *    <li>0******** (for L/L situations)
   *   </ul>
   * </ul>
   * For any other combination of dimensions this predicate returns <code>false</code>.
   * <p>
   * The SFS defined this predicate only for P/L, P/A, L/L, and L/A situations.
   * JTS extends the definition to apply to L/P, A/P and A/L situations as well.
   * This makes the relation symmetric.
   *
   *@param  dimensionOfGeometryA  the dimension of the first <code>Geometry</code>
   *@param  dimensionOfGeometryB  the dimension of the second <code>Geometry</code>
   *@return                       <code>true</code> if the two <code>Geometry</code>s
   *      related by this <code>IntersectionMatrix</code> cross.
   */
  public boolean isCrosses(int dimensionOfGeometryA, int dimensionOfGeometryB) {
    if ((dimensionOfGeometryA == Dimension.P && dimensionOfGeometryB == Dimension.L) ||
        (dimensionOfGeometryA == Dimension.P && dimensionOfGeometryB == Dimension.A) ||
        (dimensionOfGeometryA == Dimension.L && dimensionOfGeometryB == Dimension.A)) {
      return matches(matrix[Location.INTERIOR][Location.INTERIOR], 'T') &&
          matches(matrix[Location.INTERIOR][Location.EXTERIOR], 'T');
    }
    if ((dimensionOfGeometryA == Dimension.L && dimensionOfGeometryB == Dimension.P) ||
        (dimensionOfGeometryA == Dimension.A && dimensionOfGeometryB == Dimension.P) ||
        (dimensionOfGeometryA == Dimension.A && dimensionOfGeometryB == Dimension.L)) {
      return matches(matrix[Location.INTERIOR][Location.INTERIOR], 'T') &&
          matches(matrix[Location.EXTERIOR][Location.INTERIOR], 'T');
    }
    if (dimensionOfGeometryA == Dimension.L && dimensionOfGeometryB == Dimension.L) {
      return matrix[Location.INTERIOR][Location.INTERIOR] == 0;
    }
    return false;
  }

  /**
   *  Tests whether this <code>IntersectionMatrix</code> is
   *  T*F**F***.
   *
   *@return    <code>true</code> if the first <code>Geometry</code> is within
   *      the second
   */
  public boolean isWithin() {
    return matches(matrix[Location.INTERIOR][Location.INTERIOR], 'T') &&
        matrix[Location.INTERIOR][Location.EXTERIOR] == Dimension.FALSE &&
        matrix[Location.BOUNDARY][Location.EXTERIOR] == Dimension.FALSE;
  }

  /**
   *  Tests whether this <code>IntersectionMatrix</code> is
   *  T*****FF*.
   *
   *@return    <code>true</code> if the first <code>Geometry</code> contains the
   *      second
   */
  public boolean isContains() {
    return matches(matrix[Location.INTERIOR][Location.INTERIOR], 'T') &&
        matrix[Location.EXTERIOR][Location.INTERIOR] == Dimension.FALSE &&
        matrix[Location.EXTERIOR][Location.BOUNDARY] == Dimension.FALSE;
  }

  /**
   *  Returns <code>true</code> if this <code>IntersectionMatrix</code> is
   *    <code>T*****FF*</code>
   * or <code>*T****FF*</code>
   * or <code>***T**FF*</code>
   * or <code>****T*FF*</code>
   *
   *@return    <code>true</code> if the first <code>Geometry</code> covers the
   *      second
   */
  public boolean isCovers() {
    boolean hasPointInCommon =
        matches(matrix[Location.INTERIOR][Location.INTERIOR], 'T')
        || matches(matrix[Location.INTERIOR][Location.BOUNDARY], 'T')
        || matches(matrix[Location.BOUNDARY][Location.INTERIOR], 'T')
        || matches(matrix[Location.BOUNDARY][Location.BOUNDARY], 'T');

    return hasPointInCommon &&
        matrix[Location.EXTERIOR][Location.INTERIOR] == Dimension.FALSE &&
        matrix[Location.EXTERIOR][Location.BOUNDARY] == Dimension.FALSE;
  }

  /**
   *  Returns <code>true</code> if this <code>IntersectionMatrix</code> is
   *    <code>T*F**F***</code>
   * or <code>*TF**F***</code>
   * or <code>**FT*F***</code>
   * or <code>**F*TF***</code>
   *
   *@return    <code>true</code> if the first <code>Geometry</code>
   * is covered by the second
   */
  public boolean isCoveredBy() {
    boolean hasPointInCommon =
        matches(matrix[Location.INTERIOR][Location.INTERIOR], 'T')
        || matches(matrix[Location.INTERIOR][Location.BOUNDARY], 'T')
        || matches(matrix[Location.BOUNDARY][Location.INTERIOR], 'T')
        || matches(matrix[Location.BOUNDARY][Location.BOUNDARY], 'T');

    return hasPointInCommon &&
        matrix[Location.INTERIOR][Location.EXTERIOR] == Dimension.FALSE &&
        matrix[Location.BOUNDARY][Location.EXTERIOR] == Dimension.FALSE;
  }

  /**
   *  Returns <code>true</code> if this <code>IntersectionMatrix</code> is
   *  T*F**FFF*.
   *
   *@param  dimensionOfGeometryA  the dimension of the first <code>Geometry</code>
   *@param  dimensionOfGeometryB  the dimension of the second <code>Geometry</code>
   *@return                       <code>true</code> if the two <code>Geometry</code>
   *      s related by this <code>IntersectionMatrix</code> are equal; the
   *      <code>Geometry</code>s must have the same dimension for this function
   *      to return <code>true</code>
   */
  public boolean isEquals(int dimensionOfGeometryA, int dimensionOfGeometryB) {
    if (dimensionOfGeometryA != dimensionOfGeometryB) {
      return false;
    }
    return matches(matrix[Location.INTERIOR][Location.INTERIOR], 'T') &&
        matrix[Location.EXTERIOR][Location.INTERIOR] == Dimension.FALSE &&
        matrix[Location.INTERIOR][Location.EXTERIOR] == Dimension.FALSE &&
        matrix[Location.EXTERIOR][Location.BOUNDARY] == Dimension.FALSE &&
        matrix[Location.BOUNDARY][Location.EXTERIOR] == Dimension.FALSE;
  }

  /**
   *  Returns <code>true</code> if this <code>IntersectionMatrix</code> is
   *  <UL>
   *    <LI> T*T***T** (for two points or two surfaces)
   *    <LI> 1*T***T** (for two curves)
   *  </UL>.
   *
   *@param  dimensionOfGeometryA  the dimension of the first <code>Geometry</code>
   *@param  dimensionOfGeometryB  the dimension of the second <code>Geometry</code>
   *@return                       <code>true</code> if the two <code>Geometry</code>s
   *      related by this <code>IntersectionMatrix</code> overlap. For this
   *      function to return <code>true</code>, the <code>Geometry</code>s must
   *      be two points, two curves or two surfaces.
   */
  public boolean isOverlaps(int dimensionOfGeometryA, int dimensionOfGeometryB) {
    if ((dimensionOfGeometryA == Dimension.P && dimensionOfGeometryB == Dimension.P) ||
        (dimensionOfGeometryA == Dimension.A && dimensionOfGeometryB == Dimension.A)) {
      return matches(matrix[Location.INTERIOR][Location.INTERIOR], 'T') &&
          matches(matrix[Location.INTERIOR][Location.EXTERIOR], 'T') && matches(matrix[Location.EXTERIOR][Location.INTERIOR],
          'T');
    }
    if (dimensionOfGeometryA == Dimension.L && dimensionOfGeometryB == Dimension.L) {
      return matrix[Location.INTERIOR][Location.INTERIOR] == 1 &&
          matches(matrix[Location.INTERIOR][Location.EXTERIOR], 'T') &&
          matches(matrix[Location.EXTERIOR][Location.INTERIOR], 'T');
    }
    return false;
  }

  /**
   *  Returns whether the elements of this <code>IntersectionMatrix</code>
   *  satisfies the required dimension symbols.
   *
   *@param  requiredDimensionSymbols  nine dimension symbols with which to
   *      compare the elements of this <code>IntersectionMatrix</code>. Possible
   *      values are <code>{T, F, * , 0, 1, 2}</code>.
   *@return                           <code>true</code> if this <code>IntersectionMatrix</code>
   *      matches the required dimension symbols
   */
  public boolean matches(String requiredDimensionSymbols) {
    if (requiredDimensionSymbols.length() != 9) {
      throw new IllegalArgumentException("Should be length 9: " + requiredDimensionSymbols);
    }
    for (int ai = 0; ai < 3; ai++) {
      for (int bi = 0; bi < 3; bi++) {
        if (!matches(matrix[ai][bi], requiredDimensionSymbols.charAt(3 * ai +
            bi))) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   *  Transposes this IntersectionMatrix.
   *
   *@return    this <code>IntersectionMatrix</code> as a convenience
   */
  public IntersectionMatrix transpose() {
    int temp = matrix[1][0];
    matrix[1][0] = matrix[0][1];
    matrix[0][1] = temp;
    temp = matrix[2][0];
    matrix[2][0] = matrix[0][2];
    matrix[0][2] = temp;
    temp = matrix[2][1];
    matrix[2][1] = matrix[1][2];
    matrix[1][2] = temp;
    return this;
  }

  /**
   *  Returns a nine-character <code>String</code> representation of this <code>IntersectionMatrix</code>
   *  .
   *
   *@return    the nine dimension symbols of this <code>IntersectionMatrix</code>
   *      in row-major order.
   */
  public String toString() {
    StringBuffer buf = new StringBuffer("123456789");
    for (int ai = 0; ai < 3; ai++) {
      for (int bi = 0; bi < 3; bi++) {
        buf.setCharAt(3 * ai + bi, Dimension.toDimensionSymbol(matrix[ai][bi]));
      }
    }
    return buf.toString();
  }
}


package geogebra.common.util;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;

/**
 * Matrix format allowing conversion from/to MyList and GeoList,
 * supporting matrix operations (inverse, determinant etc.)
 * 
 * @author Michael Borcherds
 *
 */
public class GgbMat extends Array2DRowRealMatrix{

	private static final long serialVersionUID = 1L;
	
	private boolean isUndefined = false;

	/**
	 * Creates matrix from GeoList
	 * @param inputList list
	 */
	public GgbMat(GeoList inputList) {
		int rows = inputList.size();
		if (!inputList.isDefined() || rows == 0) {
			setIsUndefined(true);
			return;
		}

		GeoElement geo = inputList.get(0);

		if (!geo.isGeoList()) {
			setIsUndefined(true);
			return;
		}

		int cols = ((GeoList) geo).size();

		if (cols == 0) {
			setIsUndefined(true);
			return;
		}

		data = new double[rows][cols];
		// m = rows;
		// n = cols;

		GeoList rowList;

		for (int r = 0; r < rows; r++) {
			geo = inputList.get(r);
			if (!geo.isGeoList()) {
				setIsUndefined(true);
				return;
			}
			rowList = (GeoList) geo;
			if (rowList.size() != cols) {
				setIsUndefined(true);
				return;
			}
			for (int c = 0; c < cols; c++) {
				geo = rowList.get(c);
				if (!geo.isGeoNumeric()) {
					setIsUndefined(true);
					return;
				}

				setEntry(r, c, ((GeoNumeric) geo).getValue());
			}
		}
	}

	/**
	 * Creates matrix from MyList
	 * @param inputList list
	 */
	public GgbMat(MyList inputList) {

		if (!inputList.isMatrix()) {
			setIsUndefined(true);
			return;
		}
		int rows = inputList.getMatrixRows();
		int cols = inputList.getMatrixCols();
		if (rows < 1 || cols < 1) {
			setIsUndefined(true);
			return;
		}

		data = new double[rows][cols];

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				ExpressionValue geo = MyList.getCell(inputList, c, r);
				if (!geo.isNumberValue()) {
					setIsUndefined(true);
					return;
				}
				setEntry(r, c, ((NumberValue) geo).getDouble());
			}
		}
	}

	/**
	 * Inverts this matrix. If singular, sets the undefined flag to true.
	 */
	public void inverseImmediate() {

		try {
			DecompositionSolver d = new LUDecompositionImpl(this,Kernel.STANDARD_PRECISION).getSolver();
			RealMatrix ret = d.getInverse();
			data = ret.getData();
			// m = ret.m;
			// n = ret.n;
		} catch (Exception e) { // can't invert
			setIsUndefined(true);
		}
	}
	
	/**
	 * Returns determinant of this matrix
	 * @return determinat
	 */
	public double determinant(){
		return new LUDecompositionImpl(this, Kernel.STANDARD_PRECISION).getDeterminant();
	}

	/**
	 * Computes the reduced row echelon form.
	 * 
	 * code from http://rosettacode.org/wiki/Reduced_row_echelon_form
	 */
	public void reducedRowEchelonFormImmediate() {
		int rowCount = data.length;
		if (rowCount == 0)
			return;

		int columnCount = data[0].length;

		int lead = 0;
		for (int r = 0; r < rowCount; r++) {
			if (lead >= columnCount)
				break;
			{
				int i = r;
				//make sure we don't use a leader which is almost zero
				// http://www.geogebra.org/forum/viewtopic.php?f=1&t=25684
				while (Kernel.isZero(data[i][lead])) {
					data[i][lead] = 0;
					i++;
					if (i == rowCount) {
						i = r;
						lead++;
						if (lead == columnCount)
							return;
					}
				}
				double[] temp = data[r];
				data[r] = data[i];
				data[i] = temp;
			}

			{
				double lv = data[r][lead];
				for (int j = 0; j < columnCount; j++)
					data[r][j] /= lv;
			}

			for (int i = 0; i < rowCount; i++) {
				if (i != r) {
					double lv = data[i][lead];
					for (int j = 0; j < columnCount; j++)
						data[i][j] -= lv * data[r][j];
				}
			}
			lead++;
		}
	}
	
	/**
	 * Transposes this matrix
	 */
	public void transposeImmediate() {

		int m = getRowDimension();
		int n = getColumnDimension();

		double[][] C = new double[n][m];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[j][i] = data[i][j];
			}
		}
		data = C;
	}

	/**
	 * returns GgbMatrix as a GeoList eg { {1,2}, {3,4} }
	 * @param outputList list for the copy
	 * @param cons construction
	 */
	public void getGeoList(GeoList outputList, Construction cons) {

		if (isUndefined) {
			outputList.setDefined(false);	
			return;
		}

		outputList.clear();
		outputList.setDefined(true);

		for (int r = 0; r < getRowDimension(); r++) {
			GeoList columnList = new GeoList(cons);
			for (int c = 0; c < getColumnDimension(); c++) {
				// Application.debug(get(r, c)+"");
				columnList.add(new GeoNumeric(cons, getEntry(r, c)));
			}
			outputList.add(columnList);
		}
	}
	
	/**
	 * returns GgbMatrix as a MyList eg { {1,2}, {3,4} }
	 * @param outputList list for the copy
	 * @param kernel kernel
	 */
	public void getMyList(MyList outputList,Kernel kernel) {
		if (isUndefined) {
			return;			
		}

		outputList.clear();

		for (int r = 0; r < getRowDimension(); r++) {
			MyList columnList = new MyList(kernel);
			for (int c = 0; c < getColumnDimension(); c++) {
				// Application.debug(get(r, c)+"");
				columnList.addListElement(new GeoNumeric(kernel.getConstruction(), getEntry(r, c)));
			}
			outputList.addListElement(columnList);
		}
	}


	/**
	 * @return true if the matrix is undefined eg after being inverted
	 */
	public boolean isUndefined() {
		return isUndefined;
	}

	/**
	 * Sets the undefined flag to false (e.g. when inverting singular matrix)
	 * @param undefined new undefined flag
	 */
	public void setIsUndefined(boolean undefined) {
		isUndefined = undefined;
	}

	/**
	 * True for matrix formed by integers
	 * @return true if all entries are integers
	 */
	public boolean hasOnlyIntegers() {
		for(int i=0;i<data.length;i++)
			for(int j=0;j<data[i].length;j++)
				if(!Kernel.isInteger(data[i][j]))
					return false;
		return true;
	}

}

package geogebra.util;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyList;
import geogebra.kernel.arithmetic.NumberValue;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;


public class GgbMat extends Array2DRowRealMatrix {

	private boolean isUndefined = false;

	public GgbMat (GeoList inputList) {
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


		int cols = ((GeoList)geo).size();

		if (cols == 0) {
			setIsUndefined(true);
			return;   		
		}

		data = new double[rows][cols];
		//m = rows;
		//n = cols;
		
		GeoList rowList;

		for (int r = 0 ; r < rows ; r++) {
			geo = inputList.get(r);
			if (!geo.isGeoList()) {
				setIsUndefined(true);
				return;   		
			}
			rowList = (GeoList)geo;
			if (rowList.size() != cols) {
				setIsUndefined(true);
				return;   		
			}
			for (int c = 0 ; c < cols ; c++) {
				geo = rowList.get(c);
				if (!geo.isGeoNumeric()) {
					setIsUndefined(true);
					return;   		
				}

				setEntry(r, c, ((GeoNumeric)geo).getValue());
			}
		}
	}

	public GgbMat (MyList inputList) {
		
		
		if ( !inputList.isMatrix()) {
			setIsUndefined(true);
			return;
		} 
		int rows = inputList.getMatrixRows();
		int cols = inputList.getMatrixCols();
		if(rows < 1 || cols <1){
			setIsUndefined(true);
			return;
		} 

		data = new double[rows][cols];
				
		for (int r = 0 ; r < rows ; r++) {
			for (int c = 0 ; c < cols ; c++) {
				ExpressionValue geo = MyList.getCell(inputList, c, r);
				if (!geo.isNumberValue()) {
					setIsUndefined(true);
					return;   		
				}
				setEntry(r, c, ((NumberValue)geo).getDouble());
			}
		}
	}
	
	public void inverseImmediate() {

		try {
			RealMatrix ret = inverse();
			data = ret.getData();
			luDecompose();
			//m = ret.m;
			//n = ret.n;
		}
		catch (Exception e) { // can't invert
			setIsUndefined(true);
		}

	}

	/*
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
            while (data[i][lead] == 0) {
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
		
		if (m == n) luDecompose();
	}

	/*
	 * returns GgbMatrix as a GeoList eg { {1,2}, {3,4} }
	 */
	public GeoList getGeoList(GeoList outputList, Construction cons) {

		if (isUndefined) {
			outputList.setDefined(false);
			return outputList;
		}

		outputList.clear();
		outputList.setDefined(true);

		for (int r = 0 ; r < getRowDimension() ; r++) {  	   			
			GeoList columnList = new GeoList(cons);
			for (int c = 0 ; c < getColumnDimension() ; c++) {
				//Application.debug(get(r, c)+"");
				columnList.add(new GeoNumeric(cons, getEntry(r, c)));  	   			
			}
			outputList.add(columnList);
		}

		return outputList;

	}

	/*
	 * returns true if the matrix is undefined
	 * eg after being inverted 
	 */
	public boolean isUndefined() {
		return isUndefined;
	}
	public void setIsUndefined(boolean undefined) {
		isUndefined = undefined;
	}



}

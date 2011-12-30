package geogebra3D;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.util.TraceSettings;
import geogebra.gui.view.spreadsheet.RelativeCopy;
import geogebra.gui.view.spreadsheet.SpreadsheetTraceManager;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.kernel.geos.GeoElementSpreadsheet;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoAngle3D;
import geogebra3D.kernel3D.GeoPoint3D;

import java.util.ArrayList;

public class SpreadsheetTraceManager3D extends SpreadsheetTraceManager {

	public SpreadsheetTraceManager3D(SpreadsheetView view) {
		super(view);
	}
	
	@Override
	protected boolean addElementTrace(GeoElement geo, Construction cons, ArrayList<Double> currentTrace) {
		
		// check 2D
		if (super.addElementTrace(geo, cons, currentTrace)) return true;
		
		// check 3D
		switch (geo.getGeoClassType()) {

		case POINT3D:
			GeoPoint3D P = (GeoPoint3D) geo;
			double[] coords = new double[3];
			P.getInhomCoords(coords);
			currentTrace.add(coords[0]);
			currentTrace.add(coords[1]);
			currentTrace.add(coords[2]);
			return true;
			
		case ANGLE3D:
			GeoAngle3D angle = (GeoAngle3D) geo;			
			currentTrace.add(angle.getValue());
			return true;


	
	}
		
		return false;
	}
		
	@Override
	protected boolean setGeoTraceRow(GeoElement geo, Construction cons, ArrayList<Double> traceArray,  int row) {

		TraceSettings t = traceGeoCollection.get(geo);
		int column = t.traceColumn1;
		int traceIndex = 0;
		GeoElement[] geos = getElementList(geo);
		
		if(t.doTraceGeoCopy){
			setTraceCellAsGeoCopy(cons,geo,t.traceColumn1,row);
			return true;
		}
		
		// handle null trace (when shifting cells a null trace is sometimes needed)	
		if(traceArray == null){
			traceArray = new ArrayList<Double>();
			traceArray.add(Double.NaN);
			traceArray.add(Double.NaN);
		}
		
		// trace 
		for (int i = 0; i < geos.length; i++) {		

			switch (geos[i].getGeoClassType()) {

			case POINT: 

				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.NUMERIC);
				++column;
				++traceIndex;

				if (((GeoPoint2) geos[i]).getMode() == AbstractKernel.COORD_POLAR)
					setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.ANGLE);
				else
					setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.NUMERIC);
				++column;
				++traceIndex;

				return true;


			case VECTOR: 
				
				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.NUMERIC);
				++column;
				++traceIndex;
				
				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.NUMERIC);
				++column;
				++traceIndex;
				return true;


			case NUMERIC:
				
				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.NUMERIC);
				++column;
				++traceIndex;
				return true;


			case ANGLE: 
				
				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.ANGLE);
				++column;
				++traceIndex;
				return true;

			case POINT3D: 

				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.NUMERIC);
				++column;
				++traceIndex;

				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.NUMERIC);
				++column;
				++traceIndex;

				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.NUMERIC);
				++column;
				++traceIndex;

				return true;


			case VECTOR3D: 
				
				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.NUMERIC);
				++column;
				++traceIndex;
				
				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.NUMERIC);
				++column;
				++traceIndex;
				
				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.NUMERIC);
				++column;
				++traceIndex;
				return true;

			case ANGLE3D: 
				
				setTraceCell(cons, column, row, traceArray.get(traceIndex), GeoClass.ANGLE);
				++column;
				++traceIndex;
				return true;
				
				default:
					Application.debug(geos[i].getClassName());

			}
		}					
		
		return false;
	}


}

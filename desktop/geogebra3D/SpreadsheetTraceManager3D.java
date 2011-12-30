package geogebra3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.gui.view.spreadsheet.SpreadsheetTraceManager;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
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
			Application.debug("TODO: z coord");
			//currentTrace.add(coords[2]);
			return true;
			
		case ANGLE3D:
			GeoAngle3D angle = (GeoAngle3D) geo;			
			currentTrace.add(angle.getValue());
			return true;


	
	}
		
		return false;
	}
		


}

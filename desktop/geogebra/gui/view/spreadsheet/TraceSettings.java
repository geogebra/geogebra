package geogebra.gui.view.spreadsheet;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Spreadsheet trace settings.
 * @author G.Sturr 2010-5-20
 */
public class TraceSettings {
	
	public int traceColumn1 = -1;
	public int traceColumn2 = -1;
	public int traceRow1 = -1;
	public int traceRow2 = -1;
	public int tracingRow = 0;
	public int numRows = 10;
	public int headerOffset = 1;  // show label is default
	
	public ArrayList<Double> lastTrace = new ArrayList<Double>();
		
	public boolean doColumnReset = false ;
	public boolean needsColumnReset = false;
	public boolean doRowLimit = false;	
	public boolean showLabel = true;
	public boolean showTraceList = false;
	
	public boolean doTraceGeoCopy = false;
	
	public void debug(GeoElement geo){
		System.out.println("=====================================");
		System.out.println(geo.toString());
		Field[] fields = TraceSettings.class.getDeclaredFields();
			for (Field field : fields) {
				try {
					System.out.println(field.getName() + ": " + field.get(this).toString());
				} catch (Exception e) {
					Application.debug(e.getMessage());
				}
			}
		System.out.println("=====================================");	
	}
	
	public TraceSettings clone(TraceSettings t){
		return this;
	}
	
	
}
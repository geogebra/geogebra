package geogebra.common.cas.view;

import geogebra.common.main.App;

public class RowHeaderPopupMenu {

	protected App app;

	public String getDeleteString(int [] selRows) {
		String strRows;
		if (selRows.length == 1) {
			strRows = app.getLocalization().getPlain("DeleteRowA", Integer.toString(selRows[0]+1));			
		} else {
			strRows = app.getLocalization().getPlain("DeleteRowsAtoB", 
						Integer.toString(selRows[0]+1), 
						Integer.toString(selRows[selRows.length-1]+1));
		}
		return strRows;
	}

}

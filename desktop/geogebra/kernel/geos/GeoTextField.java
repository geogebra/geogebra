package geogebra.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.AbstractGeoTextField;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.util.StringUtil;
import geogebra.gui.inputfield.AutoCompleteTextField;


import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class GeoTextField extends AbstractGeoTextField {

	
	
	private static int defaultLength = 20;
	
	AutoCompleteTextField textField;
	
	public GeoTextField(Construction c) {
		
		super(c);
		
		textField = new AutoCompleteTextField(defaultLength, c.getApplication());
		textField.showPopupSymbolButton(true);
		textField.setAutoComplete(false);
		textField.enableColoring(false);
	}
	public GeoTextField(Construction cons, int labelOffsetX, int labelOffsetY) {
		this(cons);
		this.labelOffsetX = labelOffsetX;
		this.labelOffsetY = labelOffsetY;
	}

	
	
	@Override
	protected void getXMLtags(StringBuilder sb) {

		super.getXMLtags(sb);
		if (linkedGeo != null) {
   	
			sb.append("\t<linkedGeo exp=\"");
			sb.append(StringUtil.encodeXML(linkedGeo.getLabel()));
			sb.append("\"");			    		    	
			sb.append("/>\n");
		}
		
		if (getLength() != defaultLength) {
			sb.append("\t<length val=\"");
			sb.append(getLength());
			sb.append("\"");			    		    	
			sb.append("/>\n");			
		}

	}
	public JTextField getTextField() {
		return textField;
	}
	
	@Override
	public void setLength(int l) {
		textField.setColumns(l);
		// don't show the popup button in small fields
		textField.showPopupSymbolButton(l > 8);
	}
	
	public int getLength() {
		return textField.getColumns();
	}
	
	public void setFocus(final String str) {
		textField.requestFocus();
		if (str != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					textField.setText(str);
				}
			});
		}
	}
	
	@Override
	public GeoElement copy() {
		return new GeoTextField(cons, labelOffsetX, labelOffsetY);
	}

}

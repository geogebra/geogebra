package geogebra.kernel;

import geogebra.gui.inputfield.AutoCompleteTextField;
import geogebra.util.Util;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class GeoTextField extends GeoButton {

	private GeoElement linkedGeo = null;
	
	private static int defaultLength = 20;
	
	AutoCompleteTextField textField;
	
	public GeoTextField(Construction c) {
		
		super(c);
		
		textField = new AutoCompleteTextField(defaultLength, c.getApplication());
		textField.showPopupSymbolButton(true);
		textField.setAutoComplete(false);
	}
	public GeoTextField(Construction cons, int labelOffsetX, int labelOffsetY) {
		this(cons);
		this.labelOffsetX = labelOffsetX;
		this.labelOffsetY = labelOffsetY;
	}

	public String getClassName() {
		return "GeoTextField";
	}
	public boolean isChangeable(){
		return true;
	}
	
    protected String getTypeString() {
		return "TextField";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_TEXTFIELD;
    }
    
	public boolean isTextField() {
		return true;
	}
	
	public void setLinkedGeo(GeoElement geo) {
		linkedGeo = geo;
		text = geo.getValueForInputBar();
	}
	
	public GeoElement getLinkedGeo() {
		return linkedGeo;
	}
	
	protected void getXMLtags(StringBuilder sb) {

		super.getXMLtags(sb);
		if (linkedGeo != null) {
   	
			sb.append("\t<linkedGeo exp=\"");
			sb.append(Util.encodeXML(linkedGeo.getLabel()));
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
            SwingUtilities.invokeLater( new Runnable(){ public void
            	run() { textField.setText(str);} });
			
		}
	}
	
	private String text = null;
	public String toValueString() {
		if (linkedGeo == null) return "";
		return text;
	}
	public void setText(String text2) {
		text = text2;		
	}
	
	public GeoElement copy() {
		return new GeoTextField(cons, labelOffsetX, labelOffsetY);
	}
	
	

}

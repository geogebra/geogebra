package geogebra.common.kernel.geos;

import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.StringUtil;
import geogebra.common.util.TextObject;
import geogebra.common.util.Unicode;

public class GeoTextField extends GeoButton {
	private static int defaultLength = 20;
	private int length;
	public GeoTextField(Construction c) {
		super(c);
		length = defaultLength;
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
	
	public String getTypeString() {
		return "TextField";
	}
    
    public GeoClass getGeoClassType() {
    	return GeoClass.TEXTFIELD;
    }
    
	public boolean isTextField() {
		return true;
	}
	
	public void setLinkedGeo(GeoElement geo) {
		linkedGeo = geo;
		text = geo.getValueForInputBar();
		
		// remove quotes from start and end
		if (text.length() > 0 && text.charAt(0) == '"') {
			text = text.substring(1);
		}		
		if (text.length() > 0 && text.charAt(text.length() - 1) == '"') {
			text = text.substring(0, text.length() - 1);
		}
	}
	
	public GeoElement getLinkedGeo() {
		return linkedGeo;
	}

	protected GeoElement linkedGeo = null;
	

	protected String text = null;
	public String toValueString(StringTemplate tpl) {
		if (linkedGeo == null) return "";
		return text;
	}
	public void setText(String text2) {
		text = text2;		
	}
	
	public boolean isGeoTextField(){
		return true;
	}

	public void setLength(int l){
		length = l;
		this.updateVisualStyle();
	}

	public int getLength() {
		return length;
	}

	protected void getXMLtags(StringBuilder sb) {

		super.getXMLtags(sb);
		if (linkedGeo != null) {
   	
			sb.append("\t<linkedGeo exp=\"");
			sb.append(StringUtil.encodeXML(linkedGeo.getLabel(StringTemplate.xmlTemplate)));
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
	@Override
	public GeoElement copy() {
		return new GeoTextField(cons, labelOffsetX, labelOffsetY);
	}
	/**
	 * @param inputText new value for linkedGeo
	 */
	public void updateLinkedGeo(String inputText) {
		String defineText = inputText;
		StringTemplate tpl = StringTemplate.defaultTemplate;
		if (linkedGeo.isGeoLine()) {

			// not y=
			// and not Line[A,B]
			if ((defineText.indexOf('=') == -1)
					&& (defineText.indexOf('[') == -1)) {
				// x + 1 changed to
				// y = x + 1
				defineText = "y=" + defineText;
			}

			String prefix = linkedGeo.getLabel(tpl) + ":";
			// need a: in front of
			// X = (-0.69, 0) + \lambda (1, -2)
			if (!defineText.startsWith(prefix)) {
				defineText = prefix + defineText;
			}
		} else if (linkedGeo.isGeoText()) {
			defineText = "\"" + defineText + "\"";
		} else if (linkedGeo.isGeoPoint()) {
			if (((GeoPoint2) linkedGeo).toStringMode == Kernel.COORD_COMPLEX) {
				// z=2 doesn't work for complex numbers (parses to
				// GeoNumeric)
				defineText = defineText + "+0" + Unicode.IMAGINARY;
			}
		} else if (linkedGeo instanceof FunctionalNVar) {
			// string like f(x,y)=x^2
			// or f(\theta) = \theta
			defineText = linkedGeo.getLabel(tpl) + "("
					+ ((FunctionalNVar) linkedGeo).getVarString(tpl)
					+ ")=" + defineText;
		}

		try {
			linkedGeo = kernel
					.getAlgebraProcessor()
					.changeGeoElementNoExceptionHandling(linkedGeo,
							defineText, false, true);
		} catch (Exception e1) {
			app.showError(e1.getMessage());
			return;
		}
		this.setLinkedGeo(linkedGeo);

		
	}
	
	public void updateText(TextObject textField){
		
		if (linkedGeo != null) {

			String text;

			if (linkedGeo.isGeoText()) {
				text = ((GeoText) linkedGeo).getTextString();
			} else {

				// want just a number for eg a=3 but we want variables for eg
				// y=m x + c
				boolean substituteNos = linkedGeo.isGeoNumeric()
						&& linkedGeo.isIndependent();
				text = linkedGeo.getFormulaString(StringTemplate.defaultTemplate,
						substituteNos);
			}

			if (linkedGeo.isGeoText() && (text.indexOf("\n") > -1)) {
				// replace linefeed with \\n
				while (text.indexOf("\n") > -1) {
					text = text.replaceAll("\n", "\\\\\\\\n");
				}
			}
			if (!textField.getText().equals(text)) { // avoid redraw error
				textField.setText(text);
			}

		}

		setText(textField.getText());
	
	}
	public void textObjectUpdated(TextObject textField) {
		if (linkedGeo != null) {
			updateLinkedGeo(textField.getText());
			updateText(textField);
		}
		runScripts(textField.getText());
		
	}
	


}

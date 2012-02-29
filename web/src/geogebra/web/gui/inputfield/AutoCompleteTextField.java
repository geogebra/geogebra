package geogebra.web.gui.inputfield;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.awt.event.FocusListener;
import geogebra.common.euclidian.Drawable;
import geogebra.common.javax.swing.JLabel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;

import com.google.gwt.user.client.ui.TextBox;

public class AutoCompleteTextField extends TextBox implements geogebra.common.gui.inputfield.AutoCompleteTextField{

	public AutoCompleteTextField(int length, AbstractApplication application,
            Drawable drawTextField) {
	    super();
	    setVisibleLength(length);
    }

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	
//	public String getText() {
//	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
//	    return null;
//    }
//
//	public void setText(String s) {
//	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
//	    
//    }

	public void showPopupSymbolButton(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setAutoComplete(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void enableColoring(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setOpaque(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setFont(Font font) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setForeground(Color color) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setBackground(Color color) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setFocusable(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setEditable(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void requestFocus() {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setLabel(JLabel label) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setVisible(boolean b) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setColumns(int length) {
	    AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void addFocusListener(FocusListener focusListener) {
		AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void addKeyListener(geogebra.common.awt.event.KeyListener listener) {
		super.addKeyPressHandler((geogebra.web.awt.event.KeyListener) listener);
	}
	
	public void wrapSetText(String s) {
		AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

}

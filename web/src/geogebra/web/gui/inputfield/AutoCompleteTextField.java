package geogebra.web.gui.inputfield;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.euclidian.DrawTextField;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.event.FocusListener;
import geogebra.common.javax.swing.JLabel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;

import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.TextBox;

public class AutoCompleteTextField extends TextBox implements geogebra.common.gui.inputfield.AutoCompleteTextField{
	
	public AutoCompleteTextField(int length, AbstractApplication application,
            Drawable drawTextField) {
	    super();
	    setVisibleLength(length);
	    init();
    }
	
	private void init(){
		addMouseUpHandler(new MouseUpHandler(){
			public void onMouseUp(MouseUpEvent event) {
				AutoCompleteTextField tf = ((AutoCompleteTextField)event.getSource()); 
	            tf.setFocus(true);
            }
		});
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
	    AbstractApplication.debug("implementation needed - just finishing"); //TODO Auto-generated
		setFocus(true);
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

	public void addFocusListener(FocusListener listener) {
		//super.addFocusListener((geogebra.web.euclidian.event.FocusListener) listener);
		super.addFocusHandler((geogebra.web.euclidian.event.FocusListener) listener);
		super.addBlurHandler((geogebra.web.euclidian.event.FocusListener) listener);
		
	    
    }

	public void addKeyListener(geogebra.common.euclidian.event.KeyListener listener) {
		super.addKeyPressHandler((geogebra.web.euclidian.event.KeyListener) listener);
	}
	
	public void wrapSetText(String s) {
		AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public int getCaretPosition() {
		AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    return 0;
    }

	public void setCaretPosition(int caretPos) {
		AbstractApplication.debug("implementation needed"); //TODO Auto-generated
	    
    }

}

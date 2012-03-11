package geogebra.web.javax.swing;

import geogebra.common.awt.Dimension;
import geogebra.common.awt.Rectangle;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.javax.swing.JLabel;
import geogebra.common.main.AbstractApplication;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class Box extends geogebra.common.javax.swing.Box{

	private HorizontalPanel impl;
	
	public Box(){
		impl = new HorizontalPanel();
		impl.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	}
	
	public static HorizontalPanel getImpl(Box box){
		if (box == null) return null;
		return box.impl;
	}
	
	@Override
    public void add(JLabel label) {
	    impl.add(geogebra.web.javax.swing.JLabel.getImpl(
	    		(geogebra.web.javax.swing.JLabel)label));
	    
    }

	@Override
    public void add(AutoCompleteTextField textField) {
	    impl.add((geogebra.web.gui.inputfield.AutoCompleteTextField)textField);
	    
    }

	@Override
    public void setVisible(boolean isVisible) {
	    impl.setVisible(isVisible);
    }

	@Override
    public void setBounds(Rectangle rect) {
	    AbstractApplication.debug("implementation needed - just finishing"); // TODO
	    impl.setWidth(rect.getWidth() + "");
	    impl.setHeight(rect.getHeight() + "");
	    
	    if (impl.getParent() instanceof AbsolutePanel){
	    	((AbsolutePanel)(impl.getParent())).
	    		setWidgetPosition(impl, (int)rect.getMinX(), (int)rect.getMinY());
	    }
	    
    }

	@Override
    public Dimension getPreferredSize() {
	    return new geogebra.web.awt.Dimension(impl.getOffsetWidth(), impl.getOffsetHeight());
    }

	@Override
    public Rectangle getBounds() {
	    AbstractApplication.debug("implementation needed - just finishing"); // TODO
	    return new geogebra.web.awt.Rectangle(impl.getAbsoluteLeft(), impl.getAbsoluteTop(),
	    		impl.getOffsetWidth(), impl.getOffsetHeight());
    }

}

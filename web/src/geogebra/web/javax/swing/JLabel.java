package geogebra.web.javax.swing;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.main.AbstractApplication;

public class JLabel extends geogebra.common.javax.swing.JLabel {

	private com.google.gwt.user.client.ui.Label impl;
	
	JLabel(){
		impl = new com.google.gwt.user.client.ui.Label();
	}
	
	public JLabel(String string) {
		impl = new com.google.gwt.user.client.ui.Label(string);
    }

	public static com.google.gwt.user.client.ui.Label getImpl(JLabel label){
		if (label==null) return null;
		return label.impl;
	}
	
	@Override
    public void setVisible(boolean b) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    impl.setVisible(b);
    }

	@Override
    public void setText(String text) {
	    impl.setText(text);
	    
    }

	@Override
    public void setOpaque(boolean b) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public void setFont(Font font) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public void setForeground(Color color) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public void setBackground(Color color) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

}

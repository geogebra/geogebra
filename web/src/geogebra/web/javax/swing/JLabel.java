package geogebra.web.javax.swing;

import com.google.gwt.user.client.DOM;

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
	    impl.setVisible(b);
    }

	@Override
    public void setText(String text) {
	    impl.setText(text);
	    
    }

	@Override
    public void setOpaque(boolean b) {
	    AbstractApplication.debug("implementation needed - just finishing"); // TODO Auto-generated


//      The next rows set not only the background's opacity, but the text's opacity as well: 
//	    if (b==true){
//	    	DOM.setStyleAttribute(impl.getElement(), "opacity", "1.0");
//	    	DOM.setStyleAttribute(impl.getElement(), "filter", "alpha(opacity=100)");
//	    	//DOM.setStyleAttribute(impl.getElement(), "-moz-opacity", "");
//	    	//DOM.setStyleAttribute(impl.getElement(), "-khtml-opacity", "");
//	    	
//		} else {
//			DOM.setStyleAttribute(impl.getElement(), "opacity", "0.2");  //IE
//			DOM.setStyleAttribute(impl.getElement(), "filter", "alpha(opacity=20)");  //non-IE
//			//DOM.setStyleAttribute(impl.getElement(), "-moz-opacity", "0.0");
//			//DOM.setStyleAttribute(impl.getElement(), "-khtml-opacity", "0.0");
//			
//	    }
	    if (b == true){
	    	//TODO
	    } else {
	    	//TODO: save the original color as well
	    	DOM.setStyleAttribute(impl.getElement(), "background", "rgba(0,0,0,0)");
	    }
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
		DOM.setStyleAttribute(impl.getElement(), "background", Color.getColorString(color));
		//DOM.setStyleAttribute(impl.getElement(), "background", "rgba("+ color.getRed()+", "+color.getGreen()+", "+color.getBlue()+", 1)");
		
    }

}

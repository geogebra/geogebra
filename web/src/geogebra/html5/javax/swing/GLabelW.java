package geogebra.html5.javax.swing;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.main.App;

import com.google.gwt.user.client.DOM;

public class GLabelW extends geogebra.common.javax.swing.GLabel {

	private com.google.gwt.user.client.ui.HTML impl;
	
	GLabelW(){
		impl = new com.google.gwt.user.client.ui.HTML();
	}
	
	public GLabelW(String string) {
		impl = new com.google.gwt.user.client.ui.HTML(string);
    }

	public static com.google.gwt.user.client.ui.Label getImpl(GLabelW label){
		if (label==null) return null;
		return label.impl;
	}
	
	@Override
    public void setVisible(boolean b) {
	    impl.setVisible(b);
    }

	@Override
    public void setText(String text) {
	    impl.setHTML(text);
	    
    }

	@Override
    public void setOpaque(boolean b) {
	    App.debug("implementation needed - just finishing"); // TODO Auto-generated


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
    public void setFont(GFont font) {
	    App.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public void setForeground(GColor color) {
	    impl.getElement().getStyle().setColor(color.toString());
    }

	@Override
    public void setBackground(GColor color) {
		DOM.setStyleAttribute(impl.getElement(), "background", GColor.getColorString(color));
		//DOM.setStyleAttribute(impl.getElement(), "background", "rgba("+ color.getRed()+", "+color.getGreen()+", "+color.getBlue()+", 1)");
		
    }

}

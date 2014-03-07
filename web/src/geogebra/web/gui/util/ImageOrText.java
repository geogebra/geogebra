package geogebra.web.gui.util;

import geogebra.common.awt.GColor;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Label;

public class ImageOrText {
	public String url = null;
	public String text = null;
	public GColor bgColor = null, fgColor = null;
	public static ImageOrText[] convert(ImageResource[] res) {
	    ImageOrText[] arr = new ImageOrText[res.length];
	    for(int i=0; i< arr.length; i++){
	    	arr[i] = new ImageOrText();
	    	arr[i].url = res[i].getSafeUri().asString();
	    }
	    return arr;
    }
	
	public static ImageOrText[] convert(String[] res) {
	    ImageOrText[] arr = new ImageOrText[res.length];
	    for(int i=0; i< arr.length; i++){
	    	arr[i] = new ImageOrText();
	    	arr[i].text = res[i];
	    }
	    return arr;
    }

	public void applyToLabel(Label button) {
		button.setPixelSize(32,32);
		if(this.url != null){
			button.getElement().getStyle().setBackgroundImage("url("+this.url+")");
			button.getElement().getStyle().setProperty("backgroundRepeat", "no-repeat");
		}
		if(this.text != null){
			button.setText(this.text);
		}
		if(this.fgColor != null){
			button.getElement().getStyle().setBackgroundColor(GColor.getColorString(this.fgColor));
		}
		if(this.bgColor != null){
			button.getElement().getStyle().setBackgroundColor(GColor.getColorString(this.bgColor));
		}
	    
    }
}

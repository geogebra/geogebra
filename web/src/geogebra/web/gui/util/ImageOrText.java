package geogebra.web.gui.util;

import geogebra.common.awt.GColor;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Label;

public class ImageOrText {
	private String url = null;
	private String text = null;
	private GColor bgColor = null, fgColor = null;
	private int bgSize;

	public ImageOrText() {
	    // TODO Auto-generated constructor stub
    }

	/**
	 * @param string
	 *            text
	 */
	public ImageOrText(String string) {
		this.setText(string);
	}

	public static ImageOrText[] convert(ImageResource[] res, int size) {
	    ImageOrText[] arr = new ImageOrText[res.length];
	    for(int i=0; i< arr.length; i++){
	    	if(res[i] == null){
	    		return arr;
	    	}
	    	arr[i] = new ImageOrText();
	    	arr[i].setUrl(res[i].getSafeUri().asString());
	    	arr[i].bgSize = size;
	    }
	    return arr;
    }
	
	public static ImageOrText[] convert(String[] res) {
	    ImageOrText[] arr = new ImageOrText[res.length];
	    for(int i=0; i< arr.length; i++){
	    	arr[i] = new ImageOrText();
	    	arr[i].setText(res[i]);
	    }
	    return arr;
    }

	/**
	 * @param button
	 *            {@link Label}
	 */
	public void applyToLabel(Label button) {
		if (url != null) {
			button.getElement().getStyle()
			        .setBackgroundImage("url(" + url + ")");
			if (bgSize > 0) {
				button.getElement().getStyle()
				        .setProperty("backgroundSize", bgSize + "px");
			}
			if (text != null) {
				button.addStyleName("textIconButton");
			} else {
				button.addStyleName("stylebarButton");
			}
		}
		if (text != null) {
			button.setText(text);
			if (fgColor != null) {
				button.getElement().getStyle()
				        .setColor(GColor.getColorString(fgColor));
			}
			button.setWidth("auto");
			return;
		}
		if (fgColor != null) {
			button.getElement()
			        .getStyle()
			        .setBorderColor(
			                "rgba(" + fgColor.getRed() + ", "
			                        + fgColor.getGreen() + ", "
			                        + fgColor.getBlue() + ", 1)");
			button.getElement().addClassName("borderButton");
			button.getElement().getStyle().setBackgroundColor(GColor.getColorString(fgColor));
		}
		if (bgColor != null) {
			button.getElement().getStyle()
			        .setBackgroundColor(GColor.getColorString(bgColor));
		}
	    
    }

	/**
	 * @return the url
	 */
    public String getUrl() {
	    return url;
    }

	/**
	 * @param url the url to set
	 */
    public void setUrl(String url) {
	    this.url = url;
    }

	/**
	 * @param bgColor the bgColor to set
	 */
    public void setBgColor(GColor bgColor) {
	    this.bgColor = bgColor;
    }

	/**
	 * @param fgColor the fgColor to set
	 */
    public void setFgColor(GColor fgColor) {
	    this.fgColor = fgColor;
    }

	/**
	 * @param text the text to set
	 */
    public void setText(String text) {
	    this.text = text;
    }

	/**
	 * @return text
	 */
	public String getText() {
		return this.text;
	}

	public void setBgSize(int size) {
		this.bgSize = size;
	}
}

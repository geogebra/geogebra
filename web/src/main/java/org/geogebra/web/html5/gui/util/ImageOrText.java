package org.geogebra.web.html5.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Label;

/**
 * image or text holder
 */
public class ImageOrText {
	private String url = null;
	private String text = null;
	private GColor bgColor = null;
	private GColor fgColor = null;
	private int bgSize;
	private String className;

	/**
	 * empty constructor
	 */
	public ImageOrText() {
		// empty constructor
    }

	/**
	 * @param string
	 *            text
	 */
	public ImageOrText(String string) {
		this.setText(string);
	}

	/**
	 * @param res
	 *            image resource
	 */
	public ImageOrText(ImageResource res) {
		setResource(res);
		bgSize = res.getWidth();
	}

	/**
	 * @param res
	 *            svg resource
	 * @param width
	 *            width
	 */
	public ImageOrText(SVGResource res, int width) {
		setSvgRes(res);
		bgSize = width;
	}

	/**
	 * @param res
	 *            svg resource
	 */
	public void setSvgRes(SVGResource res) {
		if (res != null) {
			setUrl(res.getSafeUri().asString());
		}
	}

	/**
	 * @param res
	 *            resource
	 */
	public void setResource(ImageResource res) {
		if (res != null) {
			setUrl(res.getSafeUri().asString());
		}
	}

	/**
	 * @param res
	 *            resource
	 * @param size
	 *            size
	 * @return converted array
	 */
	public static ImageOrText[] convert(ImageResource[] res, int size) {
	    ImageOrText[] arr = new ImageOrText[res.length];
		for (int i = 0; i < arr.length; i++) {
			if (res[i] == null) {
	    		return arr;
	    	}
			arr[i] = new ImageOrText(res[i]);
			arr[i].bgSize = size;
	    }
	    return arr;
    }

	/**
	 * @param res
	 *            resource
	 * @param size
	 *            size
	 * @return converted array
	 */
	public static ImageOrText[] convert(SVGResource[] res, int size) {
		ImageOrText[] arr = new ImageOrText[res.length];
		for (int i = 0; i < arr.length; i++) {
			if (res[i] == null) {
				return arr;
			}
			arr[i] = new ImageOrText(res[i], size);
		}
		return arr;
	}
	
	/**
	 * @param res
	 *            resource
	 * @return converted array
	 */
	public static ImageOrText[] convert(String[] res) {
	    ImageOrText[] arr = new ImageOrText[res.length];
		for (int i = 0; i < arr.length; i++) {
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
			if (bgSize > 0) {
				button.getElement().getStyle()
						.setProperty("backgroundSize",
								bgSize + "px " + bgSize + "px");
			}
			button.getElement().getStyle()
					.setBackgroundImage("url(" + url + ")");
			if (text != null) {
				button.addStyleName("textIconButton");
			} else if (className != null) {
				button.getElement().addClassName("borderButton");
				button.getElement().addClassName(className);
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
			if ("+".equals(text)) {
				button.getElement().addClassName("borderButton");
				button.getElement().addClassName("plusButton");
			} else {
				button.setWidth("auto");
			}
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
			button.getElement().getStyle()
					.setBackgroundColor(GColor.getColorString(fgColor));
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

	/**
	 * @param size
	 *            size
	 */
	public void setBgSize(int size) {
		this.bgSize = size;
	}

	/**
	 * @param string
	 *            class name
	 * @return this image
	 */
	public ImageOrText setClass(String string) {
		this.className = string;
		return this;
	}
}

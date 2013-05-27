package geogebra.touch.gui.elements;

import org.vectomatic.dom.svg.ui.SVGResource;

public class ArrowImageButton extends StandardImageButton
{

	public ArrowImageButton(SVGResource icon)
	{
		super(icon);
		this.setStyleName("arrowLeft");
	}
	
}
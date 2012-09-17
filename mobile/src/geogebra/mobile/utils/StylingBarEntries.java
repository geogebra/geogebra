package geogebra.mobile.utils;

import geogebra.common.awt.GColor;
import geogebra.mobile.gui.CommonResources;
import geogebra.mobile.gui.elements.toolbar.ToolBarButton;
import geogebra.mobile.model.GuiModel;
import geogebra.web.awt.GColorW;

import org.vectomatic.dom.svg.ui.SVGResource;

public enum StylingBarEntries
{
	Point(GColor.blue, new SVGResource[]{CommonResources.INSTANCE.label()}), 
	DependentPoints(GColor.darkGray, new SVGResource[]{CommonResources.INSTANCE.label()}),
	Line(GColor.black, new SVGResource[]{}), 
	Polygon(new GColorW(153,51,0), new SVGResource[]{});
	
	GColor defaultColor; 
	SVGResource[] entry;
	ToolBarButton[] buttons; 

	StylingBarEntries(GColor color, SVGResource[] entries)
	{
		this.defaultColor = color; 
		this.entry = entries;
	}
	
	public GColor getColor(){
		return this.defaultColor; 
	}
	
	public ToolBarButton[] getButtons(GuiModel model){
		if(this.buttons == null){
			this.buttons = new ToolBarButton[this.entry.length]; 
			for(int i = 0; i < this.entry.length; i++){
				this.buttons[i] = new ToolBarButton(this.entry[i], model); 
			}
		}		
		return this.buttons; 
	}
}

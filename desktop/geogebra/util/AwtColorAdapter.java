package geogebra.util;

import geogebra.common.awt.ColorAdapter;

import java.awt.Color;
public class AwtColorAdapter extends java.awt.Color implements ColorAdapter{
	
	public AwtColorAdapter(Color color){
		super(color.getRed(), color.getGreen(), color.getBlue());
		
	}

	public AwtColorAdapter(int red, int green, int blue) {
		super(red, green, blue);
	}	
}

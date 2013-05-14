package geogebra.javax.swing;

import geogebra.common.javax.swing.GImageIcon;

import javax.swing.ImageIcon;

public class GImageIconD extends GImageIcon{

	private ImageIcon impl;
	
	public GImageIconD(ImageIcon ii){
		impl = ii;
	}
	
	public ImageIcon getImpl(){
		return impl;
	}
}

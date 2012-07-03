package geogebra.awt;

import geogebra.common.main.App;

public class GComponentD implements geogebra.common.awt.Component{
	private java.awt.Component impl;
	public GComponentD(Object component){
		if (component instanceof java.awt.Component){
			impl = (java.awt.Component) component;
		} else {
			App.warn(
					"Function called with not the right type.");
		}
	}
	
}

package geogebra.javax.swing;

//import geogebra.gui.inputfield.AutoCompleteTextField;

public class Box extends geogebra.common.javax.swing.Box {
	
	static javax.swing.Box impl = null; 
	
	private Box(javax.swing.Box box) {
		this.impl = box;
	}
		
	public javax.swing.Box getImpl() {
		return this.impl;
	}

}

package geogebra.javax.swing;

import geogebra.common.awt.Dimension;
import geogebra.common.awt.Rectangle;
import geogebra.common.gui.inputfield.AutoCompleteTextField;

//import geogebra.gui.inputfield.AutoCompleteTextField;

public class Box extends geogebra.common.javax.swing.Box {
	
	private javax.swing.Box impl = null; 
	
	public Box(javax.swing.Box box) {
		this.impl = box;
	}
		
	public javax.swing.Box getImpl() {
		return this.impl;
	}

	@Override
	public void add(geogebra.common.javax.swing.JLabel label) {
		impl.add(((geogebra.javax.swing.JLabel)label).getImpl());
	}

	@Override
	public void add(AutoCompleteTextField textField) {
		impl.add((geogebra.gui.inputfield.AutoCompleteTextField)textField);
		
	}

	@Override
	public void setVisible(boolean isVisible) {
		impl.setVisible(isVisible);
	}

	@Override
	public void setBounds(Rectangle rect) {
		impl.setBounds(geogebra.awt.Rectangle.getAWTRectangle(rect));
	}

	@Override
	public Dimension getPreferredSize() {
		return new geogebra.awt.Dimension(impl.getPreferredSize());
	}

	@Override
	public Rectangle getBounds() {
		return new geogebra.awt.Rectangle(impl.getBounds());
	}

//	@Override
//	public geogebra.common.javax.swing.Box createHorizontalBox() {
//		return new Box(javax.swing.Box.createHorizontalBox());
//	}

}

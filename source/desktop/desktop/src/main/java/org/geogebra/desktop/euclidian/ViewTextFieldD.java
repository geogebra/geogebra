package org.geogebra.desktop.euclidian;

import java.awt.Graphics2D;

import javax.swing.Box;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.ViewTextField;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.awt.GRectangleD;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;

public class ViewTextFieldD extends ViewTextField {

	private Box box;
	private AutoCompleteTextFieldD textField;
	private final EuclidianViewInterfaceD euclidianView;

	public ViewTextFieldD(EuclidianViewInterfaceD euclidianView) {
		this.euclidianView = euclidianView;
	}

	private AutoCompleteTextFieldD newAutoCompleteTextField(int length,
			Drawable drawTextField) {
		return new AutoCompleteTextFieldD(length,
				euclidianView.getApplication(), drawTextField);
	}

	/**
	 * @return box wrapping the input field
	 */
	public Box getBox() {
		createBox();
		return box;
	}

	private void createBox() {
		if (box == null) {
			box = Box.createHorizontalBox();
			box.add(textField);
		}
	}

	@Override
	public void revalidateBox() {
		getBox().revalidate();
	}

	@Override
	public void setBoxVisible(boolean isVisible) {
		getBox().setVisible(isVisible);
	}

	@Override
	public void setBoxBounds(GRectangle rect) {
		getBox().setBounds(GRectangleD.getAWTRectangle(rect));
	}

	@Override
	public void repaintBox(GGraphics2D g) {
		g.translate(box.getBounds().getX(), box.getBounds().getY());
		box.paint(GGraphics2DD.getAwtGraphics(g));
		g.translate(-box.getBounds().getX(), -box.getBounds().getY());
	}

	@Override
	public void hideDeferred() {
		if (textField != null) {
			textField.setVisible(false);
			getBox().setVisible(false);
		}
	}

	@Override
	public AutoCompleteTextField getTextField(int length,
			DrawInputBox drawInputBox) {
		if (textField == null) {
			textField = newAutoCompleteTextField(length, drawInputBox);
			textField.setAutoComplete(false);
			textField.enableColoring(false);
			textField.setOpaque(true);
			textField.setFocusable(true);
			textField.setFocusTraversalKeysEnabled(false);
			createBox();
			box.add(textField);
			this.euclidianView.add(box);
		} else {
			textField.setDrawTextField(drawInputBox);
		}

		return textField;
	}

	/**
	 * @return textfield (may be null)
	 */
	@Override
	public AutoCompleteTextField getTextField() {
		return textField;
	}

	@Override
	public void remove() {
		textField = null;
		box = null;
	}

	@Override
	public void setColumns(int length) {
		if (textField != null) {
			textField.setColumns(length);
		}
	}

	@Override
	protected void applyChanges() {
		// Clear implementation, not needed.
	}

	@Override
	public void draw(DrawInputBox inputBox) {
		inputBox.draw(new GGraphics2DD((Graphics2D) euclidianView.getJPanel().getGraphics()));
	}
}

package org.geogebra.desktop.euclidian;

import com.himamis.retex.editor.desktop.MathFieldD;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GRectangleD;

import javax.swing.Box;

public class SymbolicEditorD extends SymbolicEditor {

	private Box box;
	private MathFieldD mathField;

	protected SymbolicEditorD(App app, EuclidianView view) {
		super(app, view);

		box = Box.createHorizontalBox();

		mathField = new MathFieldD();
		mathField.getInternal().setFieldListener(this);
		mathField.setVisible(true);
		box.add(mathField);
	}

	@Override
	public void hide() {
		Log.debug("hide called");

		box.setVisible(false);
	}

	@Override
	public boolean isClicked(GPoint point) {
		return false;
	}

	@Override
	public void attach(GeoInputBox geoInputBox, GRectangle bounds) {
		Log.debug("attach called");

		this.geoInputBox = geoInputBox;
		this.drawInputBox = (DrawInputBox) view.getDrawableFor(geoInputBox);

		//mathField.getInternal().parse(geoInputBox.getTextForEditor());
		mathField.getInternal().parse("alma");
		mathField.setBounds(GRectangleD.getAWTRectangle(bounds));
		((EuclidianViewD) view).add(box);
		box.setVisible(true);
	}

	@Override
	public void onEnter() {

	}

	@Override
	public void onKeyTyped() {

	}

	@Override
	public boolean onEscape() {
		return false;
	}

	@Override
	public void onTab(boolean shiftDown) {

	}
}

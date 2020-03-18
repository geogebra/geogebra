package org.geogebra.desktop.euclidian;

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.renderer.share.TeXFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.awt.GRectangleD;

import javax.swing.Box;

public class SymbolicEditorD extends SymbolicEditor {

	private Box box;
	private MathFieldD mathField;

	protected SymbolicEditorD(App app, EuclidianView view) {
		super(app, view);

		box = Box.createHorizontalBox();

		mathField = new MathFieldD();
		mathFieldInternal = mathField.getInternal();

		mathField.getInternal().setFieldListener(this);
		mathField.setVisible(true);
		mathField.getInternal().setType(TeXFont.SANSSERIF);

		box.add(mathField);
	}

	@Override
	public void hide() {
		applyChanges();
		drawInputBox.setEditing(false);
		box.setVisible(false);
	}

	@Override
	public boolean isClicked(GPoint point) {
		return false;
	}

	@Override
	public void attach(GeoInputBox geoInputBox, GRectangle bounds) {
		this.geoInputBox = geoInputBox;
		this.drawInputBox = (DrawInputBox) view.getDrawableFor(geoInputBox);

		drawInputBox.setEditing(true);

		mathField.getInternal().parse(geoInputBox.getTextForEditor());
		mathField.setBounds(GRectangleD.getAWTRectangle(bounds));
		mathField.getInternal().setSize(geoInputBox.getFontSizeMultiplier()
				* (app.getSettings().getFontSettings().getAppFontSize() + 3));

		box.setBounds(GRectangleD.getAWTRectangle(bounds));
		((EuclidianViewD) view).add(box);
		box.setVisible(true);
		box.revalidate();
	}

	@Override
	public void repaintBox(GGraphics2D g) {
		g.translate(box.getBounds().getX(), box.getBounds().getY());
		box.paint(GGraphics2DD.getAwtGraphics(g));
		g.translate(-box.getBounds().getX(), -box.getBounds().getY());
	}

	@Override
	public void onEnter() {
		applyChanges();
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
		applyChanges();
	}
}

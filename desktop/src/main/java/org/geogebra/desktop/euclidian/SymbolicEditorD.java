package org.geogebra.desktop.euclidian;

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.renderer.share.TeXFont;
import org.geogebra.common.awt.GColor;
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
	private double baseline;

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

		baseline = bounds.getY() + bounds.getHeight() / 2;

		box.setBounds(GRectangleD.getAWTRectangle(bounds));
		((EuclidianViewD) view).add(box);
		box.setVisible(true);
		box.revalidate();

		mathField.requestViewFocus();
	}

	@Override
	public void repaintBox(GGraphics2D g) {
		GColor bgColor = geoInputBox.getBackgroundColor() != null
				? geoInputBox.getBackgroundColor() : view.getBackgroundCommon();
		String text = serializer.serialize(mathFieldInternal.getFormula());

		double currentHeight = drawInputBox.getPreferredHeight(text);
		box.setBounds(box.getX(), box.getY(), box.getWidth(), (int) currentHeight);
		box.revalidate();

		g.saveTransform();
		g.translate(box.getX(), baseline - currentHeight / 2);
		view.getTextField().drawBounds(g, bgColor, 0, 0, box.getWidth(), (int) currentHeight);

		g.translate(DrawInputBox.TF_PADDING_HORIZONTAL, 0);
		box.paint(GGraphics2DD.getAwtGraphics(g));

		g.restoreTransform();
	}

	@Override
	public void onEnter() {
		applyChanges();
	}

	@Override
	public void onKeyTyped() {
		view.repaintView();
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

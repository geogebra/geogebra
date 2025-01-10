package org.geogebra.desktop.euclidian;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Box;
import javax.swing.SwingUtilities;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.TextRendererSettings;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.awt.GRectangleD;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.renderer.share.TeXFont;

public class SymbolicEditorD extends SymbolicEditor {

	private final Box box;
	private final MathFieldD mathField;

	protected SymbolicEditorD(App app, EuclidianView view) {
		super(app, view);

		box = Box.createHorizontalBox();

		mathField = new MathFieldD(new SyntaxAdapterImpl(app.getKernel()), view::repaintView);

		mathField.getInternal().addMathFieldListener(this);
		mathField.setVisible(true);
		mathField.getInternal().setType(TeXFont.SANSSERIF);

		mathField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent focusEvent) {
				// do nothing
			}

			@Override
			public void focusLost(FocusEvent focusEvent) {
				applyAndHide();
			}
		});

		box.add(mathField);
	}

	@Override
	protected void showRedefinedBox(final DrawInputBox drawable) {
		SwingUtilities.invokeLater(() -> drawable.setWidgetVisible(true));
	}

	@Override
	protected MathFieldInternal getMathFieldInternal() {
		return mathField.getInternal();
	}

	@Override
	protected void hide() {
		getDrawInputBox().setEditing(false);
		box.setVisible(false);
		view.repaintView();
	}

	@Override
	public boolean isClicked(GPoint point) {
		return false;
	}

	@Override
	public void attach(GeoInputBox geoInputBox, GRectangle bounds, TextRendererSettings settings) {
		setInputBox(geoInputBox);
		getDrawInputBox().setEditing(true);

		mathField.getInternal().setType(getGeoInputBox().isSerifContent()
				? TeXFont.SERIF : TeXFont.SANSSERIF);
		resetChanges();
		mathField.setBounds(GRectangleD.getAWTRectangle(bounds));
		mathField.getInternal().setSize(geoInputBox.getFontSizeMultiplier()
				* (app.getSettings().getFontSettings().getAppFontSize() + 3));

		setBaseline(bounds.getY() + bounds.getHeight() / 2);

		box.setBounds(GRectangleD.getAWTRectangle(bounds));
		((EuclidianViewD) view).add(box);
		box.setVisible(true);
		box.revalidate();

		mathField.requestViewFocus();
	}

	@Override
	public void repaintBox(GGraphics2D g) {
		GColor bgColor = getGeoInputBox().getBackgroundColor() != null
				? getInputBoxBackgroundColor() : view.getBackgroundCommon();

		g.saveTransform();
		int boxY = (int) computeTop(box.getHeight());
		int boxX = box.getX();
		AutoCompleteTextFieldD.drawBounds(g, bgColor, boxX, boxY,
				box.getWidth(), box.getHeight(), getDrawInputBox());

		mathField.setForeground(GColorD.getAwtColor(getGeoInputBox().getObjectColor()));
		box.setBorder(null);
		g.setClip(boxX, boxY, box.getWidth(), box.getHeight());
		mathField.scrollHorizontally(box.getWidth());
		g.translate(boxX + DrawInputBox.TF_PADDING_HORIZONTAL - mathField.getScrollX(), boxY);
		box.paint(GGraphics2DD.getAwtGraphics(g));

		g.restoreTransform();
		g.resetClip();
	}

	private GColor getInputBoxBackgroundColor() {
		return getGeoInputBox().hasError() ? GColor.ERROR_RED_BACKGROUND
				: getGeoInputBox().getBackgroundColor();
	}

	@Override
	public void onKeyTyped(String key) {
		addDegree(key, mathField.getInternal());
		String text = texSerializer.serialize(getMathFieldInternal().getFormula());
		GDimension equationSize = app.getDrawEquation().measureEquation(app, text,
				getDrawInputBox().getTextFont(text), false);
		double currentHeight = equationSize.getHeight() + 2 * DrawInputBox.TF_MARGIN_VERTICAL;
		box.setBounds(box.getX(), box.getY(), box.getWidth(),
				Math.max((int) currentHeight, DrawInputBox.SYMBOLIC_MIN_HEIGHT));
		dispatchKeyTypeEvent(key);
		box.revalidate();
		view.repaintView();
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		view.repaintView();
		return false;
	}

	@Override
	public boolean onEscape() {
		return false;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		applyChanges();
		return true;
	}

	@Override
	protected void selectEntryAt(int x, int y) {
		mathField.getInternal().selectEntryAt(x, y);
	}
}

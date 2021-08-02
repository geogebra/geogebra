package org.geogebra.desktop.euclidian;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Box;
import javax.swing.SwingUtilities;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.awt.GRectangleD;

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.renderer.share.TeXFont;

public class SymbolicEditorD extends SymbolicEditor {

	private final Box box;
	private final MathFieldD mathField;
	private double baseline;

	protected SymbolicEditorD(App app, EuclidianView view) {
		super(app, view);

		box = Box.createHorizontalBox();

		mathField = new MathFieldD(new SyntaxAdapterImpl(app.kernel), view::repaintView);

		mathField.getInternal().setFieldListener(this);
		mathField.setVisible(true);
		mathField.getInternal().setType(TeXFont.SANSSERIF);

		mathField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent focusEvent) {
				// do nothing
			}

			@Override
			public void focusLost(FocusEvent focusEvent) {
				hide();
			}
		});

		box.add(mathField);
	}

	@Override
	public void resetChanges() {
		String text = getGeoInputBox().getTextForEditor();

		boolean textMode = isTextMode();
		mathField.getInternal().setPlainTextMode(textMode);
		if (textMode) {
			mathField.setPlainText(text);
		} else {
			mathField.parse(text);
		}

		if (getGeoInputBox().getLinkedGeo().hasSpecialEditor()) {
			getMathFieldInternal().getFormula().getRootComponent().setProtected();
			getMathFieldInternal().setLockedCaretPath();
		}
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
	public void hide() {
		if (getDrawInputBox().isEditing()) {
			applyChanges();
			getDrawInputBox().setEditing(false);
			box.setVisible(false);
			view.repaintView();
		}
	}

	@Override
	public boolean isClicked(GPoint point) {
		return false;
	}

	@Override
	public void attach(GeoInputBox geoInputBox, GRectangle bounds) {
		setInputBox(geoInputBox);
		getDrawInputBox().setEditing(true);

		mathField.getInternal().setType(getGeoInputBox().isSerifContent()
				? TeXFont.SERIF	:  TeXFont.SANSSERIF);
		mathField.getInternal().parse(getGeoInputBox().getTextForEditor());
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
		GColor bgColor = getGeoInputBox().getBackgroundColor() != null
				? getGeoInputBox().getBackgroundColor() : view.getBackgroundCommon();

		g.saveTransform();
		int boxY = (int) (baseline - (double) (box.getHeight()) / 2);
		int boxX = box.getX();
		view.getTextField().drawBounds(g, bgColor, boxX, boxY, box.getWidth(), box.getHeight());

		mathField.setForeground(GColorD.getAwtColor(getGeoInputBox().getObjectColor()));
		box.setBorder(null);
		g.setClip(boxX, boxY, box.getWidth(), box.getHeight());
		mathField.scrollHorizontally(box.getWidth());
		g.translate(boxX + DrawInputBox.TF_PADDING_HORIZONTAL - mathField.getScrollX(), boxY);
		box.paint(GGraphics2DD.getAwtGraphics(g));

		g.restoreTransform();
		g.resetClip();
	}

	@Override
	public void onKeyTyped(String key) {
		addDegree(key, mathField.getInternal());
		String text = texSerializer.serialize(getMathFieldInternal().getFormula());
		double currentHeight = app.getDrawEquation().measureEquation(app, null, text,
				getDrawInputBox().getTextFont(text), false).getHeight() + 2 * DrawInputBox.TF_MARGIN_VERTICAL;
		box.setBounds(box.getX(), box.getY(), box.getWidth(),
				Math.max((int) currentHeight, DrawInputBox.SYMBOLIC_MIN_HEIGHT));
		box.revalidate();
		view.repaintView();
	}

	@Override
	public void onCursorMove() {
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

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MediaBoundingBox;
import org.geogebra.common.euclidian.inline.InlineFormulaController;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoFormula;

public class DrawFormula extends Drawable implements DrawInline {

	public static final int PADDING = 8;

	private final TransformableRectangle rectangle;

	private final GeoFormula formula;
	private final InlineFormulaController formulaController;

	/**
	 * @param ev view
	 * @param formula formula
	 */
	public DrawFormula(EuclidianView ev, GeoFormula formula) {
		super(ev, formula);
		this.rectangle = new TransformableRectangle(view, formula, false);
		this.formula = formula;
		this.formulaController = ev.getApplication().createInlineFormulaController(ev, formula);
		update();
	}

	@Override
	public void update() {
		updateStrokes(geo);
		labelDesc = geo.toValueString(StringTemplate.defaultTemplate);
		rectangle.updateSelfAndBoundingBox();

		GPoint2D point = formula.getLocation();
		if (formulaController != null && point != null) {
			double angle = formula.getAngle();
			double width = formula.getWidth();
			double height = formula.getHeight();

			formulaController.setLocation(view.toScreenCoordX(point.getX()),
					view.toScreenCoordY(point.getY()));
			formulaController.setHeight((int) (height));
			formulaController.setWidth((int) (width - PADDING));
			formulaController.setAngle(angle);
			formulaController.setColor(geo.getObjectColor());
			formulaController.setFontSize(view.getFontSize());
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (formula.isEuclidianVisible()
				&& (formulaController == null || !formulaController.isInForeground())
			&& rectangle.getDirectTransform() != null) {
			g2.setPaint(geo.getObjectColor());
			g2.setStroke(objStroke); // needed eg for \sqrt
			g2.saveTransform();
			g2.transform(rectangle.getDirectTransform());
			g2.translate(PADDING, PADDING);
			GFont font = view.getApplication().getFontCommon(false,
					GFont.PLAIN, view.getFontSize());
			drawMultilineLaTeX(g2, font,
					geo.getObjectColor(), view.getBackgroundCommon());
			g2.restoreTransform();
		}
	}

	@Override
	public GRectangle getBounds() {
		return rectangle.getBounds();
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return rectangle.hit(x, y);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(getBounds());
	}

	@Override
	public MediaBoundingBox getBoundingBox() {
		return rectangle.getBoundingBox();
	}

	@Override
	public String urlByCoordinate(int x, int y) {
		return "";
	}

	@Override
	public void saveContent() {
		if (formulaController != null) {
			formula.setContent(formulaController.getText());
		}
	}

	@Override
	public GAffineTransform getTransform() {
		return rectangle.getDirectTransform();
	}

	@Override
	public BoundingBox<? extends GShape> getSelectionBoundingBox() {
		return getBoundingBox();
	}

	@Override
	public void updateByBoundingBoxResize(GPoint2D point, EuclidianBoundingBoxHandler handler) {
		rectangle.updateByBoundingBoxResize(point, handler);
	}

	@Override
	public void updateContent() {
		if (formulaController != null
				&& !formulaController.getText().equals(formula.getContent())) {
			formulaController.updateContent(formula.getContent());
		}
	}

	@Override
	public void toForeground(int x, int y) {
		if (formulaController != null) {
			GPoint2D p = rectangle.getInversePoint(x - PADDING, y - PADDING);
			formulaController.toForeground((int) p.getX(), (int) p.getY());
		}
	}

	@Override
	public void toBackground() {
		if (formulaController != null) {
			formulaController.toBackground();
		}
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		rectangle.fromPoints(points);
	}

	@Override
	protected List<GPoint2D> toPoints() {
		return rectangle.toPoints();
	}

	@Override
	public void remove() {
		if (formulaController != null) {
			formulaController.discard();
		}
	}
}

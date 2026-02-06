/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.multiuser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawLocus;
import org.geogebra.common.euclidian.draw.DrawSegment;
import org.geogebra.common.euclidian.draw.HasTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.RectangleTransformable;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.timer.client.Timer;

class User {

	private final TooltipChip tooltip;
	// we are storing the labels since the geo might be recreated
	private final HashSet<String> selectedGeos;
	private final HashMap<String, Timer> updatedGeos;

	private final GColor color;

	User(String user, GColor color) {
		this.tooltip = new TooltipChip(user, color);
		this.selectedGeos = new HashSet<>();
		this.updatedGeos = new HashMap<>();
		this.color = color;
	}

	public void addSelection(EuclidianView view, String label) {
		selectedGeos.add(label);
		view.repaintView();
	}

	public void addInteraction(EuclidianView view, String label) {
		GeoElement geo = view.getApplication().getKernel().lookupLabel(label);
		if (geo instanceof GeoLocusStroke && !selectedGeos.contains(label)) {
			updatedGeos.computeIfAbsent(label, k -> new Timer() {
				@Override
				public void run() {
					updatedGeos.remove(label);
					view.repaintView();
				}
			}).schedule(2000);
			view.repaintView();
		}
	}

	public void deselectAll(EuclidianView view) {
		selectedGeos.clear();
		updatedGeos.clear();
		view.repaintView();
	}

	public void removeSelection(String label) {
		selectedGeos.remove(label);
	}

	public void paintInteractionBackgrounds(EuclidianView view, GGraphics2D graphics) {
		List<GeoElement> geos = getHighlightedGeos(view);
		if (geos.size() == 1) {
			GeoElement geo = geos.get(0);
			Drawable d = (Drawable) view.getDrawableFor(geo);
			if (drawAsBackground(d)) {
				d.updateIfNeeded();
				GBasicStroke selStroke = EuclidianStatic.getStroke(geo
								.getLineThickness() / 2.0
								+ 4,
						EuclidianStyleConstants.LINE_TYPE_FULL);
				graphics.setStroke(selStroke);
				graphics.setColor(color.deriveWithAlpha(geo.getLineOpacity()));
				d.drawStroke(graphics);
			}
		}
	}

	public void paintInteractionBoxes(EuclidianView view, GGraphics2D graphics) {
		List<GeoElement> geos = getHighlightedGeos(view);

		graphics.setColor(color);
		GPoint2D pt = null;
		if (geos.size() == 1) {
			GeoElement geo = geos.get(0);
			Drawable d = (Drawable) view.getDrawableFor(geo);
			if (d instanceof HasTransformation hasTransformation) {
				RectangleTransformable transformableGeo = (RectangleTransformable) geo;
				graphics.saveTransform();
				graphics.transform(hasTransformation.getTransform());
				graphics.draw(AwtFactory.getPrototype().newRectangle(
						(int) transformableGeo.getWidth(),
						(int) transformableGeo.getHeight()
				));
				graphics.restoreTransform();
				pt = new GPoint2D(transformableGeo.getWidth(), 0);
				hasTransformation.getTransform().transform(pt, pt);
			} else if (d != null && !drawAsBackground(d)) {
				GRectangle2D bounds = d.getBoundsForStylebarPosition();
				if (bounds != null) {
					graphics.draw(bounds);
					pt = new GPoint2D(bounds.getMaxX(), bounds.getMinY());
				}
			}
		} else if (geos.size() > 1) {
			GRectangle2D multiBounds = view.getEuclidianController().calculateBounds(geos);
			pt = new GPoint2D(multiBounds.getMaxX(), multiBounds.getMinY());
			graphics.draw(multiBounds);
		}

		if (geos.isEmpty() || pt == null) {
			tooltip.hide();
		} else {
			showTooltipBy((AppW) view.getApplication(), pt);
		}
	}

	private boolean drawAsBackground(Drawable d) {
		return d instanceof DrawLocus || d instanceof DrawSegment;
	}

	private List<GeoElement> getHighlightedGeos(EuclidianView view) {
		SelectionManager selection = view.getApplication().getSelectionManager();

		Stream<String> startingStream = selectedGeos.isEmpty()
				? updatedGeos.keySet().stream()
				: selectedGeos.stream();
		return startingStream
				.map((label) -> view.getApplication().getKernel().lookupLabel(label))
				.filter((geo) -> !selection.containsSelectedGeo(geo))
				.collect(Collectors.toList());
	}

	private void showTooltipBy(AppW app, GPoint2D pt) {
		app.getAppletFrame().add(tooltip);
		tooltip.show(pt.x + getOffsetX(app), pt.y + getOffsetY(app));
	}

	private double getOffsetY(AppW app) {
		return app.getActiveEuclidianView().getAbsoluteTop() - app.getAbsTop();
	}

	private double getOffsetX(AppW app) {
		return app.getActiveEuclidianView().getAbsoluteLeft() - app.getAbsLeft();
	}

	public void rename(GeoElement target) {
		String oldLabel = target.getOldLabel();
		if (selectedGeos.contains(oldLabel)) {
			selectedGeos.remove(oldLabel);
			selectedGeos.add(target.getLabelSimple());
			target.getKernel().notifyRepaint();
		}
		// only selected geos handled here, updated geos are on timer => eventual consistency
	}
}

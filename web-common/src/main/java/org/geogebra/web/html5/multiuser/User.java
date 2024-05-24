package org.geogebra.web.html5.multiuser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawLocus;
import org.geogebra.common.euclidian.draw.DrawSegment;
import org.geogebra.common.euclidian.draw.HasTransformation;
import org.geogebra.common.factories.AwtFactory;
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
	private final ArrayList<String> selectedGeos;
	private final HashMap<String, Timer> updatedGeos;

	private final GColor color;

	User(String user, GColor color) {
		this.tooltip = new TooltipChip(user, color);
		this.selectedGeos = new ArrayList<>();
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
		if (geos.isEmpty()) {
			tooltip.hide();
		} else {
			showTooltipBy((AppW) view.getApplication(), geos.get(0));
		}

		if (geos.size() == 1) {
			GeoElement geo = geos.get(0);
			Drawable d = (Drawable) view.getDrawableFor(geo);
			if (d instanceof HasTransformation) {
				RectangleTransformable transformableGeo = (RectangleTransformable) geo;
				graphics.saveTransform();
				graphics.transform(((HasTransformation) d).getTransform());
				graphics.draw(AwtFactory.getPrototype().newRectangle(
						(int) transformableGeo.getWidth(),
						(int) transformableGeo.getHeight()
				));
				graphics.restoreTransform();
			} else if (d != null && !drawAsBackground(d)) {
				GRectangle2D bounds = d.getBoundsForStylebarPosition();
				if (bounds != null) {
					graphics.draw(bounds);
				}
			}
		} else if (geos.size() > 1) {
			graphics.draw(view.getEuclidianController().calculateBounds(geos));
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

	private void showTooltipBy(AppW app, GeoElement geo) {
		DrawableND drawable = app.getActiveEuclidianView().getDrawableFor(geo);
		if (drawable != null) {
			GRectangle2D bounds = drawable.getBoundsForStylebarPosition();
			if (bounds != null) {
				double x = bounds.getMaxX() + getOffsetX(app);
				double y = bounds.getMinY() + getOffsetY(app);
				app.getAppletFrame().add(tooltip);
				tooltip.show(x, y);
			}
		}
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

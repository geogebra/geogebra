package org.geogebra.common.gui.stylebar;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;

import com.google.j2objc.annotations.Weak;

/**
 * dynamic stylebar positioner logic, also used for preview point popup
 *
 */
public class StylebarPositioner {

	private static final int MARGIN = 4;
	@Weak
	private final App app;
	/**
	 * euclidian view
	 */
	@Weak
	protected final EuclidianView euclidianView;
	@Weak
	private final SelectionManager selectionManager;
	private boolean center;

	/**
	 * @param app
	 *            The instance of the App class.
	 */
	public StylebarPositioner(App app) {
		this.app = app;
		euclidianView = app.getActiveEuclidianView();
		selectionManager = app.getSelectionManager();
	}

	/**
	 * @param center
	 *            true if should be center positioned
	 */
	public void setCenter(boolean center) {
		this.center = center;
	}

	private boolean hasVisibleGeos(List<GeoElement> geoList) {
		for (GeoElement geo : geoList) {
			if (isVisible(geo)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasVisibleGeosInHits(List<GeoElement> geoList) {
		for (GeoElement geo : geoList) {
			if (isVisible(geo) && euclidianView.getHits().contains(geo)) {
				return true;
			}
		}
		return false;
	}

	private boolean isVisible(GeoElement geo) {
		return geo.isVisibleInView(euclidianView.getViewID())
				&& geo.isEuclidianVisible() && !geo.isAxis();
	}

	private List<GeoElement> createActiveGeoList() {
		List<GeoElement> selectedGeos = selectionManager.getSelectedGeos();
		List<GeoElement> justCreatedGeos = euclidianView
				.getEuclidianController().getJustCreatedGeos();
		boolean selectedGeosVisible = euclidianView.checkHitForStylebar()
				? hasVisibleGeosInHits(selectedGeos)
				: hasVisibleGeos(selectedGeos);
		if (selectedGeosVisible || hasVisibleGeos(justCreatedGeos)) {
			selectedGeos.addAll(justCreatedGeos);
			return selectedGeos;
		}
		return Collections.emptyList();
	}

	@SuppressWarnings({ "MethodWithTooManyParameters", "OverlyComplexMethod",
			"OverlyLongMethod", "ReturnOfNull" })
	private GPoint getStylebarPositionForDrawable(GRectangle2D gRectangle2D,
			boolean hasBoundingBox, boolean isPoint, boolean noUseOfRectangle,
			int popupHeight, int popupWidth, GRectangle canvasRect) {
		boolean functionOrLine = noUseOfRectangle || gRectangle2D == null;

		int minXPosition = (int) Math.round(canvasRect.getX());
		int maxXPosition = (int) Math
				.round(canvasRect.getX() + canvasRect.getWidth());
		int minYPosition = (int) Math.round(canvasRect.getY());
		int maxYPosition = (int) Math
				.round(canvasRect.getY() + canvasRect.getHeight());

		// final int BOTTOM_MARGIN = 7 * MARGIN;
		double top;

		if (functionOrLine) {
			GPoint mouseLoc;
			mouseLoc = euclidianView.getEuclidianController().getMouseLoc();
			if (mouseLoc == null) {
				return null;
			}
			top = mouseLoc.y + MARGIN;
		} else if (isPoint) {
			top = gRectangle2D.getMaxY() /* + MARGIN */;
		} else {
			if (hasBoundingBox) {
				top = gRectangle2D.getMinY() - popupHeight /*- BOTTOM_MARGIN*/;
			} else {
				top = gRectangle2D.getMinY();
			}
		}

		if (top < minYPosition) {
			top = (gRectangle2D != null ? gRectangle2D.getMaxY()
					: 0) /* + MARGIN */;
		}

		if (top > maxYPosition) {
			if (isPoint) {
				top = gRectangle2D.getMinY() - popupHeight /*- BOTTOM_MARGIN*/;
			} else {
				top = maxYPosition;
			}
		}

		double left;
		if (functionOrLine) {
		    left = euclidianView.getEuclidianController().getMouseLoc().x + MARGIN;
		} else {
			if (isPoint) {
				left = center
						? (gRectangle2D.getMaxX() + gRectangle2D.getMinX()) / 2
								- ((double) popupWidth / 2)
						: gRectangle2D.getMaxX();
			} else {
				left = gRectangle2D.getMaxX();
			}
		}

		left = left < minXPosition ? minXPosition : left;
		left = left > maxXPosition ? maxXPosition : left;
		return new GPoint((int) left, (int) top);
	}

	/**
	 * Calculates the position of the dynamic stylebar on the EuclidianView
	 * 
	 * @param stylebarHeight
	 *            The height of the stylebar.
	 * @param minYPosition
	 *            The minimum y position on the canvas for the top of the
	 *            stylebar.
	 * @param maxYPosition
	 *            The maximum y position on the canvas for the top of the
	 *            stylebar. The top of the stylebar is allowed to be at this
	 *            position, so if the entire stylebar should be on the canvas
	 *            then the height of the stylebar should be already subtracted
	 *            from this value.
	 * @return Returns a GPoint which contains the x and y coordinates for the
	 *         top of the stylebar.
	 */
	@SuppressWarnings({ "WeakerAccess", "unused" })
	public GPoint getPositionOnCanvas(int stylebarHeight, int minYPosition,
			int maxYPosition) {
		return getPositionOnCanvas(stylebarHeight, 0, getGRectangle(0,
				minYPosition, Integer.MAX_VALUE, maxYPosition));
	}

	/**
	 * Calculates the position of the dynamic stylebar on the EuclidianView
	 * 
	 * @param stylebarHeight
	 *            The height of the stylebar.
	 * @param minYPosition
	 *            The minimum y position on the canvas for the top of the
	 *            stylebar.
	 * @param maxYPosition
	 *            The maximum y position on the canvas for the top of the
	 *            stylebar. The top of the stylebar is allowed to be at this
	 *            position, so if the entire stylebar should be on the canvas
	 *            then the height of the stylebar should be already subtracted
	 *            from this value.
	 * @param minXPosition
	 *            The minimum x position on the canvas for the left end of the
	 *            stylebar.
	 * @param maxXPosition
	 *            The maximum x position on the canvas for the left end of the
	 *            stylebar. The left end of the stylebar is allowed to be on
	 *            this position, so if the entire stylebar should be on the
	 *            canvas then the width of the stylebar should be already
	 *            subtracted from this value.
	 * @return Returns a GPoint which contains the x and y coordinates for the
	 *         top of the stylebar.
	 */
	@SuppressWarnings({ "WeakerAccess", "SameParameterValue", "unused" })
	public GPoint getPositionOnCanvas(int stylebarHeight, int minYPosition,
			int maxYPosition, int minXPosition, int maxXPosition) {
		return getPositionOnCanvas(stylebarHeight, 0, getGRectangle(
				minXPosition, minYPosition, maxXPosition, maxYPosition));
	}

	/**
	 * Calculates the position of the dynamic stylebar on the EuclidianView
	 * 
	 * @param stylebarHeight
	 *            The height of the stylebar.
	 * @param stylebarWidth
	 *            The width of the stylebar.
	 * @param canvasRect
	 *            The rectangle on the euclidian view where the top-left corner
	 *            of the stylebar is allowed to be. If the whole stylebar should
	 *            be on the euclidian view then the width and the height of the
	 *            stylebar should be already subtracted from the rectangle's
	 *            dimensions.
	 * @return Returns a GPoint which contains the x and y coordinates for the
	 *         top of the stylebar.
	 */
	@SuppressWarnings("WeakerAccess")
	@Nullable
	public GPoint getPositionOnCanvas(int stylebarHeight, int stylebarWidth,
			GRectangle canvasRect) {
		List<GeoElement> activeGeoList = createActiveGeoList();
		if (activeGeoList.isEmpty()) {
			return null;
		}

		if (app.getConfig().hasPreviewPoints()) {
			GeoElement selectedPreviewPoint = getSelectedPreviewPoint();
			if (selectedPreviewPoint != null) {
				return getPositionFor(selectedPreviewPoint, stylebarHeight,
						stylebarWidth, canvasRect);
			}
		}

		GeoElement geo = activeGeoList.get(0);
		if (geo.isEuclidianVisible()) {
			if (geo instanceof GeoFunction) {
				return getPositionForFunction(geo, stylebarHeight,
						stylebarWidth, canvasRect);
			}
			return getPositionFor(geo, stylebarHeight, stylebarWidth,
					canvasRect);
		}
		return null;
	}

	/**
	 * Returns the position of the popup for the first element of the geoList.
	 *
	 * This method is deprecated, use the getPositionFor(GeoElement geo, int
	 * stylebarHeight, int stylebarWidth, GRectangle canvasRect) method instead!
	 *
	 * @param geoList
	 *            selected geos
	 * @param stylebarHeight
	 *            height of stylebar
	 * @param minYPosition
	 *            min y pos of popup
	 * @param maxYPosition
	 *            max y pos of popup
	 * @param minXPosition
	 *            min x pos of popup
	 * @param maxXPosition
	 *            max x pos of popup
	 * @return position of popup
	 */
	@Deprecated
	@SuppressWarnings({ "unused", "MethodWithTooManyParameters", "deprecation",
			"ReturnOfNull" })
	public GPoint getPositionFor(List<GeoElement> geoList, int stylebarHeight,
			int minYPosition, int maxYPosition, int minXPosition,
			int maxXPosition) {
		if (geoList != null && !geoList.isEmpty()) {
			return getPositionFor(geoList.get(0), stylebarHeight, 0,
					getGRectangle(minXPosition, minYPosition, maxXPosition,
							maxYPosition));
		}
		return null;
	}

	protected GRectangle getGRectangle(int minX, int minY, int maxX, int maxY) {
		return AwtFactory.getPrototype().newRectangle(minX, minY, maxX - minX,
				maxY - minY);
	}

	private GeoElement getSelectedPreviewPoint() {
		List<GeoElement> visiblePreviewPoints = app.getSpecialPointsManager()
				.getSelectedPreviewPoints();
		if (visiblePreviewPoints != null && !visiblePreviewPoints.isEmpty()) {
			for (GeoElement previewPoint : visiblePreviewPoints) {
				if (euclidianView.getHits().contains(previewPoint)) {
					return previewPoint;
				}
			}
		}
		return null;
	}

	/**
	 * @param geo
	 *            geoElement
	 * @param stylebarHeight
	 *            height of stylebar
	 * @param stylebarWidth
	 *            width of stylebar
	 * @param canvasRect
	 *            canvas
	 * @return position
	 */
	@SuppressWarnings("WeakerAccess")
	public GPoint getPositionFor(GeoElement geo, int stylebarHeight,
			int stylebarWidth, GRectangle canvasRect) {
		DrawableND dr = euclidianView.getDrawableND(geo);
		if (dr != null) {
			return getStylebarPositionForDrawable(
					dr.getBoundsForStylebarPosition(),
					!(dr instanceof DrawLine), dr instanceof DrawPoint, dr.is3D(),
					stylebarHeight, stylebarWidth, canvasRect);
		}
		// noinspection ReturnOfNull
		return null;
	}

	private GPoint getPositionForFunction(GeoElement geo, int stylebarHeight,
			int stylebarWidth, GRectangle canvasRect) {
		if (euclidianView.getHits().contains(geo)) {
			return getStylebarPositionForDrawable(null, true, false,
					true, stylebarHeight, stylebarWidth, canvasRect);
		} else {
			// with select tool, it happens that first selected geo is a
			// function, and then
			// the user select another geo (e.g. a point). Then we still want to
			// show style bar.
			if (app.getMode() == EuclidianConstants.MODE_SELECT) {
				DrawableND dr = euclidianView.getDrawableND(geo);
				if (dr != null) {
					return getStylebarPositionForDrawable(
							dr.getBoundsForStylebarPosition(),
							!(dr instanceof DrawLine), false, true,
							stylebarHeight, stylebarWidth, canvasRect);
				}
			}
		}
		return null;
	}
}

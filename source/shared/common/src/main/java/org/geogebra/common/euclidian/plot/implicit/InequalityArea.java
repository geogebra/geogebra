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

package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.implicit.classification.ClassifiedRegion;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.util.debug.Log;

final class InequalityArea {
	private final ViewportPanZoomDelta panZoom;
	private GArea area;

	InequalityArea(EuclidianViewBounds bounds) {
		panZoom = new ViewportPanZoomDelta(bounds);
		area = AwtFactory.getPrototype().newArea();
	}

	void update(List<ClassifiedRegion> results) {
		update(results, null);
	}

	void update(List<ClassifiedRegion> results, EuclidianViewBounds bounds) {
		if (panZoom.isActive()) {
			Log.debug("PanZoom is active");
			return;
		}
		final long totalStart = ImplicitPlotTimings.start();
		GArea filled = AwtFactory.getPrototype().newArea();
		AreaAssemblyStats stats = new AreaAssemblyStats();
		int filledCount = 0;
		int holeCount = 0;
		for (ClassifiedRegion region : results) {
			if (!region.isFilled()) {
				continue;
			}
			filledCount++;
			GGeneralPath outerBoundary = region.getOuterBoundary();
			long stageStart = ImplicitPlotTimings.start();
			GArea regionArea = AwtFactory.getPrototype().newArea(outerBoundary);
			stats.createRegionAreasElapsed += ImplicitPlotTimings.delta(stageStart);
			for (GGeneralPath hole : region.getHoles()) {
				holeCount++;
				stageStart = ImplicitPlotTimings.start();
				regionArea.subtract(AwtFactory.getPrototype().newArea(hole));
				stats.subtractHolesElapsed += ImplicitPlotTimings.delta(stageStart);
			}
			subtractExplicitFalseRegions(regionArea, results, region, bounds, stats);
			stageStart = ImplicitPlotTimings.start();
			filled.add(regionArea);
			stats.addFilledRegionsElapsed += ImplicitPlotTimings.delta(stageStart);
		}
		area = filled;
		panZoom.snapshot();
		ImplicitPlotTimings.log("InequalityArea.createRegionAreas",
				elapsedStart(stats.createRegionAreasElapsed),
				"regions=" + results.size() + " filled=" + filledCount);
		ImplicitPlotTimings.log("InequalityArea.subtractHoles",
				elapsedStart(stats.subtractHolesElapsed),
				"holes=" + holeCount);
		ImplicitPlotTimings.log("InequalityArea.subtractExplicitFalseRegions",
				elapsedStart(stats.subtractExplicitFalseRegionsElapsed),
				"falseRegionsChecked=" + stats.falseRegionsChecked
						+ " falseRegionsSubtracted=" + stats.falseRegionsSubtracted);
		ImplicitPlotTimings.log("InequalityArea.addFilledRegions",
				elapsedStart(stats.addFilledRegionsElapsed),
				"filled=" + filledCount);
		ImplicitPlotTimings.log("InequalityArea.areaAssembly", totalStart,
				"regions=" + results.size()
						+ " filled=" + filledCount
						+ " holes=" + holeCount
						+ " falseRegionsChecked=" + stats.falseRegionsChecked
						+ " falseRegionsSubtracted=" + stats.falseRegionsSubtracted);
	}

	private void subtractExplicitFalseRegions(GArea filledRegionArea,
			List<ClassifiedRegion> regions, ClassifiedRegion filledRegion,
			EuclidianViewBounds bounds, AreaAssemblyStats stats) {
		long stageStart = ImplicitPlotTimings.start();
		if (bounds == null) {
			stats.subtractExplicitFalseRegionsElapsed += ImplicitPlotTimings.delta(stageStart);
			return;
		}
		for (ClassifiedRegion region : regions) {
			if (region == filledRegion || region.isFilled()
					|| region.getOuterBoundary() == null) {
				continue;
			}
			stats.falseRegionsChecked++;
			GPoint2D samplePoint = region.getSamplePoint();
			if (samplePoint == null || !filledRegionArea.contains(
					bounds.toScreenCoordXd(samplePoint.x),
					bounds.toScreenCoordYd(samplePoint.y))) {
				continue;
			}
			filledRegionArea.subtract(areaOf(region));
			stats.falseRegionsSubtracted++;
		}
		stats.subtractExplicitFalseRegionsElapsed += ImplicitPlotTimings.delta(stageStart);
	}

	private static final class AreaAssemblyStats {
		private long createRegionAreasElapsed;
		private long subtractHolesElapsed;
		private long subtractExplicitFalseRegionsElapsed;
		private long addFilledRegionsElapsed;
		private int falseRegionsChecked;
		private int falseRegionsSubtracted;
	}

	private long elapsedStart(long elapsed) {
		return ImplicitPlotTimings.start() - elapsed;
	}

	private GArea areaOf(ClassifiedRegion region) {
		GArea regionArea = AwtFactory.getPrototype().newArea(region.getOuterBoundary());
		for (GGeneralPath hole : region.getHoles()) {
			regionArea.subtract(AwtFactory.getPrototype().newArea(hole));
		}
		return regionArea;
	}

	boolean needsUpdate() {
		return false;
	}

	void applyTransformations() {
		area = panZoom.applyTo(area);
	}

	GArea getFilledArea() {
		return area;
	}

	void onMoveStop() {
		panZoom.snapshot();
	}

	void onZoomStop() {
		// not used;
	}

}

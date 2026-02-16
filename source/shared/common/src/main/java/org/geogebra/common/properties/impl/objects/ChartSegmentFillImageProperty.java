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

package org.geogebra.common.properties.impl.objects;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.ChartStyleGeo;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractImageProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.util.ImageManager;

/**
 * {@code Property} responsible for setting the image
 * used for image-style filling in pie and bar charts.
 */
public final class ChartSegmentFillImageProperty extends AbstractImageProperty
		implements GeoElementDependentProperty, ChartSegmentSelectionDependentProperty {
	private final ChartStyleGeo chartStyleGeo;
	private final ChartSegmentSelection chartSegmentSelection;

	/**
	 * Constructs the property.
	 * @param localization localization for translating property names
	 * @param geoElement the element to create the property for
	 * @param chartSegmentSelection the selection from which to read the selected bar/slice index
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public ChartSegmentFillImageProperty(Localization localization, ImageManager imageManager,
			GeoElement geoElement, ChartSegmentSelection chartSegmentSelection)
			throws NotApplicablePropertyException {
		super(localization, imageManager, "Image");
		if (!(geoElement instanceof ChartStyleGeo chartStyleGeo)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.chartStyleGeo = chartStyleGeo;
		this.chartSegmentSelection = chartSegmentSelection;
	}

	@Override
	protected @CheckForNull String getImagePath() {
		return chartSegmentSelection.getUniformValueOrNull(chartStyleGeo.getIntervals(),
				index -> chartStyleGeo.getStyle().getBarImage(index));
	}

	@Override
	protected void setImagePath(@CheckForNull String path) {
		String resolvedPath = path != null ? path : "";
		chartSegmentSelection.forEachSelectedSegment(chartStyleGeo.getIntervals(), index -> {
			chartStyleGeo.getStyle().setBarImage(resolvedPath, index);
			chartStyleGeo.getStyle().setBarAlpha(resolvedPath.isEmpty() ? 0d : 1d, index);
		});
		((GeoElement) chartStyleGeo).updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public boolean isAvailable() {
		return chartSegmentSelection.getUniformValueOrNull(chartStyleGeo.getIntervals(),
				index -> chartStyleGeo.getStyle().getBarFillType(index)) == FillType.IMAGE;
	}

	@Override
	public GeoElement getGeoElement() {
		return (GeoElement) chartStyleGeo;
	}

	@Override
	public ChartSegmentSelection getChartSegmentSelection() {
		return chartSegmentSelection;
	}
}

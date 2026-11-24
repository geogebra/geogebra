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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.properties.impl.objects.BarChartDataPropertyFactory.BarChartData;
import org.geogebra.common.properties.impl.objects.BoxPlotDataPropertyFactory.BoxPlotData;
import org.geogebra.common.properties.impl.objects.HistogramDataPropertyFactory.HistogramData;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;

/** Temporary values shared by reconstructed properties for one element. */
public final class ElementSettingsData {
	final HistogramData histogram = new HistogramData();
	final BoxPlotData boxPlot = new BoxPlotData();
	final BarChartData barChart = new BarChartData();
	final ChartSegmentSelection styleSelection = new ChartSegmentSelection();
	final ChartSegmentSelection fillingSelection = new ChartSegmentSelection();
	FillType previousPatternFillType = FillType.STANDARD;
	final Map<Integer, FillType> previousSegmentPatterns = new HashMap<>();
	ScriptEvent selectedScriptEvent = ScriptEvent.OnClick;
	final Map<ScriptEvent, ScriptType> scriptLanguages = new EnumMap<>(ScriptEvent.class);
}

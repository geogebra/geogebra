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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.ChartStyleGeo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for selecting a bar or a slice for a bar chart or pie chart
 * respectively (to be used for independent settings).
 */
public class ChartSegmentSelectionProperty extends AbstractNamedEnumeratedProperty<Integer> {
	private final GeoElement geoElement;
	private final ChartSegmentSelection chartSegmentSelection;

	/**
	 * Owner of the selected bar/slice index of a chart, enabling passing between {@code Property}s,
	 * updating via {@code ChartSegmentSelectionProperty}, reading by other related
	 * {@code Property}s, and registering/unregistering listeners.
	 */
	public static final class ChartSegmentSelection {
		private int index = 0;
		private final Set<Listener> listeners = new HashSet<>();

		/**
		 * Listener for changes in selection.
		 */
		public interface Listener {
			/**
			 * Method called when the selection changed.
			 */
			void selectedChartUpdated();
		}

		void setIndex(int index) {
			this.index = index;
			listeners.forEach(Listener::selectedChartUpdated);
		}

		/**
		 * @return the index of the selected bar/slice (indexed from 1),
		 * or {@code 0} if all bars/slices are selected
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * Register a listener for selection change notifications.
		 * @param listener the listener to be registered
		 */
		public void registerListener(Listener listener) {
			listeners.add(listener);
		}

		/**
		 * Unregisters a previously registered listener,
		 * undoing the effects of {@link ChartSegmentSelection#registerListener}.
		 * @param listener the listener to be unregistered
		 */
		public void unregisterListener(Listener listener) {
			listeners.remove(listener);
		}
	}

	/**
	 * Constructs the property for the given element.
	 * @param localization localization for translating property names
	 * @param geoElement the element to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public ChartSegmentSelectionProperty(Localization localization, GeoElement geoElement,
			ChartSegmentSelection chartSegmentSelection)
			throws NotApplicablePropertyException {
		super(localization, "Selection");
		if (!(geoElement instanceof ChartStyleGeo)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoElement = geoElement;
		this.chartSegmentSelection = chartSegmentSelection;
	}

	@Override
	public @Nonnull List<Integer> getValues() {
		int numberOfIntervals = ((ChartStyleGeo) geoElement).getIntervals();
		return IntStream.range(0, numberOfIntervals + 1).boxed().collect(Collectors.toList());
	}

	@Override
	public String[] getValueNames() {
		int numberOfIntervals = ((ChartStyleGeo) geoElement).getIntervals();
		String firstValueTransKey = geoElement instanceof GeoPieChart ? "AllSlices" : "AllBars";
		String valueTransKey = geoElement instanceof GeoPieChart ? "SliceA" : "BarA";
		return Stream.concat(
				Stream.of(getLocalization().getMenu(firstValueTransKey)),
				IntStream.rangeClosed(1, numberOfIntervals).mapToObj(index ->
						getLocalization().getPlain(valueTransKey, String.valueOf(index)))
		).toArray(String[]::new);
	}

	@Override
	protected void doSetValue(Integer value) {
		chartSegmentSelection.setIndex(value);
	}

	@Override
	public Integer getValue() {
		return chartSegmentSelection.getIndex();
	}
}

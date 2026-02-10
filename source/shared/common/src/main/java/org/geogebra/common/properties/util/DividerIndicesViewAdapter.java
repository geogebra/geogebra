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

package org.geogebra.common.properties.util;

import java.util.Arrays;

import org.geogebra.common.properties.EnumeratedProperty;

/**
 * This is a utility class that provides methods for displaying data with dividers for a
 * grouped enumerated property.
 * <p>It allows converting between model indices
 * (corresponding to a valid position in {@link EnumeratedProperty#getValues()})
 * and view indices (corresponds to a valid position with an array containing values and dividers
 * together see {@link EnumeratedProperty#getGroupDividerIndices()}).</p>
 */
public final class DividerIndicesViewAdapter {

	private final int[] dividerIndices;
	private final int valueCount;

	/**
	 * Constructs an adapter for a given divider indices and value count.
	 * @param dividerIndices divider indices
	 * @param valueCount number of values
	 */
	public DividerIndicesViewAdapter(int[] dividerIndices, int valueCount) {
		this.dividerIndices = Arrays.copyOf(dividerIndices, dividerIndices.length);
		this.valueCount = valueCount;
		offsetIndices();
	}

	private void offsetIndices() {
		for (int i = 1; i < dividerIndices.length; i++) {
			dividerIndices[i] += i;
		}
	}

	/**
	 * Gets the number of total views, model value count + the number of dividers.
	 * @return view count
	 */
	public int getViewCount() {
		return valueCount + dividerIndices.length;
	}

	/**
	 * Returns whether there is a divider or not at a given view index.
	 * @param viewIndex view index to test
	 * @return <code>true</code> if there is a divider at given index, <code>false</code> otherwise
	 */
	public boolean isDivider(int viewIndex) {
		for (int i = 0; i < dividerIndices.length; i++) {
			if (dividerIndices[i] == viewIndex) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Converts view index to a model index. View index must be
	 * between 0 and {@link DividerIndicesViewAdapter#getViewCount()}.
	 * @param viewIndex corresponds to a valid index in the array of values and dividers
	 * @return model index, that corresponds to a valid index in the array of values
	 */
	public int convertViewIndexToModel(int viewIndex) {
		int insertPosition = Math.abs(Arrays.binarySearch(dividerIndices, viewIndex));
		return viewIndex - insertPosition + 1;
	}

	/**
	 * Converts model index to a view index. Model index must be
	 * between 0 and <code>valueCount</code> passed to the constructor.
	 * @param modelIndex corresponds to a valid index in the array of values
	 * @return view index, that corresponds to a valid index in the array of values and dividers
	 */
	public int convertModelIndexToView(int modelIndex) {
		int viewIndex = modelIndex;
		for (int i = 0; i < dividerIndices.length; i++) {
			if (dividerIndices[i] <= viewIndex) {
				viewIndex += 1;
			}
		}
		return viewIndex;
	}
}

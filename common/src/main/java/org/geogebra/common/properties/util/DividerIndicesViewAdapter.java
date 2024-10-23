package org.geogebra.common.properties.util;

import java.util.Arrays;

import org.geogebra.common.properties.EnumeratedProperty;
import org.geogebra.common.properties.GroupedEnumeratedProperty;

/**
 * This is a utility class that provides methods for displaying data with dividers for a
 * grouped enumerated property. <p></p>It allows converting between model indices
 * (corresponding to a valid position in {@link EnumeratedProperty#getValues()})
 * and view indices (corresponds to a valid position with an array containig values and dividers
 * together {see @link GroupedEnumeratedProperty#getGroupDividerIndices()}).
 */
public final class DividerIndicesViewAdapter {

	private final int[] dividerIndicies;
	private final int valueCount;

	/**
	 * Constructs an adapter for a GroupedEnumeratedProperty.
	 * @param property property to construct adapter for
	 */
	public DividerIndicesViewAdapter(GroupedEnumeratedProperty<?> property) {
		this(property.getGroupDividerIndices(), property.getValues().size());
	}

	/**
	 * Constructs an adapter for a given divider indices and value count.
	 * @param dividerIndices divider indices
	 * @param valueCount number of values
	 */
	public DividerIndicesViewAdapter(int[] dividerIndices, int valueCount) {
		this.dividerIndicies = Arrays.copyOf(dividerIndices, dividerIndices.length);
		this.valueCount = valueCount;
		offsetIndices();
	}

	private void offsetIndices() {
		for (int i = 1; i < dividerIndicies.length; i++) {
			dividerIndicies[i] += i;
		}
	}

	/**
	 * Gets the number of total views, model value count + the number of dividers.
	 * @return view count
	 */
	public int getViewCount() {
		return valueCount + dividerIndicies.length;
	}

	/**
	 * Returns whether there is a divider or not at a given view index.
	 * @param viewIndex view index to test
	 * @return <code>true</code> if there is a divider at given index, <code>false</code> otherwise
	 */
	public boolean isDivider(int viewIndex) {
		for (int i = 0; i < dividerIndicies.length; i++) {
			if (dividerIndicies[i] == viewIndex) {
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
		int insertPosition = Math.abs(Arrays.binarySearch(dividerIndicies, viewIndex));
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
		for (int i = 0; i < dividerIndicies.length; i++) {
			if (dividerIndicies[i] <= viewIndex) {
				viewIndex += 1;
			}
		}
		return viewIndex;
	}
}

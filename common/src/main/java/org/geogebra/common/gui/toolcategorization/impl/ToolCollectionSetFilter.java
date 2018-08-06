package org.geogebra.common.gui.toolcategorization.impl;

import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Filters tools that are in a set.
 */
public class ToolCollectionSetFilter implements ToolCollectionFilter {

	private Set<Integer> excludeSet;

	/**
	 * Constructs a filter. This filters based on a set.
	 *
	 * @param excludeSet tools that should be excluded
	 */
	public ToolCollectionSetFilter(Set<Integer> excludeSet) {
		this.excludeSet = excludeSet;
	}

	/**
	 * Calls {@link ToolCollectionSetFilter#ToolCollectionSetFilter(Set)}.
	 * @param excludeTools tools that should be excluded
	 */
	public ToolCollectionSetFilter(Integer... excludeTools) {
		this(new HashSet<Integer>(Arrays.asList(excludeTools)));
	}

	@Override
	public boolean filter(int tool) {
		return !excludeSet.contains(tool);
	}
}

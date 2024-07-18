package org.geogebra.common.gui.toolcategorization.impl;

import java.util.Set;

import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;

/**
 * Filters tools that are in a set.
 */
public class ToolCollectionSetFilter implements ToolCollectionFilter {

	private Set<Integer> set;
	private boolean include = false;

	/**
	 * Constructs a filter based on a set of tool IDs to exclude.
	 *
	 * @param excludeSet tools that should be excluded
	 */
	public ToolCollectionSetFilter(Set<Integer> excludeSet) {
		this.set = excludeSet;
	}

	/**
	 * Calls {@link ToolCollectionSetFilter#ToolCollectionSetFilter(Set)}.
	 * @param excludeTools tools that should be excluded
	 */
	public ToolCollectionSetFilter(Integer... excludeTools) {
		this(Set.of(excludeTools));
	}

	/**
	 * Set whether this *set* contains tools
	 * that should be included or excluded.
	 * @param include true if the set contains tools that should
	 *                be included, false otherwise.
	 */
	public void setInclude(boolean include) {
		this.include = include;
	}

	@Override
	public boolean isIncluded(int tool) {
		return set.contains(tool) == include;
	}
}

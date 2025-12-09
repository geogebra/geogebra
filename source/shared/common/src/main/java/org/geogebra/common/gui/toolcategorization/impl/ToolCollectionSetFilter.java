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

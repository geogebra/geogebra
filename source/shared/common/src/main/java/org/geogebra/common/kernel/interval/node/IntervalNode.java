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

package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.interval.Interval;

/**
 * Interface for nodes and leaves in interval expression tree.
 */
public interface IntervalNode {
	/**
	 *
	 * @return if node is a leaf.
	 */
	boolean isLeaf();

	/**
	 *
	 * @return node as IntervalExpressionNode if it is, null otherwise.
	 */
	IntervalExpressionNode asExpressionNode();

	/**
	 *
	 * @return the evaluated value of the node.
	 */
	Interval value();

	/**
	 *
	 * @return if node or its subtrees have function variable.
	 */
	boolean hasFunctionVariable();

	@MissingDoc
	IntervalNode simplify();
}

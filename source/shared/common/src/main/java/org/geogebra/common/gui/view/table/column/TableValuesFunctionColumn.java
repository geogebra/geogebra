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

package org.geogebra.common.gui.view.table.column;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.util.DoubleUtil;

public class TableValuesFunctionColumn extends AbstractTableValuesColumn {

	private final GeoEvaluatable evaluatable;
	private final GeoList values;

	/**
	 * Creates a function column
	 * @param evaluatable function
	 * @param values values to evaluate function at
	 */
	public TableValuesFunctionColumn(GeoEvaluatable evaluatable, GeoList values) {
		super(evaluatable);
		this.evaluatable = evaluatable;
		this.values = values;
	}

	@Override
	protected double calculateValue(int row) {
		if (values.size() <= row) {
			return Double.NaN;
		}
		double xValue = DoubleUtil.checkDecimalFraction(values.get(row).evaluateDouble());
		return evaluatable.value(xValue);
	}

	@Override
	protected String getHeaderName() {
		return evaluatable.getCaption(StringTemplate.defaultTemplate) + "(x)";
	}
}

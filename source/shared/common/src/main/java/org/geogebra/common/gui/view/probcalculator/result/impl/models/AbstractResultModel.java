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

package org.geogebra.common.gui.view.probcalculator.result.impl.models;

import org.geogebra.common.gui.view.probcalculator.result.EditableResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.ResultModel;

public abstract class AbstractResultModel implements ResultModel {

	static final String PLUS_SIGN = " + ";
	static final String EQUALS_SIGN = " = ";

	/**
	 * @return editable entry for lower bound
	 */
	public abstract EditableResultEntry getLow();

	/**
	 * @return editable entry for upper bound
	 */
	public abstract EditableResultEntry getHigh();

	/**
	 * @return editable entry for result
	 */
	public abstract EditableResultEntry getResult();

	/**
	 * @param low lower bound value
	 */
	public abstract void setLow(String low);

	/**
	 * @param high upper bound value
	 */
	public abstract void setHigh(String high);

	/**
	 * @param result result value
	 */
	public abstract void setResult(String result);
}

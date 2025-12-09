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

package org.geogebra.common.exam.restrictions;

public enum ExamFeatureRestriction {

	/** APPS_5751 */
	AUTOMATIC_GRAPH_SELECTION_FOR_FUNCTIONS,

	/** APPS-5926, APPS-6088, APPS-6315 */
	HIDE_CALCULATED_EQUATION,

	/** APPS-6308 */
	HIDE_SPECIAL_POINTS,

	/** APPS-6088 */
	RESTRICT_CHANGING_EQUATION_FORM,

	/** APPS-5929 */
	RATIONALIZATION,

	/** APPS-5929 */
	SURD,

	/** APPS-6310 */
	SPREADSHEET,

	/** APPS-6312 */
	CUSTOM_MMS_REGRESSION_MODELS,

	/** APPS-6519 */
	DISABLE_MIXED_NUMBERS
}

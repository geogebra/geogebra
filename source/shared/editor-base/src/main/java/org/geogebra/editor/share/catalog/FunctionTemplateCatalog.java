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

package org.geogebra.editor.share.catalog;

import static org.geogebra.editor.share.catalog.Parameter.createDownParameter;
import static org.geogebra.editor.share.catalog.Parameter.createUpParameter;

import java.util.Arrays;
import java.util.List;

/**
 * Catalog of built-in function templates (fractions, roots, integrals, etc).
 */
class FunctionTemplateCatalog {

	List<FunctionTemplate> createGeneralFunctions() {
		return Arrays.asList(
				new FunctionTemplate(Tag.SUBSCRIPT),

				new FunctionTemplate(Tag.SUPERSCRIPT),

				new FunctionTemplate(Tag.RECURRING_DECIMAL),

				new FunctionTemplate(Tag.FRAC, "\\frac", createDownParameter(1),
						createUpParameter(0)),

				new FunctionTemplate(Tag.SQRT, "\\sqrt", Parameter.BASIC),

				new FunctionTemplate(Tag.CBRT, "\\sqrt[3]", Parameter.BASIC),

				new FunctionTemplate(Tag.NROOT, "\\sqrt",
						new Parameter[]{createDownParameter(0), createUpParameter(1)}, 1),

				new FunctionTemplate(Tag.LOG, "\\log",
						new Parameter[]{Parameter.BASIC, Parameter.BASIC}, 1),

				new FunctionTemplate(Tag.DEF_INT, "\\int",
						new Parameter[]{createUpParameter(1), createDownParameter(0)}, 2),

				new FunctionTemplate(Tag.LIM_EQ, "\\lim", new Parameter[]{
						Parameter.BASIC}, 1),

				new FunctionTemplate(Tag.PROD_EQ, "\\prod",
						new Parameter[]{createUpParameter(1), createDownParameter(0)}, 2),

				new FunctionTemplate(Tag.SUM_EQ, "\\sum",
						new Parameter[]{createUpParameter(1), createDownParameter(0)}, 2),

				new FunctionTemplate(Tag.VEC, "\\vec", Parameter.BASIC),

				new FunctionTemplate(Tag.ATOMIC_PRE, "\\atomicpre", createUpParameter(1),
						createDownParameter(0), Parameter.BASIC),

				new FunctionTemplate(Tag.ATOMIC_POST, "\\atomicpost",
						Parameter.BASIC, createUpParameter(2), createDownParameter(1)),

				new FunctionTemplate(Tag.POINT_AT, "", Parameter.BASIC, Parameter.BASIC),

				new FunctionTemplate(Tag.POINT, "", Parameter.BASIC, Parameter.BASIC),

				new FunctionTemplate(Tag.VECTOR, "",
						new Parameter[]{createDownParameter(1), new Parameter(0, 2),
								createUpParameter(1)}, 0, 2),

				new FunctionTemplate(Tag.ABS),

				new FunctionTemplate(Tag.FLOOR),

				new FunctionTemplate(Tag.CEIL)
		);
	}
}

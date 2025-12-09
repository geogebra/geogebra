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

package org.geogebra.common.kernel.arithmetic;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class ExpressionValueTreeIteratorTests extends BaseUnitTest {

	@Test
	public void testIteratorDepthFirst() {
		ValidExpression expression = parseExpression("1 + {(1, 2), 3} + Command(\"string\")");
		List<Class<? extends ExpressionValue>> expectedClasses = List.of(
				ExpressionNode.class, // Full expression
				ExpressionNode.class, // 1 + {(1, 2), 3}
				MySpecialDouble.class, // 1
				MyList.class, // {(1, 2), 3}
				ExpressionNode.class, // List arguments are wrapped
				MyVecNode.class, // (1, 2)
				ExpressionNode.class, // Vector arguments are wrapped
				MySpecialDouble.class, // 1
				ExpressionNode.class, // Vector arguments are wrapped
				MySpecialDouble.class, // 2
				ExpressionNode.class, // List arguments are wrapped
				MySpecialDouble.class, // 3
				ExpressionNode.class, // Command is wrapped
				Command.class, // Command("string")
				ExpressionNode.class, // Command arguments are wrapped
				MyStringBuffer.class // "string"
		);
		List<Class<? extends ExpressionValue>> actualClasses = new ArrayList<>();
		for (ExpressionValue child : expression) {
			actualClasses.add(child.getClass());
		}
		assertIterableEquals(expectedClasses, actualClasses);
	}
}

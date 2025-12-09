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

package org.geogebra.common.kernel.commands;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class CmdCopyFreeObjectTest extends BaseUnitTest {

	@Test
	public void listFunction() {
		add("a=1");
		add("myFunc(x)=3a");
		add("myList={myFunc}");
		add("myCopy=CopyFreeObject(myList)");
		add("a=2");
		t("myCopy", "{(3 * 1)}");
	}

	@Test
	public void listFunctionNVar() {
		add("a=1");
		add("myFunc2(x,y)=3a+x+y");
		add("myList2={myFunc2}");
		add("myCopy2=CopyFreeObject(myList2)");
		add("a=2");
		t("myCopy2", "{(3 * 1) + x + y}");
	}
}

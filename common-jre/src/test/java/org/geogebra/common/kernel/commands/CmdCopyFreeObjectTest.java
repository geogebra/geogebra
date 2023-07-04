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

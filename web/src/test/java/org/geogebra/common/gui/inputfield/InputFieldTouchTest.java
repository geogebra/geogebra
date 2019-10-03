package org.geogebra.common.gui.inputfield;

import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.AppMocker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class InputFieldTouchTest {

	private AppWFull app;
	private GeoInputBox input1;
	private GeoInputBox input2;

	@Before
	public void setUp() {
		app = AppMocker.mockGraphing(InputFieldTouchTest.class);
		input1 = addInputBox("inputbox1");
		input2 = addInputBox("inputbox2");

	}

	@Test
	public  void textShouldApplyTest() {
		Assert.assertEquals("", input1.getText());

	}

	private GeoInputBox addInputBox(String name) {
		String command = name + " = InputBox()";
		GeoElementND[] results = app.getKernel().getAlgebraProcessor().processAlgebraCommand(command,
				false);

		return (GeoInputBox)results[0];
	}

}

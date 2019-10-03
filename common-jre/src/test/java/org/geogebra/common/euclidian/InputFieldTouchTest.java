package org.geogebra.common.euclidian;

import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.test.euclidian.TextFieldCommonJre;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InputFieldTouchTest {

	private static AppCommon3D app;
	private static EuclidianView ev;
	private static EuclidianController ec;

	private GeoInputBox input1;
	private GeoInputBox input2;
	private ViewTextField viewTextField = new TextFieldCommonJre(null);
	@Before
	public void setUp() {
		app = new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
		ev = app.getActiveEuclidianView();
		ev.setViewTextField(viewTextField);
		ec = ev.getEuclidianController();
		input1 = addInputBox("inputbox1");
	}

	@Test
	public  void textShouldApplyTest() {
		DrawInputBox di1 = (DrawInputBox) ev.getDrawableFor(input1);
		ev.getViewTextField().focusTo(di1);
		Assert.assertEquals(input1, viewTextField.getTextField().getInputBox());
	}

	private GeoInputBox addInputBox(String name) {
		String command = name + " = InputBox()";
		GeoElementND[] results = app.getKernel().getAlgebraProcessor().processAlgebraCommand(command,
				false);

		return (GeoInputBox)results[0];
	}

}

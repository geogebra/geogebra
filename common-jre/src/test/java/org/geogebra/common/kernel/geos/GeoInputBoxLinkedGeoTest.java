package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Assert;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoInputBoxLinkedGeoTest extends BaseUnitTest {

	private GeoInputBox inputBox;

	@Override
	public AppCommon createAppCommon() {
		return new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
	}

	@Test
	public void shouldNotShowQuotesForText() {
		setupInput("txt", "\"GeoGebra Rocks\"");
		t("ib", "GeoGebra Rocks");
		updateInput("GeoGebra Really Rocks");
		t("txt", "GeoGebra Really Rocks");
		hasType("txt", GeoClass.TEXT);
	}

	@Test
	public void enteringNewValueShouldKeepVectorType() {
		setupAndCheckInput("v", "(1, 3)");
		t("Rename(v,\"V\")", new String[0]);
		updateInput("(1, 5)");
		t("V", "(1, 5)");
		hasType("V", GeoClass.VECTOR);
	}

	@Test
	public void enteringNewValueShouldKeepVectorType3D() {
		setupAndCheckInput("v3", "(1, 3, 0)");
		updateInput("(1, 5)");
		t("v3", "(1, 5)");
		hasType("v3", GeoClass.VECTOR);
	}

	@Test
	public void enteringNewValueShouldKeepPlaneType() {
		setupAndCheckInput("p", "x + y - z = 0");
		updateInput("x = y");
		t("p", "x - y = 0");
		hasType("p", GeoClass.PLANE3D);
	}

	@Test
	public void enteringIncompatibleTypeShouldBeIgnored() {
		setupAndCheckInput("n", "4");
		getApp().storeUndoInfo();
		updateInput("y");
		t("n", "4");
		hasType("n", GeoClass.NUMERIC);
	}

	@Test
	public void enteringNewValueShouldKeepComplexNumber() {
		setupAndCheckInput("P", "1 + " + Unicode.IMAGINARY);
		updateInput("7");
		t("P", "7 + 0" + Unicode.IMAGINARY);
		Assert.assertEquals("7",
				lookup("P").getDefinition(StringTemplate.defaultTemplate));
		hasType("P", GeoClass.POINT);
	}

	@Test
	public void enteringShortLinearExprShouldKeepLineType() {
		setupAndCheckInput("l", "y = 2x + 3");
		updateInput("3x + 5");
		t("l", "y = 3x + 5");
		hasType("l", GeoClass.LINE);
	}

	@Test
	public void symbolicShouldShowDefinition() {
		setupInput("l", "1 + 1 / 5");
		((GeoNumeric) lookup("l")).setSymbolicMode(true, false);
		inputBox.setSymbolicMode(true, false);
		Assert.assertEquals("1 + 1 / 5", inputBox.getTextForEditor());
		((GeoNumeric) lookup("l")).setSymbolicMode(false, false);
		Assert.assertEquals("1 + 1 / 5", inputBox.getTextForEditor());
	}

	@Test
	public void nonsymbolicShouldShowDefinition() {
		setupInput("l", "1 + 1 / 5");
		((GeoNumeric) lookup("l")).setSymbolicMode(true, false);
		inputBox.setSymbolicMode(false, false);
		Assert.assertEquals("1 + 1 / 5", inputBox.getTextForEditor());
		((GeoNumeric) lookup("l")).setSymbolicMode(false, false);
		Assert.assertEquals("1 + 1 / 5", inputBox.getTextForEditor());
	}

	@Test
	public void shouldShowValueForSimpleNumeric() {
		setupInput("l", "5");
		inputBox.setSymbolicMode(true, false);
		Assert.assertEquals("5", inputBox.getText());
		Assert.assertEquals("5", inputBox.getTextForEditor());
	}

	private void t(String input, String... expected) {
		AlgebraTestHelper.testSyntaxSingle(input, expected,
				getApp().getKernel().getAlgebraProcessor(),
				StringTemplate.xmlTemplate);
	}

	private void hasType(String label, GeoClass geoClass) {
		Assert.assertEquals(lookup(label).getGeoClassType(), geoClass);
	}

	private void updateInput(String string) {
		inputBox.textObjectUpdated(new ConstantTextObject(string));
	}

	private void setupAndCheckInput(String label, String value) {
		setupInput(label, value);
		Assert.assertEquals(value,
				inputBox.toValueString(StringTemplate.testTemplate));
	}

	private void setupInput(String label, String value) {
		add(label + ":" + value);
		inputBox = (GeoInputBox) add("ib=InputBox(" + label + ")");
	}

	@Test
	public void testCanBeSymbolicForPlane() {
		add("A = (0,0)");
		add("B = (2,0)");
		add("C = (2,2)");
		add("p:Plane(A,B,C)");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(p)");
		Assert.assertTrue(inputBox.canBeSymbolic());
	}
}

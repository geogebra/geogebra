package org.geogebra.common.kernel.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.geogebra.common.kernel.StringTemplate;
import org.junit.Before;
import org.junit.Test;

public class GeoCasCellTest extends BaseSymbolicTest {

	@Before
	public void clean() {
		app.getKernel().clearConstruction(true);
		app.setCasConfig();
		app.getKernel().setAngleUnit(app.getConfig().getDefaultAngleUnit());
	}

	@Test
	public void testLineLabel() {
		GeoCasCell f = new GeoCasCell(kernel.getConstruction());
		f.setInput("Line((1,1,1),(2,2,2))");
		f.computeOutput();
		f.plot();
		assertThat(f.getTwinGeo().getLabel(StringTemplate.defaultTemplate), equalTo("f"));
	}

	@Test
	public void testInequalityLabel() {
		GeoCasCell f = new GeoCasCell(kernel.getConstruction());
		f.setInput("b:= x<3");
		f.computeOutput();
		f.plot();
		assertThat(f.getTwinGeo().getLabel(StringTemplate.defaultTemplate), equalTo("b"));
	}
}

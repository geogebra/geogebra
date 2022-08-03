package org.geogebra.common.kernel.geos.symbolic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.gui.view.table.RegressionSpecification;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.test.commands.ErrorAccumulator;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class SymbolicRegressionTest extends BaseUnitTest {

	public AppCommon createAppCommon() {
		return new AppDNoGui(new LocalizationD(3), false);
	}

	@Test
	public void regressionShouldNotBeSymbolic() {
		GeoList list = add("x_1={1,2,3,4}");
		GeoList listY = add("y_1={1,8,27,64}");
		getApp().setCasConfig();
		getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		TableValuesView view = new TableValuesView(getKernel());
		getKernel().attach(view);
		view.add(listY);
		view.showColumn(listY);
		getApp().getSettings().getTable().setValueList(list);
		GeoElement regression = view.plotRegression(1,
				RegressionSpecification.getForListSize(3).get(0));
		assertThat(regression.getGeoClassType(), CoreMatchers.is(GeoClass.FUNCTION));
		assertEquals("f", regression.getLabelSimple());
		EvalInfo info = EvalInfoFactory.getEvalInfoForRedefinition(getKernel(),
				regression, true);
		ErrorAccumulator handler = new ErrorAccumulator();
		getKernel().getAlgebraProcessor().changeGeoElementNoExceptionHandling(regression,
				"FitPoly(RemoveUndefined((x_1,y_1)),3)+1", info, false,
				null, handler);
		assertThat(lookup("f"), hasValue("x³ + 1"));
		reload();
		assertThat(lookup("f"), hasValue("x³ + 1"));
		getKernel().getAlgebraProcessor().changeGeoElementNoExceptionHandling(lookup("f"),
				"FitLogistic(RemoveUndefined((x_1,y_1)),3)+1", info, false,
				null, handler);
		assertThat(lookup("f"),
				hasValue("FitLogistic({(1, 1), (2, 8), (3, 27), (4, 64)}, 3) + 1"));
		assertEquals("", handler.getErrors());
	}

}

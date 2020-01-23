package org.geogebra.web.full.main;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.geogebra.common.kernel.geos.output.GeoOutputFilter;
import org.geogebra.common.kernel.geos.output.NoFilter;
import org.geogebra.common.kernel.geos.output.ProtectiveOutputFilter;
import org.geogebra.web.test.AppMocker;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class AppWFullTest {

	@Test
	public void graphingUsesProtectiveFilter() {
		GeoOutputFilter outputFilter = AppMocker.mockGraphing(getClass()).getOutputFilter();
		assertThat(outputFilter instanceof ProtectiveOutputFilter, is(true));
	}

	@Test
	public void geometryUsesNoFilter() {
		GeoOutputFilter outputFilter = AppMocker.mockGeometry(getClass()).getOutputFilter();
		assertThat(outputFilter instanceof NoFilter, is(true));
	}
}
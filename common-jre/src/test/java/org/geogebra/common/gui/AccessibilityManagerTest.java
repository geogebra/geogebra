package org.geogebra.common.gui;

import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.jre.util.UtilFactoryJre;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.Prover;
import org.junit.Before;
import org.junit.Test;

public class AccessibilityManagerTest extends BaseUnitTest {

	ScreenReaderAdapter screenReaderAdapter;

	@Before
	public void setupTimer() {
		UtilFactory.setPrototypeIfNull(new UtilFactoryJre() {
			@Override
			public HttpRequest newHttpRequest() {
				return null;
			}

			@Override
			public Prover newProver() {
				return null;
			}

			@Override
			public double getMillisecondTime() {
				return 0;
			}

			@Override
			public GTimer newTimer(GTimerListener listener, int delay) {
				return new FlushableTimer(listener);
			}
		});
		screenReaderAdapter = mock(ScreenReaderAdapter.class);
		((EuclidianViewNoGui) getApp().getActiveEuclidianView())
				.setScreenReader(screenReaderAdapter);
	}

	@Test
	public void shouldReadLatest() {
		GeoNumeric slider = add("a=Slider(1,10,1)");
		getApp().getSelectionManager().addSelectedGeo(slider);
		add("SetValue(a,8)");
		add("SetValue(a,7)");
		FlushableTimer.flush();
		verify(screenReaderAdapter).readText(startsWith("Slider a equals 1 Press space"));
		verify(screenReaderAdapter).readText("Slider a equals 7 ");
	}

	@Test
	public void shouldReadDynamicCaption() {
		GeoNumeric slider = add("a=Slider(1,10,1)");
		GeoText caption = add("\"My value is \"+a");
		slider.setDynamicCaption(caption);
		getApp().getSelectionManager().addSelectedGeo(slider);
		slider.setLabelMode(GeoElementND.LABEL_CAPTION);
		add("SetValue(a,8)");
		add("SetValue(a,7)");
		FlushableTimer.flush();
		verify(screenReaderAdapter).readText(startsWith("My value is 1 Press space"));
		verify(screenReaderAdapter).readText("My value is 7 ");
	}
}

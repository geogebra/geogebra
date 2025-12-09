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

package org.geogebra.common.kernel.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.util.UtilFactoryJre;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BatchedUpdateWrapperTest extends BaseUnitTest {
	private WrappedViewTest wrappedView;

	@Mock
	/* package */ GTimer timer;

	@Spy
	private UtilFactoryJre utilFactory;

	private BatchedUpdateWrapper wrapper;

	static abstract private class WrappedViewTest
			implements WrappableView {

		@Override
		public boolean needsUpdateVisualstyle(GProperty property) {
			return true;
		}

		@Override
		public boolean show(GeoElement geo) {
			return true;
		}

		@Override
		public boolean getIsWrapped() {
			return true;
		}

	}

	@Before
	public void setupBatchedUpdateWrapperTest() {
		when(utilFactory.newTimer(any(GTimerListener.class), anyInt())).then(
				invocation -> timer);
		wrappedView = Mockito.mock(WrappedViewTest.class,
				Mockito.CALLS_REAL_METHODS);
		wrapper = new BatchedUpdateWrapper(wrappedView, utilFactory);
	}

	@Test
	public void testCallsTimerStart() {
		GeoElement line = getElementFactory().createGeoLine();

		wrapper.add(line);
		wrapper.update(line);

		verify(timer, atLeast(1)).start();
	}

	@Test
	public void testCallsMethodsWhenTimerRuns() {
		GeoElement line = getElementFactory().createGeoLine();

		wrapper.add(line);
		wrapper.update(line);
		wrapper.rename(line);
		wrapper.updateVisualStyle(line, GProperty.LABEL_STYLE);
		wrapper.updateHighlight(line);
		wrapper.updateAuxiliaryObject(line);
		wrapper.reset();
		wrapper.repaintView();

		// Timer fires
		wrapper.onRun();

		verify(wrappedView).add(line);
		verify(wrappedView).update(line);
		verify(wrappedView).rename(line);
		verify(wrappedView).updateVisualStyle(line, GProperty.LABEL_STYLE);
		verify(wrappedView).updateHighlight(line);
		verify(wrappedView).updateAuxiliaryObject(line);
		verify(wrappedView).reset();
		verify(wrappedView).repaintView();
	}

	@Test
	public void testOptimizeUpdateCalls() {
		GeoElement line = getElementFactory().createGeoLine();
		wrapper.update(line);
		wrapper.update(line);

		wrapper.onRun();

		verify(wrappedView, times(1)).update(line);
	}

	@Test
	public void testOptimizeRemove() {
		GeoElement line = getElementFactory().createGeoLine();
		wrapper.add(line);
		wrapper.update(line);
		wrapper.update(line);
		wrapper.remove(line);

		wrapper.onRun();

		verify(wrappedView, times(1)).remove(line);
		verify(wrappedView, times(0)).add(line);
		verify(wrappedView, times(0)).update(line);
	}

	@Test
	public void testRecursiveAdd() {
		GeoElement line1 = getElementFactory().createGeoLine();

		wrapper.add(line1);

		doAnswer(invocation -> {
			addLine();
			return null;
		}).when(wrappedView).add(eq(line1));

		wrapper.onRun();
	}

	protected void addLine() {
		GeoElement line2 = getElementFactory().createGeoLine();
		wrapper.add(line2);
	}
}

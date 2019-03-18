package org.geogebra.common.kernel.batch;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.util.UtilFactoryJre;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BatchedUpdateWrapperTest extends BaseUnitTest {

	@Mock
	private View wrappedView;

	@Mock
	private GTimer timer;

	@Spy
	private UtilFactoryJre utilFactory;

	private BatchedUpdateWrapper wrapper;

	@Before
	public void setupBatchedUpdateWrapperTest() {
		when(utilFactory.newTimer(any(GTimerListener.class), anyInt())).then(new Answer<GTimer>() {
			@Override
			public GTimer answer(InvocationOnMock invocation) {
				return timer;
			}
		});
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

		doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				GeoElement line2 = getElementFactory().createGeoLine();
				wrapper.add(line2);
				return null;
			}
		}).when(wrappedView).add(eq(line1));

		wrapper.onRun();
	}
}

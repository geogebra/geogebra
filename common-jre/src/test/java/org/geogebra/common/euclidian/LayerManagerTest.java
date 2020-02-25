package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.mock;

public class LayerManagerTest {

	private LayerManager layerManager;
	private GeoElement[] geos;

	@Before
	public void setup() {
		layerManager = new LayerManager();
		geos = new GeoElement[10];
		for (int i = 0; i < geos.length; i++) {
			geos[i] = getDummyGeo();
			layerManager.addGeo(geos[i]);
		}
	}

	@Test
	public void testMoveForward() {
		layerManager.moveForward(Arrays.asList(geos[3], geos[5], geos[9]));
		assertOrdering(0, 1, 2, 4, 6, 7, 8, 3, 5, 9);

		layerManager.moveForward(Collections.singletonList(geos[0]));
		assertOrdering(1, 0, 2, 4, 6, 7, 8, 3, 5, 9);
	}

	@Test
	public void testMoveBackward() {
		layerManager.moveBackward(Arrays.asList(geos[0], geos[9]));
		assertOrdering(0, 9, 1, 2, 3, 4, 5, 6, 7, 8);

		layerManager.moveBackward(Arrays.asList(geos[3], geos[5], geos[6]));
		assertOrdering(0, 9, 1, 3, 5, 6, 2, 4, 7, 8);
	}

	@Test
	public void testMoveToFront() {
		layerManager.moveToFront(Collections.singletonList(geos[7]));
		assertOrdering(0, 1, 2, 3, 4, 5, 6, 8, 9, 7);

		layerManager.moveToFront(Arrays.asList(geos[3], geos[7]));
		assertOrdering(0, 1, 2, 4, 5, 6, 8, 9, 3, 7);
	}

	@Test
	public void testMoveToBack() {
		layerManager.moveToBack(Arrays.asList(geos[9], geos[6], geos[4]));
		assertOrdering(4, 6, 9, 0, 1, 2, 3, 5, 7, 8);

		layerManager.moveToBack(Arrays.asList(geos[6], geos[2]));
		assertOrdering(6, 2, 4, 9, 0, 1, 3, 5, 7, 8);
	}

	private void assertOrdering(int... newOrder) {
		Assert.assertEquals(geos.length, newOrder.length);
		for (int i = 0; i < geos.length; i++) {
			Assert.assertEquals(i, geos[newOrder[i]].getOrdering());
		}
	}

	private static GeoElement getDummyGeo() {
		final int[] ordering = {0};

		GeoElement geo = mock(GeoElement.class);

		Mockito.when(geo.getOrdering()).then(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) {
				return ordering[0];
			}
		});

		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				ordering[0] = invocation.getArgument(0, Integer.class);
				return null;
			}
		}).when(geo).setOrdering(ArgumentMatchers.anyInt());

		return geo;
	}
}

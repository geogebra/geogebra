package org.geogebra.common.gui.view.table;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class TableValuesPointsImplTest extends MockedTableValuesUnitTest {

	private ArgumentCaptor<GeoPoint> captor;
	private TableValuesPointsImpl points;
	private View mockedView;
	private GeoList list;

	@Before
	public void setUp() {
		captor = ArgumentCaptor.forClass(GeoPoint.class);
		mockedView = Mockito.mock(View.class);
		points = new TableValuesPointsImpl(getConstruction(), view, model);
		list = new GeoList(getConstruction());
	}

	@Test
	public void testPointsYValues() {
		getKernel().attach(mockedView);
		mockRowCount(1);
		mockModelValue(0, 0, 1.0);
		mockModelValue(0, 1, 2.0);
		points.notifyColumnAdded(model, list, 1);
		verify(mockedView).add(captor.capture());
		GeoPoint capturedPoint = captor.getValue();
		assertEquals(1.0, capturedPoint.getX(), 0.001);
		assertEquals(2.0, capturedPoint.getY(), 0.001);
	}

	private void initTable(int rows, int columns) {
		mockRowCount(rows);
		mockColumnCount(columns);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				mockModelValue(i, j, i + j);
			}
		}
		for (int i = 1; i < columns; i++) {
			points.notifyColumnAdded(model, list, i);
		}
		getKernel().attach(mockedView);
	}

	@Test
	public void testNotifyCellChanged() {
		initTable(3, 2);
		mockModelValue(0, 1, 10.0);
		points.notifyCellChanged(model, list, 1, 0);
		verify(mockedView).update(captor.capture());
		GeoPoint capturedPoint = captor.getValue();
		assertEquals(10.0, capturedPoint.getY(), 0.001);
	}

	@Test
	public void testNotifyRowChanged() {
		initTable(3, 3);
		mockModelValue(1, 0, 42.0);
		mockModelValue(1, 1, 43.0);
		mockModelValue(1, 2, 44.0);
		points.notifyRowChanged(model, 1);

		verify(mockedView, times(2)).update(captor.capture());
		List<GeoPoint> capturedPoints = captor.getAllValues();
		assertEquals(2, capturedPoints.size());
		GeoPoint first = capturedPoints.get(0);
		GeoPoint second = capturedPoints.get(1);
		assertEquals(42.0, first.getX(), 0.001);
		assertEquals(43.0, first.getY(), 0.001);
		assertEquals(42.0, second.getX(), 0.001);
		assertEquals(44.0, second.getY(), 0.001);
	}

	@Test
	public void testNotifyRowRemoved() {
		initTable(3, 3);
		mockRowCount(2);
		points.notifyRowsRemoved(model, 0, 0);

		verify(mockedView, times(2)).remove(captor.capture());
		List<GeoPoint> capturedPoints = captor.getAllValues();
		assertEquals(2, capturedPoints.size());
		GeoPoint first = capturedPoints.get(0);
		GeoPoint second = capturedPoints.get(1);
		assertEquals(0.0, first.getX(), 0.001);
		assertEquals(1.0, first.getY(), 0.001);
		assertEquals(0.0, second.getX(), 0.001);
		assertEquals(2.0, second.getY(), 0.001);
	}

	@Test
	public void testNotifyMultipleRowRemoved() {
		initTable(5, 3);
		mockRowCount(5);
		points.notifyRowsRemoved(model, 0, 4);

		verify(mockedView, times(10)).remove(captor.capture());
	}
}

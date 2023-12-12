package org.geogebra.common.gui.view.table;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.junit.Before;
import org.junit.Test;

public class ModelEventCollectorTests extends BaseUnitTest {
    private ModelEventCollector modelEventCollector;
    private SimpleTableValuesModel simpleTableValuesModel;
    private GeoEvaluatable geoEvaluatable;

    @Before
    public void setup() {
        modelEventCollector = new ModelEventCollector();
        simpleTableValuesModel = mock(SimpleTableValuesModel.class);
        geoEvaluatable = mock(GeoEvaluatable.class);

        // Mocked getRowCount() method used in startCollection()
        when(simpleTableValuesModel.getRowCount()).thenReturn(5);
    }

    @Test
    public void testCollectionWithDatasetChangeNotification() {
        modelEventCollector.startCollection(simpleTableValuesModel);
        modelEventCollector.notifyDatasetChanged(simpleTableValuesModel);
        modelEventCollector.endCollection(simpleTableValuesModel);

        verify(simpleTableValuesModel, times(1)).notifyDatasetChanged();
    }

    @Test
    public void testCollectionWithOneCellChangeNotification() {
        modelEventCollector.startCollection(simpleTableValuesModel);
        modelEventCollector.notifyCellChanged(simpleTableValuesModel, geoEvaluatable, 0, 0);
        modelEventCollector.endCollection(simpleTableValuesModel);

        verify(simpleTableValuesModel, times(1))
                .notifyCellChanged(geoEvaluatable, 0, 0);
    }

    @Test
    public void testCollectionWithOneColumnChangeNotification() {
        modelEventCollector.startCollection(simpleTableValuesModel);
        modelEventCollector.notifyColumnChanged(simpleTableValuesModel, geoEvaluatable, 0);
        modelEventCollector.endCollection(simpleTableValuesModel);

        verify(simpleTableValuesModel, times(1))
                .notifyColumnChanged(geoEvaluatable, 0);
    }

    @Test
    public void testCollectionWithMultipleChangeNotifications() {
        modelEventCollector.startCollection(simpleTableValuesModel);
        modelEventCollector.notifyCellChanged(simpleTableValuesModel, geoEvaluatable, 1, 2);
        modelEventCollector.notifyColumnChanged(simpleTableValuesModel, geoEvaluatable, 3);
        modelEventCollector.endCollection(simpleTableValuesModel);

        verify(simpleTableValuesModel, times(1))
                .notifyCellChanged(geoEvaluatable, 1, 2);
        verify(simpleTableValuesModel, times(1))
                .notifyColumnChanged(geoEvaluatable, 3);
    }

    @Test
    public void testCollectionWithIgnoredRowsRemovedNotification() {
        modelEventCollector.startCollection(simpleTableValuesModel);
        modelEventCollector.notifyRowsRemoved(simpleTableValuesModel, 0, 2);
        modelEventCollector.endCollection(simpleTableValuesModel);

        verify(simpleTableValuesModel, never()).notifyRowsRemoved(0, 2);
    }

    @Test
    public void testCollectionWithIgnoredRowsAddedNotification() {
        modelEventCollector.startCollection(simpleTableValuesModel);
        modelEventCollector.notifyRowsAdded(simpleTableValuesModel, 0, 2);
        modelEventCollector.endCollection(simpleTableValuesModel);

        verify(simpleTableValuesModel, never()).notifyRowsAdded(0, 2);
    }
}

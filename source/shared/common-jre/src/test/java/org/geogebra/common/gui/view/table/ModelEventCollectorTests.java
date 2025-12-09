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

package org.geogebra.common.gui.view.table;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ModelEventCollectorTests extends BaseAppTestSetup {
    private ModelEventCollector modelEventCollector;
    private SimpleTableValuesModel simpleTableValuesModel;
    private GeoEvaluatable geoEvaluatable;

    @BeforeEach
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

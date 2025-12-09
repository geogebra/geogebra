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

package org.geogebra.web.full.gui.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.junit.Test;
import org.mockito.Mockito;

public class MaterialVisibilityControllerTest {
	private final LogInOperation loginOperation = Mockito.mock(LogInOperation.class);
	private final MaterialVisibilityController controller
			= new MaterialVisibilityController(loginOperation);

	@Test
	public void testForeignMaterialVisibility() {
		when(loginOperation.owns(Mockito.any())).thenReturn(false);
		assertEquals(MaterialVisibility.Shared,
				controller.getMaterialVisibility(newMaterial("")));
		assertEquals(MaterialVisibility.Shared,
				controller.getMaterialVisibility(newMaterial("S")));
		assertEquals(MaterialVisibility.Shared,
				controller.getMaterialVisibility(newMaterial("O")));
	}

	@Test
	public void testOwnMaterialVisibility() {
		when(loginOperation.owns(Mockito.any())).thenReturn(true);
		assertEquals(MaterialVisibility.Private,
				controller.getMaterialVisibility(newMaterial("")));
		assertEquals(MaterialVisibility.Shared,
				controller.getMaterialVisibility(newMaterial("S")));
		assertEquals(MaterialVisibility.Public,
				controller.getMaterialVisibility(newMaterial("O")));

	}

	private Material newMaterial(String visibility) {
		Material material = new Material(Material.MaterialType.ggb);
		material.setVisibility(visibility);
		return material;
	}

	@Test
	public void testIndexToVisibility() {
		assertEquals(MaterialVisibility.Private, controller.getVisibility(0));
		assertEquals(MaterialVisibility.Shared, controller.getVisibility(1));
		assertEquals(MaterialVisibility.Public, controller.getVisibility(2));
	}
}

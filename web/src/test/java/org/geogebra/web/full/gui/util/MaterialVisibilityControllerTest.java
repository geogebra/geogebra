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

package org.geogebra.web.full.gui.util;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.move.ggtapi.models.Material;
import org.junit.Test;

public class MaterialVisibilityControllerTest {
	private final LogInOperationMock loginOperation = new LogInOperationMock();
	private final MaterialVisibilityController controller
			= new MaterialVisibilityController(loginOperation);

	@Test
	public void testForeignMaterialVisibility() {
		assertEquals(MaterialVisibility.Shared,
				controller.getMaterialVisibility(newMaterial("")));
		assertEquals(MaterialVisibility.Shared,
				controller.getMaterialVisibility(newMaterial("S")));
		assertEquals(MaterialVisibility.Shared,
				controller.getMaterialVisibility(newMaterial("O")));
	}

	@Test
	public void testOwnMaterialVisibility() {
		loginOperation.setOwns();
		assertEquals(MaterialVisibility.Private,
				controller.getMaterialVisibility(newMaterial("")));
		assertEquals(MaterialVisibility.Shared,
				controller.getMaterialVisibility(newMaterial("S")));
		assertEquals(MaterialVisibility.Public,
				controller.getMaterialVisibility(newMaterial("O")));

	}

	private Material newMaterial(String visibility) {
		Material material = new Material(0, Material.MaterialType.ggb);
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

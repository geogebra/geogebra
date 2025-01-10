package org.geogebra.web.full.gui.util;

import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;

public class MaterialVisibilityController {
	private final LogInOperation logInOperation;

	public MaterialVisibilityController(LogInOperation loginOperation) {
		this.logInOperation = loginOperation;
	}

	MaterialVisibility getMaterialVisibility(Material material) {
		return isOwnMaterial(material)
				? MaterialVisibility.value(material.getVisibility())
				: MaterialVisibility.Shared;
	}

	private boolean isOwnMaterial(Material material) {
		return material != null
				&& logInOperation.owns(material);
	}

	MaterialVisibility getVisibility(int index) {
		return MaterialVisibility.value(index);
	}
}

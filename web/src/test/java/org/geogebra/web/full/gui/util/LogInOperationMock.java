package org.geogebra.web.full.gui.util;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;

public class LogInOperationMock extends LogInOperation {

	private boolean owns = false;

	@Override
	public BackendAPI getGeoGebraTubeAPI() {
		return null;
	}

	@Override
	protected String getURLLoginCaller() {
		return null;
	}

	@Override
	protected String getURLClientInfo() {
		return null;
	}

	public void setOwns() {
		owns = true;
	}

	@Override
	public boolean owns(Material mat) {
		return owns;
	}
}

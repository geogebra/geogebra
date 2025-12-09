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

package org.geogebra.desktop.move.ggtapi.models;

import static org.geogebra.common.main.PreviewFeature.RESOURCES_API_BETA;

import javax.swing.SwingUtilities;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.move.events.GenericEvent;
import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.views.EventRenderable;

/**
 * The desktop version of the login operation. uses an own AuthenticationModel
 * and an own implementation of the API
 * 
 * @author stefan
 */
public class LoginOperationD extends LogInOperation {

	/**
	 * Initializes the LoginOperation for Desktop by creating the corresponding
	 * model and view classes
	 */
	public LoginOperationD() {
		super();
		setModel(new AuthenticationModelD());
	}

	@Override
	public void performTokenLogin(String token, boolean automatic) {

		// Perform the API call in a background process
		doPerformTokenLogin(new GeoGebraTubeUser(token), automatic);
	}

	private GeoGebraTubeAPID api;

	@Override
	public GeoGebraTubeAPID getGeoGebraTubeAPI() {
		if (api == null) {
			ClientInfo client = new ClientInfo();
			client.setModel((AuthenticationModel) this.model);
			client.setType("desktop");
			client.setWidth(1024);
			client.setWidth(768);
			api = new GeoGebraTubeAPID(PreviewFeature.isAvailable(RESOURCES_API_BETA), client);
		}
		return api;
	}

	@Override
	protected String getURLLoginCaller() {
		return "desktop";
	}

	@Override
	protected String getURLClientInfo() {
		return UtilFactory.getPrototype().newURLEncoder()
				.encode("GeoGebra Desktop Application V"
				+ GeoGebraConstants.VERSION_STRING);
	}

	@Override
	public MaterialRestAPI getResourcesAPI() {
		return null;
	}

	@Override
	public void dispatchEvent(final GenericEvent<EventRenderable> event) {

		// call the gui event on the Event dispatch thread.
		SwingUtilities.invokeLater(() -> LoginOperationD.super.dispatchEvent(event));
	}

}

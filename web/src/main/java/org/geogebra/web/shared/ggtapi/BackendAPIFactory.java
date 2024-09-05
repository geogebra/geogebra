package org.geogebra.web.shared.ggtapi;

import org.geogebra.common.main.FeaturePreview;
import org.geogebra.common.move.ggtapi.models.MarvlService;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.models.MowService;
import org.geogebra.common.move.ggtapi.models.Service;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.shared.ggtapi.models.GeoGebraTubeAPIW;

/**
 * Class to provide the right backend API
 * for the given application.
 *
 * @author laszlo
 */
public class BackendAPIFactory {

	private final AppW app;
	private final AppletParameters articleElement;
	private BackendAPI api = null;
	private MaterialRestAPI restApi = null;

	/**
	 * @param app The application.
	 */
	public BackendAPIFactory(AppW app) {
		this.app = app;
		articleElement = app.getAppletParameters();
	}

	/**
	 * @return the backend API suitable for the application (tube or mow-back).
	 */
	public BackendAPI get() {
		createApiIfNeeded();
		api.setClient(app.getClientInfo());
		return this.api;
	}

	/**
	 * @return REST api for resources
	 */
	public MaterialRestAPI getResourcesApi() {
		if (restApi == null) {
			restApi = newMaterialRestAPI();
		}
		restApi.setClient(app.getClientInfo());
		return this.restApi;
	}

	private void createApiIfNeeded() {
		if (api != null) {
			return;
		}
		api = app.isMebis() ? newMaterialRestAPI() : newTubeAPI();
	}

	/**
	 * Create an appropriate MaterialRestApi
	 * @return a MaterialRestApi based on the backend-url data-param
	 */
	public MaterialRestAPI newMaterialRestAPI() {
		String backendURL = articleElement.getParamBackendURL().isEmpty()
				? MaterialRestAPI.marvlUrl : articleElement.getParamBackendURL();
		Service service = app.isMebis()
				? new MowService() : new MarvlService();

		return new MaterialRestAPI(backendURL, service);
	}

	private GeoGebraTubeAPIW newTubeAPI() {
		return new GeoGebraTubeAPIW(app.getClientInfo(),
				app.has(FeaturePreview.TUBE_BETA),
				articleElement);
	}
}

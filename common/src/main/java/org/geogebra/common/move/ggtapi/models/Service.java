package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.util.HttpRequest;

public interface Service {

	HttpRequest createRequest(AuthenticationModel model);

	String getDeletionJson(Material.MaterialType materialType);

	String getGgsTemplateEndpoint(int userId);
}

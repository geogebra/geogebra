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

package org.geogebra.web.shared.ggtapi.models;

import static elemental2.core.Global.JSON;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.GeoGebraTubeAPIWSimple;
import org.geogebra.web.html5.util.AppletParameters;

import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * API Interface for GeoGebraTube requests and responses
 * 
 * @author Matthias Meisinger
 * 
 */
public class GeoGebraTubeAPIW extends GeoGebraTubeAPIWSimple {

	/**
	 * @param beta
	 *            whether to use beta
	 * @param client
	 *            {@link ClientInfo}
	 * @param articleElement
	 *            parameters
	 */
	public GeoGebraTubeAPIW(ClientInfo client, boolean beta,
			AppletParameters articleElement) {
		super(beta, articleElement);
		this.client = client;
	}

	/**
	 * Copies the user data from the API response to this user.
	 * 
	 * @return true if the data could be parsed successfully, false otherwise
	 */
	@Override
	public boolean parseUserDataFromResponse(GeoGebraTubeUser user,
			String result) {
		try {
			JsPropertyMap<Object> parsedResult = Js.asPropertyMap(JSON.parse(result));
			JsPropertyMap<Object> responses = Js.asPropertyMap(parsedResult.get("responses"));

			JsPropertyMap<Object> response = Js.asPropertyMap(
					Js.asArrayLike(responses.get("response")).getAt(0));
			JsPropertyMap<Object> userinfo = Js.asPropertyMap(response.get("userinfo"));

			user.setUserId(Js.coerceToInt(userinfo.get("user_id")));
			// "username" is real name by default, uses login as fallback
			user.setUserName(Js.asString(userinfo.get("username")));
			user.setIdentifier(Js.asString(userinfo.get("identifier")));

			if (Js.isTruthy(userinfo.get("ggt_avatar_url"))) {
				user.setImageURL(Js.asString(userinfo.get("ggt_avatar_url")));
			}
			if (Js.isTruthy(userinfo.get("ggt_profile_url"))) {
				user.setProfileURL(Js.asString(userinfo.get("ggt_profile_url")));
			}
			if (Js.isTruthy(userinfo.get("image"))) {
				user.setImageURL(Js.asString(userinfo.get("image")));
			}
			if (Js.isTruthy(userinfo.get("lang_ui"))) {
				user.setLanguage(Js.asString(userinfo.get("lang_ui")));
			}
			if (Js.isTruthy(userinfo.get("token"))) {
				user.setToken(Js.asString(userinfo.get("token")));
			}

			// Further fields are not parsed yet, because they are not needed

			// user.setGGTProfileURL(userinfo.getString("ggt_profile_url"));
			// user.setGroup(userinfo.getString("group"));
			// user.setDateCreated(userinfo.getString("date_created"));
		} catch (Exception e) {
			Log.debug(e);
			return false;
		}

		return true;
	}
}

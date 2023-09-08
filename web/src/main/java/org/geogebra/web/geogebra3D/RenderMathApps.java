package org.geogebra.web.geogebra3D;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.html5.bridge.AttributeProvider;
import org.geogebra.web.html5.bridge.RenderGgbElement.RenderGgbElementFunction;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;

import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsObject;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public final class RenderMathApps implements RenderGgbElementFunction {


	private final GLookAndFeel laf;
	private final AppletFactory factory;
	private AppletParameters parameters;

	public RenderMathApps(GLookAndFeel laf, AppletFactory factory) {
		this.laf = laf;
		this.factory = factory;
	}

	@Override
	public void render(Object options, JsConsumer<Object> callback) {
		AttributeProvider provider = AttributeProvider.as(options);
		GeoGebraElement element = GeoGebraElement.as(provider.getElement());
		parameters = new AppletParameters(provider);
		String materialId = parameters.getMaterialId();
		if (materialId != null) {
			loadMaterial(materialId);
		}
		final GeoGebraFrameFull full = new GeoGebraFrameFull(factory, laf,
				null, element, parameters);
		DomGlobal.window.addEventListener("resize", evt -> onResize(full));
		full.renderArticleElementWithFrame(element, provider, callback);
		full.updateArticleHeight();
		full.getApp();
	}

	private void loadMaterial(String materialId) {
		String host = DomGlobal.location.host.matches("/(www|stage|beta|groot|alpha)"
				+ ".geogebra.(org|net)/")
				? DomGlobal.location.host
				: "www.geogebra.org";
		String path = "/materials/" + materialId + "?scope=basic";
		String url = "https://" + host + "/api/proxy.php?path=" + Global.encodeURIComponent(path);
		sendCorsRequest(url);
	}

	private void sendCorsRequest(String url) {
		HttpRequest xhr = UtilFactory.getPrototype().newHttpRequest();
		xhr.sendRequestPost("GET", url, null,
				new AjaxCallback() {

					@Override
					public void onSuccess(String response) {
						JsPropertyMap<JsObject>
								data = Js.uncheckedCast(Global.JSON.parse(response));
						JsPropertyMap<JsObject> item = null;
						if (data.has("elements")) {
							JsArray<JsObject> elements = Js.uncheckedCast(data.get("elements"));
							JsArray<JsObject> filtered =
									elements.filter(RenderMathApps.this::filterElements);
							item = Js.uncheckedCast(filtered.getAt(0));
						} else {
							item = data;
						}

						if (item == null || !item.has("url")) {
							onError("");
							return;
						}

						parameters.setAttribute("fileName", item.get("url").toString_());
						DomGlobal.console.log(item);
					}


					@Override
					public void onError(String error) {
						DomGlobal.console.debug("Error: Fetching material (id "
								+ parameters.getMaterialId() + ") failed.", parameters);
					}
				});
	}

	private Object filterElements(JsObject object, int i, JsArray<JsObject> array) {
		JsPropertyMap<String> elem = Js.uncheckedCast(object);
		String type = elem.get("type");
		return "G".equals(type) || "E".equals(type);
	}


	private void onResize(GeoGebraFrameFull full) {
		DomGlobal.console.debug("onResize");
		full.setSize(800, 600);
	}

}

package org.geogebra.web.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.Shardable;
import com.google.gwt.core.linker.CrossSiteIframeLinker;

@LinkerOrder(LinkerOrder.Order.PRIMARY)
@Shardable
public class StandardIframeLinker extends CrossSiteIframeLinker {
	protected String getJsInstallLocation(LinkerContext context) {
		return "org/geogebra/web/linker/installLocationIframe.js";
	}

}

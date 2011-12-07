package geogebra.web.ggb;

import java.util.Map;



import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.HasWidgets;


import geogebra.web.gin.AsyncProvider;
import geogebra.web.helper.CodeSplitCallback;
import geogebra.web.io.ConstructionException;
import geogebra.web.jso.JsUint8Array;
import geogebra.web.main.Application;
import geogebra.web.util.DataUtil;


public class ApplicationWrapper {
	
	private final AsyncProvider<Application> appProvider;
	private Application app;
	private Map<String, String> archiveContent;

	public ApplicationWrapper(AsyncProvider<Application> appProvider) {
		this.appProvider = appProvider;		
	}
	
	public void onSyncCanvasSizeWithApplication() {
		((geogebra.web.euclidian.EuclidianView) app.getEuclidianView()).synCanvasSize();
		app.getEuclidianView().repaintView();
	}
	
	public void onFileContentLoaded(final JsUint8Array zippedContent) {
		archiveContent = DataUtil.unzip(zippedContent);
		maybeLoadFile();
	}
	
	public void onCreateApplicationAndAddTo(HasWidgets container) {
		final Canvas canvas = Canvas.createIfSupported();
		container.add(canvas);
		
		appProvider.get(new CodeSplitCallback<Application>() {
			@Override public void onSuccess(Application result) {
				app = result;
				app.init(canvas);
				maybeLoadFile();
			}
		});
	}
	
	private void maybeLoadFile() {
		if (app == null || archiveContent == null) {
			return;
		}
		
		try {
			app.loadGgbFile(archiveContent);
		} catch (ConstructionException ex) {
				Application.log(ex.getMessage());
			return;
		}
		archiveContent = null;
		onSyncCanvasSizeWithApplication();
	}
	
	
}

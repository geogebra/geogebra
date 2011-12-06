package geogebra.web.ggb;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.HasWidgets;

import geogebra.web.gin.AsyncProvider;
import geogebra.web.helper.CodeSplitCallback;
import geogebra.web.main.Application;


public class ApplicationWrapper {
	
	private final AsyncProvider<Application> appProvider;
	private Application app;

	public ApplicationWrapper(AsyncProvider<Application> appProvider) {
		this.appProvider = appProvider;		
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
		/* I will continue here tomorrow :-)if (app == null || archiveContent == null) {
			return;
		}
		
		try {
			app.loadGgbFile(archiveContent);
		} catch (ConstructionException ex) {
			getEventBus().worksheetConstructionFailed(ex.getMessage());
			return;
		}
		archiveContent = null;
		onSyncCanvasSizeWithApplication();
		getEventBus().worksheetReady();*/
	}
	
	
}

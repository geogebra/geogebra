package org.geogebra.web.web.gui.menubar;

import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.main.FileManagerW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * @author bencze
 * The "Export Image" menu, part of the "File" menu.
 */
public class ExportMenuW extends MenuBar {

	/**
	 * Application instance
	 */
	AppW app;

	/**
	 * Constructs the "Insert Image" menu
	 * 
	 * @param app
	 *            Application instance
	 */
	public ExportMenuW(AppW app) {
		super(true);
		
		this.app = app;
		addStyleName("GeoGebraMenuBar");
		MainMenu.addSubmenuArrow(app, this);
		
		initActions();
	}

	private void initActions() {

		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
		// translation not needed
		        .getSafeUri().asString(), "ggb", true), true,
		        new MenuCommand(app) {

			        @Override
			        public void doExecute() {
				        app.getFileManager().export(app);
			        }
		        });

		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
		// translation not needed
		        .getSafeUri().asString(), "png",
		        true), true, new Command() {

			public void execute() {
				String url = ((EuclidianViewW) app.getActiveEuclidianView())
				        .getExportImageDataUrl(1.0, false);
				app.getFileManager().exportImage(url, "export-png");
			}
		});
		if(!app.getLAF().isTablet()){
			addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
			        .getSafeUri().asString(), app.getPlain("AnimatedGIF"),
			        true), true, new Command() {
				public void execute() {
					        ((FileManagerW) app.getFileManager())
					                .createRemoteAnimGif(app);
					        }
			});
		}
	}

	
}

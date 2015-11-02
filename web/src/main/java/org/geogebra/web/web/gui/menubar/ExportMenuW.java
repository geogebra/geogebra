package org.geogebra.web.web.gui.menubar;

import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.main.FileManagerW;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;

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
		MainMenu.addSubmenuArrow(this);
		
		initActions();
	}

	private void initActions() {

		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
		// translation not needed
		        .getSafeUri().asString(), "ggb", true), true,
		        new MenuCommand(app) {

			        @Override
			        public void doExecute() {
				hide();
				app.getFileManager().export(app);
			}
		        });

		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
		// translation not needed
		        .getSafeUri().asString(), "png",
 true), true, new MenuCommand(
				app) {

			public void execute() {
				hide();
				String url = ((EuclidianViewWInterface) app
						.getActiveEuclidianView()).getExportImageDataUrl(1.0,
						false);

				app.getFileManager()
						.showExportAsPictureDialog(url, app);
			}
		});
		if(!app.getLAF().isTablet()){
			addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
			        .getSafeUri().asString(), app.getPlain("AnimatedGIF"),
 true),
					true, new MenuCommand(app) {
						public void doExecute() {
							hide();
							((FileManagerW) app.getFileManager())
					                .createRemoteAnimGif(app);
					        }
			});
		}
	}

	private void hide() {
		PopupPanel p = (PopupPanel) getParent();
		p.hide();
	}
}

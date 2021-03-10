package org.geogebra.desktop.headless;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.jre.headless.App3DCompanionHeadless;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.AppDI;
import org.geogebra.common.jre.headless.EuclidianController3DNoGui;
import org.geogebra.common.jre.headless.EuclidianView3DNoGui;
import org.geogebra.common.jre.kernel.commands.CommandDispatcher3DJre;
import org.geogebra.common.jre.plugin.GgbAPIJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCompanion;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.euclidian.DrawEquationD;
import org.geogebra.desktop.factories.AwtFactoryD;
import org.geogebra.desktop.factories.LoggingCASFactoryD;
import org.geogebra.desktop.factories.UtilFactoryD;
import org.geogebra.desktop.io.MyXMLioD;
import org.geogebra.desktop.kernel.geos.GeoElementGraphicsAdapterD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.main.undo.UndoManagerD;
import org.geogebra.desktop.move.ggtapi.models.LoginOperationD;
import org.geogebra.desktop.plugin.GgbAPID;
import org.geogebra.desktop.sound.SoundManagerD;
import org.geogebra.desktop.util.GTimerD;
import org.geogebra.desktop.util.ImageManagerD;


/**
 * App for testing: does not use Swing
 *
 * @author Zbynek
 *
 */
public class AppDNoGui extends AppCommon implements AppDI {

	private DrawEquationD drawEquation;
	private GgbAPIJre ggbapi;
	private SoundManager soundManager;
	private boolean is3Dactive;
	private EuclidianView3DNoGui ev3d;

	/**
	 * @param loc
	 *            localization
	 * @param silent
	 *            whether to mute logging
	 */
	public AppDNoGui(LocalizationD loc, boolean silent) {
		super(loc, new AwtFactoryD());
		String prop = System.getProperty("ggb.prerelease");
		prerelease = !"false".equals(prop);

		UtilFactory.setPrototypeIfNull(new UtilFactoryD());
		loginOperation = new LoginOperationD(this);
		setCASFactory(new LoggingCASFactoryD());
	}

	public void addExternalImage(String name, MyImageJre img) {
		// TODO Auto-generated method stub
	}

	public void storeFrameCenter() {
		// TODO Auto-generated method stub
	}

	public MyImageJre getExportImage(double thumbnailPixelsX,
			double thumbnailPixelsY) {
		// TODO Auto-generated method stub
		return null;
	}

	public MyImageJre getExternalImage(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param locale
	 *            locale
	 */
	public void setLanguage(Locale locale) {

		if ((locale == null)
				|| getLocalization().getLocale().toString()
						.equals(locale.toString())) {
			return;
		}

		if (!initing) {
			setMoveMode();
		}

		// load resource files
		setLocale(locale);

		// make sure digits are updated in all numbers
		getKernel().updateConstruction(false);
		setUnsaved();
	}

	/**
	 * @param locale
	 *            locale
	 */
	public void setLocale(Locale locale) {
		if (locale == getLocalization().getLocale()) {
			return;
		}
		// Locale oldLocale = loc.getLocale();

		// only allow special locales due to some weird server
		// problems with the naming of the property files
		((LocalizationD) getLocalization()).setLocale(locale);
		getLocalization().updateLanguageFlags(locale.getLanguage());
	}

	public void testFeatures() {
		boolean pre = prerelease;
		ArrayList<Feature> stable = new ArrayList<>();
		ArrayList<Feature> beta = new ArrayList<>();
		ArrayList<Feature> dead = new ArrayList<>();

		for (Feature f : Feature.values()) {

			this.prerelease = false;
			if (has(f)) {
				stable.add(f);
			} else {
				this.prerelease = true;
				if (has(f)) {
					beta.add(f);
				} else {
					dead.add(f);
				}
			}
		}
		Log.debug(stable.size() + StringUtil.join("\n", stable));
		Log.debug(beta.size() + StringUtil.join("\n", beta));
		Log.debug(dead.size() + StringUtil.join("\n", dead));

		prerelease = pre;
	}

	@Override
	public DrawEquation getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquationD();
		}
		return drawEquation;
	}

	@Override
	public GgbAPI getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new GgbAPIHeadless(this);
		}
		return ggbapi;
	}

	@Override
	public MyXMLio createXMLio(Construction cons) {
		return new MyXMLioD(cons.getKernel(), cons);
	}

	@Override
	public SoundManager getSoundManager() {
		if (soundManager == null) {
			soundManager = new SoundManagerD(this);
		}
		return soundManager;
	}

	@Override
	public ImageManager getImageManager() {
		return new ImageManagerD();
	}

	@Override
	public GTimer newTimer(GTimerListener listener, int delay) {
		return new GTimerD(listener, delay);
	}

	@Override
	protected int getWindowWidth() {
		return 800;
	}

	@Override
	protected int getWindowHeight() {
		return 600;
	}

	@Override
	public MyImage getExternalImageAdapter(String filename, int width,
			int height) {
		return ImageManagerD.getExternalImage(filename);
	}

	@Override
	public UndoManagerD getUndoManager(Construction cons) {
		return new UndoManagerD(cons, true);
	}

	@Override
	public GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		return new GeoElementGraphicsAdapterD(this);
	}

	@Override
	public void setActiveView(int evID) {
		this.is3Dactive = evID == App.VIEW_EUCLIDIAN3D;
	}

	@Override
	public EuclidianView getActiveEuclidianView() {
		return is3Dactive && ev3d != null ? ev3d : euclidianView;
	}

	@Override
	public EuclidianView3DInterface getEuclidianView3D() {
		return ev3d = new EuclidianView3DNoGui(
				new EuclidianController3DNoGui(this, kernel),
				this.getSettings().getEuclidian(3));
	}

	@Override
	protected AppCompanion newAppCompanion() {
		return new App3DCompanionHeadless(this);
	}

	@Override
	public CommandDispatcher newCommand3DDispatcher(Kernel cmdKernel) {
		return new CommandDispatcher3DJre(cmdKernel);
	}

	public boolean is3D() {
		return true;
	}

	private static class GgbAPIHeadless extends GgbAPIJre {

		public GgbAPIHeadless(App app) {
			super(app);
		}

		@Override
		public byte[] getGGBfile() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setErrorDialogsActive(boolean flag) {
			// TODO Auto-generated method stub
		}

		@Override
		public void refreshViews() {
			// TODO Auto-generated method stub
		}

		@Override
		public void openFile(String strURL) {
			try {
				String lowerCase = StringUtil.toLowerCaseUS(strURL);
				URL url = new URL(strURL);
				GFileHandler.loadXML(getApplication(), url.openStream(),
						lowerCase.endsWith(FileExtensions.GEOGEBRA_TOOL
								.toString()));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean writePNGtoFile(String filename,
				double exportScale, boolean transparent, double DPI,
				boolean greyscale) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void handleSlideAction(String eventType, String pageIdx, String appStat) {
			// stub
		}

		@Override
		public void selectSlide(String pageIdx) {
			// stub
		}

		@Override
		public void previewRefresh() {
			// stub
		}

		@Override
		protected void exportPNGClipboard(boolean transparent, int DPI,
				double exportScale, EuclidianView ev) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void exportPNGClipboardDPIisNaN(boolean transparent,
				double exportScale, EuclidianView ev) {
			// TODO Auto-generated method stub

		}

		@Override
		protected String base64encodePNG(boolean transparent,
				double DPI, double exportScale, EuclidianView ev) {
			ev.updateBackground();
			GBufferedImage img = ev
					.getExportImage(exportScale, transparent,
							ExportType.PNG);
			return GgbAPID.base64encode(
					GBufferedImageD.getAwtBufferedImage(img), DPI);
		}

	}
}

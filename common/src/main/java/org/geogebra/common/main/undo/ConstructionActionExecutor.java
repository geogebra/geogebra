package org.geogebra.common.main.undo;

import static org.geogebra.common.euclidian.StrokeSplitHelper.DEL;

import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.draw.DrawInline;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.ActionType;

public class ConstructionActionExecutor
		implements ActionExecutor {

	private final App app;

	public ConstructionActionExecutor(App app) {
		this.app = app;
	}
	
	@Override
	public boolean executeAction(ActionType action, String... args) {
		if (action == ActionType.REMOVE) {
			for (String arg: args) {
				app.getKernel().lookupLabel(arg).remove();
			}
		} else if (action == ActionType.ADD) {
			for (String arg: args) {
				evalXML(arg);
			}
			app.getActiveEuclidianView().invalidateDrawableList();
		} else if (action == ActionType.UPDATE || action == ActionType.MERGE_STROKE
					|| action == ActionType.SPLIT_STROKE) {
			for (String arg: args) {
				if (arg.charAt(0) == '<') {
					evalXML(arg);
				} else if (arg.startsWith(DEL)) {
					app.getKernel().lookupLabel(arg.substring(DEL.length())).remove();
				}
				else {
					app.getGgbApi().evalCommand(arg);
				}
				app.getActiveEuclidianView().invalidateDrawableList();
			}
		} else  if (action == ActionType.UPDATE_ORDERING) {
			for (String arg: args) {
				String [] split = arg.split(",");
				GeoElement geo = app.getKernel().lookupLabel(split[0]);
				app.getKernel().getConstruction().getLayerManager()
						.updateDrawingListAndUI(geo, Double.parseDouble(split[1]));
				app.getActiveEuclidianView().invalidateDrawableList();
			}
		} else if (action == ActionType.SET_CONTENT) {
			for (int i = 0; i < args.length; i += 4) {
				GeoElement geo = app.getKernel().lookupLabel(args[i]);
				if (geo instanceof GeoInline) {
					((GeoInline) geo).setHeight(Double.parseDouble(args[i + 1]));
					((GeoInline) geo).setContentHeight(Double.parseDouble(args[i + 2]));
					setContentAndNotify((GeoInline) geo, args[i + 3]);
				}
			}
		} else {
			return false;
		}
		return true;
	}

	private void setContentAndNotify(GeoInline inline, String content) {
		inline.setContent(content);
		inline.notifyUpdate();
		DrawableND drawable = app.getActiveEuclidianView().getDrawableFor(inline);
		if (drawable instanceof DrawInline) {
			((DrawInline) drawable).updateContent();
			app.getKernel().notifyRepaint();
		}
	}

	private void evalXML(String arg) {
		app.getGgbApi().evalXML(arg);
		app.getKernel().notifyRepaint();
	}
}

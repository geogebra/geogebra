package org.geogebra.common.main.undo;

import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.draw.DrawInline;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.scripting.CmdSetValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.ActionType;
import org.geogebra.common.util.debug.Log;

public class ConstructionActionExecutor
		implements ActionExecutor {

	private final App app;
	public static final String DEL = "DEL::";
	public static final String SET = "SET::";

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
			executeUpdateAction(args);
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

	private void executeUpdateAction(String[] args) {
		for (String arg: args) {
			if (arg.charAt(0) == '<') {
				evalXML(arg);
			} else if (arg.startsWith(DEL)) {
				app.getKernel().lookupLabel(arg.substring(DEL.length())).remove();
			} else if (arg.startsWith(SET)) {
				processSetValue(arg.substring(SET.length()));
			} else {
				app.getGgbApi().evalCommand(arg);
			}
			app.getActiveEuclidianView().invalidateDrawableList();
		}
	}

	private void processSetValue(String substring) {
		try {
			ValidExpression ve = app.getKernel().getParser().parseGeoGebraExpression(substring);
			String label = ve.getLabel();
			ve.setLabel(null);
			AlgebraProcessor algebraProcessor = app.getKernel().getAlgebraProcessor();
			CmdSetValue.setValue2(app.getKernel().lookupLabel(label),
					algebraProcessor.processValidExpression(ve, new EvalInfo(false))[0]);
		} catch (ParseException | MyError | CircularDefinitionException | RuntimeException e) {
			Log.warn(e);
		}
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

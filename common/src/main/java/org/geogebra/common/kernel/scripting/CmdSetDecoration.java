package org.geogebra.common.kernel.scripting;

import java.util.Set;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.SegmentStyle;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.MyError;

/**
 * SetDecoration
 */
public class CmdSetDecoration extends CmdScripting {

	private Set<FillType> availableTypes;

	/**
	 * Create new command processor
	 *
	 * @param kernel
	 *            kernel
	 */
	public CmdSetDecoration(Kernel kernel) {
		super(kernel);
		this.availableTypes = getAvailableFillTypes(kernel);
	}

	private Set<FillType> getAvailableFillTypes(Kernel kernel) {
		App app = kernel.getApplication();
		AppConfig config = app.getConfig();
		return config.getAvailableFillTypes();
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 2:
			GeoElement[] arg = resArgs(c);
			if (!arg[1].isNumberValue()) {
				throw argErr(c, arg[1]);
			}

			int style = (int) arg[1].evaluateDouble();

			// For invalid number we assume it's 0
			// We do this also for SetPointStyle

			if (style < 0) {
				style = 0;
			}
			if (arg[0].isGeoAngle() || arg[0].isGeoSegment()) {

				arg[0].setDecorationType(style);
			} else if (arg[0].isFillable()) {
				FillType[] types = FillType.values();
				if (style >= types.length) {
					style = 0;
				}
				FillType type = types[style];
				if (!availableTypes.contains(type) || type == FillType.SYMBOLS
						|| type == FillType.IMAGE) {
					// we could add SetDecoration(poly1, 8 pic1),
					// SetDecoration(poly1, 7, text) for these
					// also SetDecoration(poly1, 1, hatchAngle)
					style = 0;
				}
				arg[0].setFillType(types[style]);
			}

			arg[0].updateRepaint();

			return arg;
		case 3:
			GeoElement[] args = resArgs(c);
			if (!args[1].isNumberValue()) {
				throw argErr(c, args[1]);
			}
			if (!args[2].isNumberValue()) {
				throw argErr(c, args[2]);
			}

			int startStyle = getValidStyle(args[1]);
			int endStyle = getValidStyle(args[2]);

			if (args[0] instanceof GeoSegment) {
				((GeoSegment) args[0]).setStartStyle(SegmentStyle.values()[startStyle]);
				((GeoSegment) args[0]).setEndStyle(SegmentStyle.values()[endStyle]);
			}

			args[0].updateVisualStyleRepaint(GProperty.COMBINED);
			return args;

		default:
			throw argNumErr(c);
		}
	}

	private int getValidStyle(GeoElement arg) {
		int style = (int) arg.evaluateDouble();
		if (style < 0 || style >= SegmentStyle.values().length) {
			style = 0;
		}
		return style;
	}

}

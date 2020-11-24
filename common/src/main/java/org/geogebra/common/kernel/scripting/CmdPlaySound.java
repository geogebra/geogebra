package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.debug.Log;

/**
 * PlaySound
 */
public class CmdPlaySound extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPlaySound(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {

		int n = c.getArgumentNumber();

		boolean[] ok = new boolean[n];
		SoundManager sm = app.getSoundManager();

		if (sm == null) {
			Log.debug("no sound manager available");
			return new GeoElement[0];
		}

		switch (n) {
		case 1:
			GeoElement[] arg = resArgs(c);

			// play a midi file
			if (arg[0].isGeoText()) {
				sm.playFile(arg[0], arg[0]
						.toValueString(StringTemplate.defaultTemplate));
				return arg;
			} else if (arg[0].isGeoAudio()) {
				sm.play((GeoAudio) arg[0]);
				return arg;
			} else if (arg[0].isGeoBoolean()) { // pause/resume current sound
				sm.pauseResumeSound((((GeoBoolean) arg[0]).getBoolean()));
				return arg;
			} else {
				throw argErr(c, arg[0]);
			}

		case 2:
			arg = resArgs(c);

			if (arg[0].isGeoAudio() && arg[1] instanceof GeoBoolean) {

				GeoBoolean playPause = (GeoBoolean) arg[1];
				if (playPause.getBoolean()) {
					sm.play((GeoAudio) arg[0]);
				} else {
					sm.pause((GeoAudio) arg[0]);
				}

				return arg;

			} else if ((ok[0] = arg[0].isGeoNumeric())

					&& (ok[1] = arg[1].isGeoNumeric())) {

				// play a note using args: note and duration
				// using instrument 0 (piano) and velocity 127 (100% of external
				// volume control)
				sm.playSequenceNote((int) ((GeoNumeric) arg[0]).getDouble(),
						((GeoNumeric) arg[1]).getDouble(), 0, 127);

				return arg;
			}

			else if ((ok[0] = arg[0].isGeoText())
					&& (ok[1] = arg[1].isGeoNumeric())) {
				// play a sequence string
				// PlaySound[ <Note Sequence>, <Instrument> ]
				// only works in desktop
				sm.playSequenceFromString(
						(((GeoText) arg[0])
								.toValueString(StringTemplate.defaultTemplate)),
						(int) ((GeoNumeric) arg[1]).getDouble());
				return arg;
			}

			throw argErr(c, getBadArg(ok, arg));

		case 3:
			arg = resArgs(c);

			// play a note using args: note, duration, instrument
			if ((ok[0] = arg[0].isGeoNumeric())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {

				sm.playSequenceNote((int) ((GeoNumeric) arg[0]).getDouble(), // note
						((GeoNumeric) arg[1]).getDouble(), // duration
						(int) ((GeoNumeric) arg[2]).getDouble(), // instrument
						127); // 100% of external volume control

				return arg;
			}

			else if ((ok[0] = arg[0].isGeoFunction())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {

				sm.playFunction(((GeoFunction) arg[0]).threadSafeCopy(), // function
						((GeoNumeric) arg[1]).getDouble(), // min value
						((GeoNumeric) arg[2]).getDouble()); // max value
				return arg;
			}

			throw argErr(c, getBadArg(ok, arg));

		case 5:
			arg = resArgs(c);

			if ((ok[0] = arg[0].isGeoFunction())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())) {

				sm.playFunction(((GeoFunction) arg[0]).threadSafeCopy(), // function
						((GeoNumeric) arg[1]).getDouble(), // min value
						((GeoNumeric) arg[2]).getDouble(), // max value
						(int) ((GeoNumeric) arg[3]).getDouble(), // sample rate
						(int) ((GeoNumeric) arg[4]).getDouble()); // bit depth

				return arg;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}
}

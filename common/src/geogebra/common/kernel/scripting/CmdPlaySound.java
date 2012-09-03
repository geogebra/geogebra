package geogebra.common.kernel.scripting;

import geogebra.common.kernel.CmdScripting;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;
import geogebra.common.sound.SoundManager;

/**
 *PlaySound
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
	protected
	final void perform(Command c) throws MyError {

		int n = c.getArgumentNumber();
		
		boolean[] ok = new boolean[n];
		SoundManager sm = app.getSoundManager();

		switch (n) {
		case 1:
			arg = resArgs(c);

			// play a midi file
			if (arg[0].isGeoText()) {
				sm.playMidiFile(( ((GeoText) arg[0]).toValueString(StringTemplate.defaultTemplate)));
				return;
			}
			// pause/resume current sound
			else if (arg[0].isGeoBoolean()) {
				sm.pauseResumeSound((((GeoBoolean) arg[0]).getBoolean()));
				return;
			}
			else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 2:
			arg = resArgs(c);

			if ( (ok[0] = arg[0].isGeoNumeric()) 
					&& (ok[1] = arg[1].isGeoNumeric())) {

				// play a note using args: note and duration
				// using instrument 0 (piano) and velocity 127 (100% of external volume control) 
				sm.playSequenceNote((int) ((GeoNumeric) arg[0]).getDouble(),
						((GeoNumeric) arg[1]).getDouble(), 0, 127);

				return;
			}

			else if ((ok[0] = arg[0].isGeoText()) 
					&& (ok[1] = arg[1].isGeoNumeric())) {
				// play a sequence string
				sm.playSequenceFromString(( ((GeoText) arg[0]).toValueString(StringTemplate.defaultTemplate)),
						(int) ((GeoNumeric) arg[1]).getDouble());
				return;
			}

			throw argErr(app, c.getName(), getBadArg(ok,arg));

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

				return;
			}

			else if ((ok[0] = arg[0].isGeoFunction()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {

				sm.playFunction(((GeoFunction) arg[0]).threadSafeCopy(), // function
						((GeoNumeric) arg[1]).getDouble(), // min value
						((GeoNumeric) arg[2]).getDouble()); // max value
				return;
			}
			
			
			throw argErr(app, c.getName(), getBadArg(ok,arg));
			

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
						(int)((GeoNumeric) arg[3]).getDouble(), // sample rate
						(int)((GeoNumeric) arg[4]).getDouble()); // bit depth
				
				return;
			} 
			throw argErr(app, c.getName(), getBadArg(ok,arg));
			
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

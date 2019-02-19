package org.geogebra.web.html5.gui.voiceInput.command;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.web.html5.gui.voiceInput.questResErr.QuestResErrInterface;
import org.geogebra.web.html5.gui.voiceInput.questResErr.XCoordQuestResErr;
import org.geogebra.web.html5.gui.voiceInput.questResErr.YCoordQuestResErr;
import org.geogebra.web.html5.main.AppW;

/**
 * @author Csilla
 *
 */
public class VoiceInputSegment implements VoiceInputCommandInterface {

	private ArrayList<QuestResErrInterface> questResList;

	/**
	 * init list of questions needed for a segment
	 */
	public VoiceInputSegment() {
		initQuestList();
	}

	public ArrayList<QuestResErrInterface> getQuestResList() {
		return questResList;
	}

	public void initQuestList() {
		questResList = new ArrayList<>();
		XCoordQuestResErr xCoord1 = new XCoordQuestResErr();
		YCoordQuestResErr yCoord1 = new YCoordQuestResErr();
		questResList.add(xCoord1);
		questResList.add(yCoord1);
		XCoordQuestResErr xCoord2 = new XCoordQuestResErr();
		YCoordQuestResErr yCoord2 = new YCoordQuestResErr();
		questResList.add(xCoord2);
		questResList.add(yCoord2);
	}

	public GeoElement createGeo(AppW appW, ArrayList<Double> inputList) {
		double xCoord1 = inputList.get(0);
		double yCoord1 = inputList.get(1);
		GeoPoint point1 = new GeoPoint(appW.getKernel().getConstruction(), "A",
				xCoord1, yCoord1, 1.0);
		double xCoord2 = inputList.get(0);
		double yCoord2 = inputList.get(1);
		GeoPoint point2 = new GeoPoint(appW.getKernel().getConstruction(), "B",
				xCoord2, yCoord2, 1.0);
		return new GeoSegment(appW.getKernel().getConstruction(), point1,
				point2);
	}

}

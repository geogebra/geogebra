package org.geogebra.web.html5.gui.voiceInput.command;

import java.util.ArrayList;

import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.web.html5.gui.voiceInput.questResErr.QuestResErrInterface;
import org.geogebra.web.html5.gui.voiceInput.questResErr.RadiusQuestResErr;
import org.geogebra.web.html5.gui.voiceInput.questResErr.XCoordQuestResErr;
import org.geogebra.web.html5.gui.voiceInput.questResErr.YCoordQuestResErr;
import org.geogebra.web.html5.main.AppW;

/**
 * @author Csilla
 *
 */
public class VoiceInputCircle implements VoiceInputCommandInterface {

	private ArrayList<QuestResErrInterface> questResList;

	/**
	 * question list needed to create circle
	 */
	public VoiceInputCircle() {
		initQuestList();
	}

	@Override
	public ArrayList<QuestResErrInterface> getQuestResList() {
		return questResList;
	}

	@Override
	public void initQuestList() {
		questResList = new ArrayList<>();
		XCoordQuestResErr xCoord = new XCoordQuestResErr();
		YCoordQuestResErr yCoord = new YCoordQuestResErr();
		RadiusQuestResErr radius = new RadiusQuestResErr();
		questResList.add(xCoord);
		questResList.add(yCoord);
		questResList.add(radius);
	}

	@Override
	public GeoElement createGeo(AppW appW, ArrayList<Double> inputList) {
		double xCoord = inputList.get(0);
		double yCoord = inputList.get(1);
		GeoPoint midlePoint = new GeoPoint(appW.getKernel().getConstruction(),
				"M", xCoord,
				yCoord, 1.0);
		GeoNumeric radius = new GeoNumeric(appW.getKernel().getConstruction(),
				inputList.get(2));
		AlgoCirclePointRadius circleAlgo = new AlgoCirclePointRadius(
				appW.getKernel().getConstruction(), midlePoint,
				radius);
		circleAlgo.getCircle().setLabel("C");
		return circleAlgo.getCircle();
	}
}

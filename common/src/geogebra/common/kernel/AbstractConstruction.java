package geogebra.common.kernel;


import java.util.TreeSet;

import geogebra.common.kernel.algos.AlgoElementInterface;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.main.AbstractApplication;


public abstract class AbstractConstruction {

	public abstract void setSuppressLabelCreation(boolean silentMode);

	public abstract AbstractKernel getKernel();

	public abstract AbstractApplication getApplication();

	public abstract boolean isSuppressLabelsActive();

	public abstract void removeRandomGeo(GeoElementInterface geoNumeric);

	public abstract void addRandomGeo(GeoElementInterface geoNumeric);

	public abstract boolean isFreeLabel(String newLabel);
	
	public abstract void putLabel(GeoElementInterface geo);
	
	public abstract void removeLabel(GeoElementInterface geo);
	public abstract String getIndexLabel(String prefix);
	public abstract int steps();
	public abstract void removeCasCellLabel(String variable);
	public abstract void removeCasCellLabel(String variable,boolean b);

	public abstract void addToConstructionList(ConstructionElement geoElement, boolean b);
	public abstract void removeFromConstructionList(ConstructionElement geoElement);

	public abstract void registerEuclidianViewCE(EuclidianViewCE algo);
	public abstract void unregisterEuclidianViewCE(EuclidianViewCE algo);

	public abstract AbstractConstructionDefaults getConstructionDefaults();

	public abstract void addToAlgorithmList(AlgoElementInterface algoElement);
	public abstract void removeFromAlgorithmList(AlgoElementInterface algoElement);
	
	public abstract TreeSet getGeoSetLabelOrder(GeoClass geoClassType);

	public abstract void setTitle(String title);
	public abstract void setAuthor(String author);
	public abstract void setDate(String date);

	public abstract void setWorksheetText(String below, int i);	

	public abstract boolean notifyEuclidianViewCE();
	public abstract GeoElementInterface lookupLabel(String label, boolean allowAutoCreate);//package private
	public abstract void updateLocalAxesNames();
	
	public abstract ConstructionElement getConstructionElement(int index);
	public abstract int getStep();
	public abstract void setStep(int s);
	public abstract void clearConstruction();
	public abstract void updateConstruction();
	public abstract boolean isEmpty();
}

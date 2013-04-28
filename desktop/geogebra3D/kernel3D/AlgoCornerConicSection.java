package geogebra3D.kernel3D;


import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoCornerConicSection extends AlgoElement3D {


	private GeoConicSection conic;

	private OutputHandler<GeoElement> outputPoints; // output
	
    
	private AlgoIntersectPlaneQuadricLimited algoParent;

	

	
	
    public AlgoCornerConicSection(Construction c, String[] labels,
    		GeoConicSection conic) {
    	super(c);
    	

		this.conic = conic;
		algoParent = (AlgoIntersectPlaneQuadricLimited) conic.getParentAlgorithm();
		
		
		outputPoints=createOutputPoints();

       
        setInputOutput(); // for AlgoElement
        
        compute();
        
        setLabels(labels);
        //update();    
        
        compute();
    	
	}
    
    

	private OutputHandler<GeoElement> createOutputPoints(){
    	return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p=new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setUndefined();
				p.setParentAlgorithm(AlgoCornerConicSection.this);
				return p;
			}
		});
    }
	



	@Override
	public void compute() {
    	
		//first check if input is defined
    	if ( !conic.isDefined() || conic.getType()==GeoConicNDConstants.CONIC_EMPTY ){
    		for(int index = 0 ; index<outputPoints.size() ; index++){
        		outputPoints.getElement(index).setUndefined();
    		}
    		return;
    	}
    	
    	/*
    	GeoPointND point = (GeoPointND) outputPoints.getElement(0);
    	point.set(algoParent.getBottomPoint(0));
    	point.updateCoords();
    	*/
    	
    	setPoint(0, algoParent.getBottomPoint(0));
    	setPoint(1, algoParent.getBottomPoint(1));
    	setPoint(2, algoParent.getTopPoint(0));
    	setPoint(3, algoParent.getTopPoint(1));
    	
    	
    	
		/*
		setPoint(conic.getParameterStart(0), 0);
		setPoint(conic.getParameterEnd(0),   1);
		setPoint(conic.getParameterStart(1), 2);
		setPoint(conic.getParameterEnd(1),   3);
		*/
    	
    	/*
    	//update and/or create points
    	int index = 0;   	
    	//affect new computed points
    	outputPoints.adjustOutputSize(newCoords.size());
    	for (Coords coords : newCoords.values()){
    	*/
    	

    		
    		/*
    		index++;
    	}
    	//other points are undefined
    	for(;index<outputPoints.size();index++)
    		outputPoints.getElement(index).setUndefined();
    		*/

    }
	
	private void setPoint(int index, GeoPoint3D p){
		

		if (!p.isDefined()){
			if (outputPoints.size() > index){
				outputPoints.getElement(index).setUndefined();
			}
			return;
		}
		
		if (outputPoints.size() <= index){
			outputPoints.adjustOutputSize(index+1, false);
		}
		

	  	GeoPointND point = (GeoPointND) outputPoints.getElement(index);
	  	point.setCoords(p.getCoords(),false);
	  	//point.updateCoords();

		
		
	}
	/*
	private void setPoint(double parameter, int index){

		App.debug(index+": "+parameter);
		if (Double.isNaN(parameter)){
			outputPoints.getElement(index).setUndefined();
			return;
		}
		
    	pp.setT(parameter);
    	conic.pathChangedWithoutCheck(coords, pp);
    	GeoPointND point = (GeoPointND) outputPoints.getElement(index);
    	point.setCoords(conic.getCoordSys().getPoint(coords),false);
    	point.updateCoords();
	}
    */
	
	
    @Override
	public Commands getClassName() {
		return Commands.Corner;
	}

    private void setLabels(String[] labels) {
    	//if only one label (e.g. "A") for more than one output, new labels will be A_1, A_2, ...
    	if (labels!=null &&
    			labels.length==1 &&
    			outputPoints.size() > 1 &&
    			labels[0]!=null &&
    			!labels[0].equals("")) {
    		outputPoints.setIndexLabels(labels[0]);


    	} else {
    		outputPoints.setLabels(labels);
    	}

    }
	
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = conic;
        
        setDependencies(); // done by AlgoElement
    }
    
    /**
     * 
     * @return corners
     */
    public GeoPoint3D[] getCorners(){
    	GeoPoint3D[] ret = new GeoPoint3D[outputPoints.size()];
    	outputPoints.getOutput(ret);
    	return ret;
    }


}

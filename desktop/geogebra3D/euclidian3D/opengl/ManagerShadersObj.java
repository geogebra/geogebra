package geogebra3D.euclidian3D.opengl;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * manager for shaders + obj files
 * 
 * @author mathieu
 *
 */
public class ManagerShadersObj extends ManagerShaders {
	
	/**
	 * constructor
	 * @param renderer renderer
	 * @param view3D 3D view
	 */
	public ManagerShadersObj(Renderer renderer, EuclidianView3D view3D) {
		super(renderer,view3D);
		
	}
	
	

	private int objCurrentIndex;
	
	private BufferedWriter objBufferedWriter;
	
	/**
	 * start .obj file (set writer and vertex index)
	 * @param writer .obj file writer
	 */
	public void startObjFile(BufferedWriter writer){
		objCurrentIndex = 0;
		objBufferedWriter = writer;
	}

	@Override
	public void drawInObjFormat(GeoElement geo, int index){

		try{
			currentGeometriesSet = geometriesSetList.get(index);
			if (currentGeometriesSet != null){
				for (Geometry geometry : currentGeometriesSet){

					printToObjFile("\n##########################\n\no "+geo.getLabelSimple()+"\n");

					switch(geometry.getType()){
					case QUADS:										

						//vertices
						GLBuffer fb = geometry.getVertices();
						for (int i = 0; i < geometry.getLength(); i++){
							printToObjFile("\nv");
							for (int j = 0; j < 3; j++){
								printToObjFile(" "+fb.get());
							}
						}
						fb.rewind();

						/*
					//normals
					printToObjFile("\n");
					fb = geometry.getNormals();
					for (int i = 0; i < geometry.getLength(); i++){
						printToObjFile("\nvn");
						for (int j = 0; j < 3; j++){
							printToObjFile(" "+fb.get());
						}
					}
					fb.rewind();
						 */

						//faces
						printToObjFile("\n");
						for (int i = 0; i < geometry.getLength()/4; i++){
							printToObjFile("\nf");
							for (int j = 0; j < 4; j++){
								objCurrentIndex++;
								//printToObjFile(" "+objCurrentIndex+"//"+objCurrentIndex);
								printToObjFile(" "+objCurrentIndex);
							}
						}

						printToObjFile("\n##########################\n\n");
						break;

					case QUAD_STRIP:										

						//vertices
						fb = geometry.getVertices();
						for (int i = 0; i < geometry.getLength(); i++){
							printToObjFile("\nv");
							for (int j = 0; j < 3; j++){
								printToObjFile(" "+fb.get());
							}
						}
						fb.rewind();

						/*
					//normals
					printToObjFile("\n");
					fb = geometry.getNormals();
					for (int i = 0; i < geometry.getLength(); i++){
						printToObjFile("\nvn");
						for (int j = 0; j < 3; j++){
							printToObjFile(" "+fb.get());
						}
					}
					fb.rewind();
						 */

						//faces
						printToObjFile("\n");
						for (int i = 0; i < geometry.getLength()/2 - 1; i++){
							printToObjFile("\nf");
							printToObjFile(" "
									+ (objCurrentIndex+1) + " "
									+ (objCurrentIndex+2) + " "
									+ (objCurrentIndex+4) + " "
									+ (objCurrentIndex+3)
									);

							objCurrentIndex += 2;
						}

						objCurrentIndex += 2; // last shift
						printToObjFile("\n##########################\n\n");
						break;

					default:
						App.error("geometry type not handled : "+geometry.getType());
						break;
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	private void printToObjFile(String s) throws IOException{
		//System.out.print(s);
		objBufferedWriter.write(s);
	}
	
	
}

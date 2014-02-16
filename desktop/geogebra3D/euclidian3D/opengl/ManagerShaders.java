package geogebra3D.euclidian3D.opengl;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;

import javax.media.opengl.glu.GLUtessellator;


/**
 * 
 * Manager using shaders
 * 
 * @author ggb3D
 *
 */
public class ManagerShaders extends ManagerD {
	
	// GL 
	private GLUtessellator tesselator;

	protected RendererShaders renderer;
	

	/** common constructor
	 * @param renderer 
	 * @param view3D 3D view
	 */
	public ManagerShaders(Renderer renderer, EuclidianView3D view3D) {
		super(renderer,view3D);
		
		
		
	}
	

	/**
	 * creates a float buffer from the array
	 * @param array float array
	 * @return float buffer
	 */
	static final public FloatBuffer floatBuffer(ArrayList<Float> array){
		FloatBuffer fb= FloatBuffer.allocate(array.size());
		for (int i = 0; i < array.size(); i++){
			fb.put(array.get(i));
		}
		fb.rewind();
		return fb;
	}
	
	/**
	 * creates a float buffer from the array
	 * @param array float array
	 * @param repetition values are repeted
	 * @return float buffer
	 */
	static final public FloatBuffer floatBuffer(ArrayList<Float> array, int repetition){
		FloatBuffer fb= FloatBuffer.allocate(array.size() * repetition);
		for (int j = 0; j < repetition; j++){
			for (int i = 0; i < array.size(); i++){
				fb.put(array.get(i));
			}
		}
		fb.rewind();
		return fb;
	}
	
	private ArrayList<Float> vertices, normals, textures, colors;
	
	@Override
	protected void initGeometriesList(){
		geometriesSetList = new TreeMap<Integer, ManagerShaders.GeometriesSet>();
		geometriesSetMaxIndex = -1;
		indicesRemoved = new Stack<Integer>();
		
		vertices = new ArrayList<Float>();
		normals = new ArrayList<Float>();
		textures = new ArrayList<Float>();
		colors = new ArrayList<Float>();
		
	}

	@Override
	protected void setRenderer(Renderer renderer){
		this.renderer = (RendererShaders) renderer;
	}
	
	@Override
	protected RendererShaders getRenderer(){
		return renderer;
	}

	
	/////////////////////////////////////////////
	// LISTS METHODS
	/////////////////////////////////////////////
	
	
	/**
	 * Geometry (set of vertices, normals, etc. for e.g. triangles)
	 * @author mathieu
	 *
	 */
	private class Geometry{
		/**
		 * type of primitives
		 */
		private Type type;
		
		private FloatBuffer v, n, t, c;
		
		private int length;
		
		
		/**
		 * Start a new geometry
		 * @param type of primitives
		 */
		public Geometry(Type type){
			this.type = type;
		}
		
		/**
		 * 
		 * @return type of primitives
		 */
		public Type getType(){
			return type;
		}
		

		/**
		 * set float buffer for vertices
		 * @param fb float buffer
		 */
		public void setVertices(FloatBuffer fb){
			this.v = fb;
		}
		
		/**
		 * 
		 * @return vertices buffer
		 */
		public FloatBuffer getVertices(){
			return v;
		}
		
		/**
		 * set float buffer for normals
		 * @param fb float buffer
		 */
		public void setNormals(FloatBuffer fb){
			this.n = fb;
		}
		
		/**
		 * 
		 * @return normals buffer
		 */
		public FloatBuffer getNormals(){
			return n;
		}
		
		/**
		 * set float buffer for texture
		 * @param fb float buffer
		 */
		public void setTextures(FloatBuffer fb){
			this.t = fb;
		}
		
		

		
		/**
		 * 
		 * @return texture buffer
		 */
		public FloatBuffer getTextures(){
			return t;
		}

		
		/**
		 * set float buffer for colors
		 * @param fb float buffer
		 */
		public void setColors(FloatBuffer fb){
			this.c = fb;
		}
		
		
		/**
		 * 
		 * @return colors buffer
		 */
		public FloatBuffer getColors(){
			return c;
		}


		
		/**
		 * set vertices length
		 * @param l vertices length
		 */
		public void setLength(int l){
			this.length = l;
		}
		
		/**
		 * 
		 * @return vertices length
		 */
		public int getLength(){
			return length;
		}

	}
	
	/**
	 * Set of geometries
	 * @author mathieu
	 *
	 */
	private class GeometriesSet extends ArrayList<Geometry>{
		
		private Geometry currentGeometry;
		

		public GeometriesSet() {
			
		}
		
		/**
		 * start a new geometry
		 * @param type type of primitives
		 */
		public void startGeometry(Type type){
			currentGeometry = new Geometry(type);
			add(currentGeometry);
		}
		
		/**
		 * set vertices for current geometry
		 * @param vertices vertices
		 */
		public void setVertices(ArrayList<Float> vertices){
			currentGeometry.setVertices(ManagerShaders.floatBuffer(vertices));
			currentGeometry.setLength(vertices.size()/3);
		}
		
		public void setNormals(ArrayList<Float> normals){
			if (normals.size() == 3){ // only one normal for all vertices
				//currentGeometry.setNormals(ManagerShaders.floatBuffer(normals, currentGeometry.getLength()));
				currentGeometry.setNormals(ManagerShaders.floatBuffer(normals));
			}else if (normals.size() == 3 * currentGeometry.getLength()){
				currentGeometry.setNormals(ManagerShaders.floatBuffer(normals));
			}
		}
		
		public void setTextures(ArrayList<Float> textures){
			if (textures.size() == 2 * currentGeometry.getLength()){
				currentGeometry.setTextures(ManagerShaders.floatBuffer(textures));
			}
		}
		
		public void setColors(ArrayList<Float> colors){
			if (colors.size() == 4 * currentGeometry.getLength()){
				currentGeometry.setColors(ManagerShaders.floatBuffer(colors));
			}
		}
	
		
	}
	
	private TreeMap<Integer, GeometriesSet> geometriesSetList;
	
	private int geometriesSetMaxIndex;
	
	private GeometriesSet currentGeometriesSet;
	
	private Stack<Integer> indicesRemoved;
	

	@Override
	public int startNewList(){
		
		int index;
		if (indicesRemoved.empty()){
			geometriesSetMaxIndex++;
			index = geometriesSetMaxIndex;
		}else{
			index = indicesRemoved.pop();
		}
		
		currentGeometriesSet = new GeometriesSet();
		geometriesSetList.put(index, currentGeometriesSet);
		
		return index;
	}
	
	
	@Override
	public void endList(){	
		//renderer.getGL2().glEndList();
	}
	
	
	/////////////////////////////////////////////
	// GEOMETRY METHODS
	/////////////////////////////////////////////

	
	@Override
	public void startGeometry(Type type){
		currentGeometriesSet.startGeometry(type);
		vertices.clear();
		normals.clear();
		textures.clear();
		colors.clear();
	}
	
	@Override
	public void endGeometry(){
		currentGeometriesSet.setVertices(vertices);
		currentGeometriesSet.setNormals(normals);
		currentGeometriesSet.setTextures(textures);
		currentGeometriesSet.setColors(colors);
		//currentGeometriesSet.endGeometry();
	}
	
	
	/////////////////////////////////////////////
	// POLYGONS METHODS
	/////////////////////////////////////////////
	

    
	@Override
	public int startPolygons(){
		
    	int index = startNewList();
    	
	    return index;
	    
	}
    
    @Override
	public void drawPolygon(Coords n, Coords[] v){
    	
    	startGeometry(Manager.Type.TRIANGLES);
    	
    	// set normal
    	normal(n);
       	
       	for (int i = 0 ; i < 3/*v.length*/ ; i++){
      		vertex(v[i]);
       	}
       	
       	endGeometry();
    
    }
    

    
    
    @Override
	public void endPolygons(){
    	endList();
    }
    
    
 
    @Override
	public void remove(int index){
    	
    	if (index >= 0){ // negative index is for no geometry
    		indicesRemoved.push(index);
    		geometriesSetList.remove(index);
    	}
    }
	
	
	/////////////////////////////////////////////
	// DRAWING METHODS
	/////////////////////////////////////////////

	@Override
	public void draw(int index){

		currentGeometriesSet = geometriesSetList.get(index);
		if (currentGeometriesSet != null){
			for (Geometry geometry : currentGeometriesSet){
				renderer.loadVertexBuffer(geometry.getVertices(), geometry.getLength());
				renderer.loadNormalBuffer(geometry.getNormals(), geometry.getLength());
				renderer.loadColorBuffer(geometry.getColors(), geometry.getLength());
				if (renderer.areTexturesEnabled()){
					renderer.loadTextureBuffer(geometry.getTextures(), geometry.getLength());	
				}
				renderer.draw(geometry.getType(), geometry.getLength());
			}
		}
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
						FloatBuffer fb = geometry.getVertices();
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
	
	
	@Override
	protected void texture(float x, float y){		
		
		textures.add(x);
		textures.add(y);
	}
	
	
	@Override
	protected void setDummyTexture(){
		//nothing needed for the shader
	}
	
	
	@Override
	protected void normal(float x, float y, float z){
		
		normals.add(x);
		normals.add(y);
		normals.add(z);
		
	}
		
	@Override
	protected void vertex(float x, float y, float z){
		
		vertices.add(x);
		vertices.add(y);
		vertices.add(z);
	}
	
	@Override
	protected void vertexInt(int x, int y, int z){		
		vertex(x,y,z);
	}

	
	
	@Override
	protected void color(float r, float g, float b){
		color(r,g,b,1f);
	}
	
	@Override
	protected void color(float r, float g, float b, float a){
		colors.add(r);
		colors.add(g);
		colors.add(b);
		colors.add(a);
	}
	
	@Override
	protected void lineWidth(float width){
		renderer.getGL().glLineWidth(width);
	}
	
	@Override
	protected void pointSize(float size){
		//renderer.getGL2().glPointSize(size);
	}

	@Override
	protected void vertices(double[] vertices) {
		// TODO Auto-generated method stub		
	}
	
	
	@Override
	public void rectangle(int x, int y, int z, int width, int height){
		
		currentGeometriesSet = new GeometriesSet();
		startGeometry(Manager.Type.QUADS);
		texture(0, 0);
		vertexInt(x,y,z); 
		texture(1, 0);
		vertexInt(x+width,y,z); 
		texture(1, 1);
		vertexInt(x+width,y+height,z); 
		texture(0, 1);
		vertexInt(x,y+height,z); 	
		endGeometry();
		
		for (Geometry geometry : currentGeometriesSet){
			renderer.loadVertexBuffer(geometry.getVertices(), geometry.getLength());
			//renderer.loadNormalBuffer(geometry.getNormals(), geometry.getLength());
			if (renderer.areTexturesEnabled()){
				renderer.loadTextureBuffer(geometry.getTextures(), geometry.getLength());	
			}
			renderer.draw(geometry.getType(), geometry.getLength());
		}
	}

	@Override
	public void rectangleBounds(int x, int y, int z, int width, int height){
		getText().rectangleBounds(x, y, z, width, height);
	}


}

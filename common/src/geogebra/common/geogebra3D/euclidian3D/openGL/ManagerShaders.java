package geogebra.common.geogebra3D.euclidian3D.openGL;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.kernel.Matrix.Coords;

import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;


/**
 * 
 * Manager using shaders
 * 
 * @author ggb3D
 *
 */
public class ManagerShaders extends Manager {
	

	protected Renderer renderer;
	

	/** common constructor
	 * @param renderer 
	 * @param view3D 3D view
	 */
	public ManagerShaders(Renderer renderer, EuclidianView3D view3D) {
		super(renderer,view3D);
		
		
		
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
		this.renderer = renderer;
	}
	
	@Override
	protected Renderer getRenderer(){
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
	protected class Geometry{
		/**
		 * type of primitives
		 */
		private Type type;
		
		private GLBuffer v, n, t, c;
		
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
		public void setVertices(GLBuffer fb){
			this.v = fb;
		}
		
		/**
		 * 
		 * @return vertices buffer
		 */
		public GLBuffer getVertices(){
			return v;
		}
		
		/**
		 * set float buffer for normals
		 * @param fb float buffer
		 */
		public void setNormals(GLBuffer fb){
			this.n = fb;
		}
		
		/**
		 * 
		 * @return normals buffer
		 */
		public GLBuffer getNormals(){
			return n;
		}
		
		/**
		 * set float buffer for texture
		 * @param fb float buffer
		 */
		public void setTextures(GLBuffer fb){
			this.t = fb;
		}
		
		

		
		/**
		 * 
		 * @return texture buffer
		 */
		public GLBuffer getTextures(){
			return t;
		}

		
		/**
		 * set float buffer for colors
		 * @param fb float buffer
		 */
		public void setColors(GLBuffer fb){
			this.c = fb;
		}
		
		
		/**
		 * 
		 * @return colors buffer
		 */
		public GLBuffer getColors(){
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
	protected class GeometriesSet extends ArrayList<Geometry>{
		
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
			currentGeometry.setVertices(GLFactory.prototype.newBuffer(vertices));
			currentGeometry.setLength(vertices.size()/3);
		}
		
		public void setNormals(ArrayList<Float> normals){
			if (normals.size() == 3){ // only one normal for all vertices
				//currentGeometry.setNormals(ManagerShaders.floatBuffer(normals, currentGeometry.getLength()));
				currentGeometry.setNormals(GLFactory.prototype.newBuffer(normals));
			}else if (normals.size() == 3 * currentGeometry.getLength()){
				currentGeometry.setNormals(GLFactory.prototype.newBuffer(normals));
			}
		}
		
		public void setTextures(ArrayList<Float> textures){
			if (textures.size() == 2 * currentGeometry.getLength()){
				currentGeometry.setTextures(GLFactory.prototype.newBuffer(textures));
			}
		}
		
		public void setColors(ArrayList<Float> colors){
			if (colors.size() == 4 * currentGeometry.getLength()){
				currentGeometry.setColors(GLFactory.prototype.newBuffer(colors));
			}
		}
	
		
	}
	
	protected TreeMap<Integer, GeometriesSet> geometriesSetList;
	
	private int geometriesSetMaxIndex;
	
	protected GeometriesSet currentGeometriesSet;
	
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
				((RendererShadersInterface) renderer).loadVertexBuffer(geometry.getVertices(), geometry.getLength());
				((RendererShadersInterface) renderer).loadNormalBuffer(geometry.getNormals(), geometry.getLength());
				((RendererShadersInterface) renderer).loadColorBuffer(geometry.getColors(), geometry.getLength());
				if (((RendererShadersInterface) renderer).areTexturesEnabled()){
					((RendererShadersInterface) renderer).loadTextureBuffer(geometry.getTextures(), geometry.getLength());	
				}
				((RendererShadersInterface) renderer).draw(geometry.getType(), geometry.getLength());
			}
		}
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
			((RendererShadersInterface) renderer).loadVertexBuffer(geometry.getVertices(), geometry.getLength());
			//renderer.loadNormalBuffer(geometry.getNormals(), geometry.getLength());
			if (((RendererShadersInterface) renderer).areTexturesEnabled()){
				((RendererShadersInterface) renderer).loadTextureBuffer(geometry.getTextures(), geometry.getLength());	
			}
			((RendererShadersInterface) renderer).draw(geometry.getType(), geometry.getLength());
		}
	}

	@Override
	public void rectangleBounds(int x, int y, int z, int width, int height){
		getText().rectangleBounds(x, y, z, width, height);
	}


}

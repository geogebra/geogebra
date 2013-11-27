package geogebra3D.euclidian3D.opengl;

import geogebra.common.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.EuclidianView3D;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.media.opengl.glu.GLUtessellator;

/**
 * 
 * Manager using shaders
 * 
 * @author ggb3D
 *
 */
public class ManagerShaders extends Manager {
	
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
	
	private ArrayList<Float> vertices, normals, textures;
	
	@Override
	protected void initGeometriesList(){
		geometriesSetList = new TreeMap<Integer, ManagerShaders.GeometriesSet>();
		geometriesSetMaxIndex = -1;
		
		vertices = new ArrayList<Float>();
		normals = new ArrayList<Float>();
		textures = new ArrayList<Float>();
		
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
		private int type;
		
		private FloatBuffer v, n, t;
		
		private int length;
		
		
		/**
		 * Start a new geometry
		 * @param type of primitives
		 */
		public Geometry(int type){
			this.type = type;
		}
		
		/**
		 * 
		 * @return type of primitives
		 */
		public int getType(){
			return type;
		}
		
		public void end(){
			
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
		public void startGeometry(int type){
			currentGeometry = new Geometry(type);
			add(currentGeometry);
		}
		
		/**
		 * end the current geometry
		 */
		public void endGeometry(){
			currentGeometry.end();
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
			if (!normals.isEmpty()){
				currentGeometry.setNormals(ManagerShaders.floatBuffer(normals));
			}
		}
		
		public void setTextures(ArrayList<Float> textures){
			if (!textures.isEmpty()){
				currentGeometry.setTextures(ManagerShaders.floatBuffer(textures));
			}
		}
		
	
		
	}
	
	private TreeMap<Integer, GeometriesSet> geometriesSetList;
	
	private int geometriesSetMaxIndex;
	
	private GeometriesSet currentGeometriesSet;
	

	@Override
	public int startNewList(){
		geometriesSetMaxIndex++;
		currentGeometriesSet = new GeometriesSet();
		geometriesSetList.put(geometriesSetMaxIndex, currentGeometriesSet);
		
		return geometriesSetMaxIndex;
	}
	
	
	@Override
	public void endList(){	
		//renderer.getGL2().glEndList();
	}
	
	
	/////////////////////////////////////////////
	// GEOMETRY METHODS
	/////////////////////////////////////////////

	
	@Override
	public void startGeometry(int type){
		currentGeometriesSet.startGeometry(type);
		vertices.clear();
		normals.clear();
		textures.clear();
	}
	
	@Override
	public void endGeometry(){
		currentGeometriesSet.setVertices(vertices);
		currentGeometriesSet.setNormals(normals);
		currentGeometriesSet.setTextures(textures);
		currentGeometriesSet.endGeometry();
	}
	
	
	/////////////////////////////////////////////
	// POLYGONS METHODS
	/////////////////////////////////////////////
	

	
    
	@Override
	public int startPolygons(){
	    
	    return -1;
	}
    
    @Override
	public void drawPolygon(Coords n, Coords[] v){
    	
    	
    }
    
    @Override
	public void endPolygons(){
    	
    }
    
    
    /** remove the polygon from gl memory
     * @param index
     */
    @Override
	public void remove(int index){
    	
    	//renderer.getGL2().glDeleteLists(index, 1);  	
    }
	
	
	/////////////////////////////////////////////
	// DRAWING METHODS
	/////////////////////////////////////////////

	@Override
	public void draw(int index){
		
		/*
		vertices.clear();
		vertices.add(0f);vertices.add(0f);vertices.add(0f);
		vertices.add(0f);vertices.add(1f);vertices.add(0f);
		vertices.add(0f);vertices.add(0f);vertices.add(1f);
		
		FloatBuffer fbVertices = floatBuffer(vertices);
				
		renderer.loadVertexBuffer(fbVertices, 3*3);//, normals, textureCoords);
		renderer.draw(Manager.TRIANGLES, 3);
		*/
		
		currentGeometriesSet = geometriesSetList.get(index);
		if (currentGeometriesSet != null){
			for (Geometry geometry : currentGeometriesSet){
				renderer.loadVertexBuffer(geometry.getVertices(), geometry.getLength());
				renderer.loadNormalBuffer(geometry.getNormals(), geometry.getLength());
				if (renderer.areTexturesEnabled()){
					renderer.loadTextureBuffer(geometry.getTextures(), geometry.getLength());	
				}
				renderer.draw(geometry.getType(), geometry.getLength());
			}
		}
	}
	
	@Override
	protected void texture(float x, float y){		
		
		textures.add(x);
		textures.add(y);
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
		
		//renderer.getGL2().glVertex3i(x,y,z); 	
	}

	
	
	@Override
	protected void color(float r, float g, float b){
		//renderer.getGL2().glColor3f(r,g,b);
	}
	
	@Override
	protected void color(float r, float g, float b, float a){
		//renderer.getGL2().glColor4f(r,g,b,a);
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
	
	
	


}

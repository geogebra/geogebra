package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import org.geogebra.common.util.StringUtil;

/**
 * adapted from three.js loading collada example (MIT licence)
 * 
 * 
 *
 */
public class FormatColladaHTML extends FormatCollada {

	private double xmin;
	private double ymin;
	private double zmin;
	private double xmax;
	private double ymax;
	private double zmax;

	@Override
	public String getExtension() {
		return "html";
	}

	@Override
	public void getScriptStart(StringBuilder sb) {
		xmin = Double.POSITIVE_INFINITY;
		ymin = Double.POSITIVE_INFINITY;
		zmin = Double.POSITIVE_INFINITY;
		xmax = Double.NEGATIVE_INFINITY;
		ymax = Double.NEGATIVE_INFINITY;
		zmax = Double.NEGATIVE_INFINITY;

		sb.append("<!DOCTYPE html>\n");
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append("<div id='container'></div>\n");
		sb.append("<button onclick='svgSnapshot()'>Export SVG</button>\n");
		sb.append(
				"<a href='javascript:void(0)' id='dlbtn'><button>Export STL</button></a>\n");

		sb.append("<div style='display: none;' id='svg'></div>\n");
		sb.append("<img id='svg2'></img>\n");
		sb.append(
				"<script src='https://cdnjs.cloudflare.com/ajax/libs/three.js/92/three.min.js'></script>\n");
		sb.append(
				"<script src='https://cdn.rawgit.com/mrdoob/three.js/r92/examples/js/loaders/ColladaLoader.js'></script>\n");
		sb.append(
				"<script src='https://cdn.rawgit.com/mrdoob/three.js/r92/examples/js/controls/OrbitControls.js'></script>\n");
		sb.append(
				"<script src='https://cdn.rawgit.com/mrdoob/three.js/r92/examples/js/renderers/SVGRenderer.js'></script>\n");
		sb.append(
				"<script src='https://cdn.rawgit.com/mrdoob/three.js/r92/examples/js/renderers/Projector.js'></script>\n");
		sb.append(
				"<script src='https://cdn.rawgit.com/mrdoob/three.js/r92/examples/js/exporters/STLExporter.js'></script>\n");

		sb.append("<script>\n");
		sb.append("var container, clock;\n");
		sb.append("var camera, scene, renderer, ggbExport, group;\n");
		sb.append("init();\n");
		sb.append("animate();\n");
		sb.append("function init() {\n");
		sb.append("container = document.getElementById( 'container' );\n");
		sb.append("scene = new THREE.Scene();\n");
		sb.append("clock = new THREE.Clock();\n");

		sb.append("var colladaDataURI = '");
		sb.append(StringUtil.txtMarker);
		super.getScriptStart(sb);
	}

	@Override
	public void getScriptEnd(StringBuilder sb) {
		super.getScriptEnd(sb);

		sb.append("';\n");
		double xd = xmax - xmin;
		double yd = ymax - ymin;
		double zd = zmax - zmin;
		double radius = Math.sqrt(xd * xd + yd * yd + zd * zd) / 200; // meters
		double cameraPosition = radius * 1.5;
		double cameraDistance = cameraPosition * Math.sqrt(3);
		sb.append("camera = new THREE.PerspectiveCamera( 45,");
		sb.append(" window.innerWidth / window.innerHeight,");
		sb.append((cameraDistance - radius) * 0.99);
		sb.append(",");
		sb.append((cameraDistance + radius) * 1.01);
		sb.append(");\n");
		sb.append("camera.position.set(");
		sb.append(cameraPosition);
		sb.append(",");
		sb.append(cameraPosition);
		sb.append(",");
		sb.append(cameraPosition);
		sb.append(");\n");
		sb.append("camera.lookAt( new THREE.Vector3(0,0,0) );\n");
		sb.append("group = new THREE.Group();\n");
		sb.append(
				"var loadingManager = new THREE.LoadingManager( function() {\n");
		sb.append("scene.add( group );\n");
		sb.append("group.add( ggbExport );\n");
		sb.append("ggbExport.translateX(");
		sb.append(-(xmin + xmax) / 200); // meters
		sb.append(");\n");
		sb.append("ggbExport.translateY(");
		sb.append(-(ymin + ymax) / 200); // meters
		sb.append(");\n");
		sb.append("ggbExport.translateZ(");
		sb.append(-(zmin + zmax) / 200); // meters
		sb.append(");\n");
		sb.append("} );\n");

		sb.append("var loader = new THREE.ColladaLoader( loadingManager );\n");
		sb.append("loader.load( colladaDataURI, function ( collada ) {\n");
		sb.append("ggbExport = collada.scene;\n");
		sb.append("} );\n");

		sb.append(
				"var ambientLight = new THREE.AmbientLight( 0xcccccc, 0.4 );\n");
		sb.append("scene.add( ambientLight );\n");
		sb.append(
				"var directionalLight = new THREE.DirectionalLight( 0xffffff, 0.8 );\n");
		sb.append("directionalLight.position.set( 1, 1, 0 ).normalize();\n");
		sb.append("scene.add( directionalLight );\n");

		sb.append("renderer = new THREE.WebGLRenderer();\n");
		sb.append("renderer.setClearColor( 0xffffff, 1);\n");
		sb.append("renderer.setPixelRatio( window.devicePixelRatio );\n");
		sb.append(
				"renderer.setSize( window.innerWidth, window.innerHeight );\n");
		sb.append("container.appendChild( renderer.domElement );\n");

		sb.append(
				"controls = new THREE.OrbitControls( camera, renderer.domElement );\n");
		// controls.addEventListener( 'change', render ); // call this only in
		// static scenes (i.e., if there is no animation loop)
		// an animation loop is required when either damping or auto-rotation
		// are enabled
		sb.append("controls.enableDamping = true;\n");
		sb.append("controls.dampingFactor = 0.25;\n");
		sb.append("controls.screenSpacePanning = false;\n");
		sb.append("controls.minDistance = 0.0001;\n");
		sb.append("controls.maxDistance = 500\n");
		sb.append("controls.maxPolarAngle = Math.PI / 2;\n");

		sb.append("setTimeout(exportSTL,50);\n");

		sb.append("}\n");
		sb.append("function animate() {\n");
		sb.append("requestAnimationFrame( animate );\n");
		sb.append("render();\n");
		sb.append("}\n");
		sb.append("function render() {\n");
		sb.append("var delta = clock.getDelta();\n");
		sb.append("group.rotation.y += delta * 0.5;\n");
		sb.append("renderer.render( scene, camera );\n");
		sb.append("}\n");

		/*
		 * The following discussion on StackOverflow shows discusses how to
		 * remove all elements from a DOM
		 *
		 * http://stackoverflow.com/questions/3955229/remove-all-child-elements-
		 * of-a-dom-node-in-javascript
		 */

		sb.append("function removeChildrenFromNode(node) {\n");
		sb.append("var fc = node.firstChild;\n");

		sb.append("while( fc ) {\n");
		sb.append("node.removeChild( fc );\n");
		sb.append("fc = node.firstChild;\n");
		sb.append("}\n");
		sb.append("}\n");

		sb.append("function svgSnapshot() {\n");
		sb.append("var svgContainer = document.getElementById('svg');\n");
		sb.append("removeChildrenFromNode(svgContainer);\n");

		sb.append("var width = window.innerWidth;\n");
		sb.append("var height = window.innerHeight;\n");

		sb.append("svgRenderer = new THREE.SVGRenderer();\n");
		sb.append("svgRenderer.setClearColor( 0xffffff );\n");
		sb.append("svgRenderer.setSize(width,height );\n");
		sb.append("svgRenderer.setQuality( 'high' );\n");
		sb.append("svgContainer.appendChild( svgRenderer.domElement );\n");
		sb.append("svgRenderer.render( scene, camera );\n");

		/*
		 * The following discussion shows how to scale an SVG to fit its
		 * contained
		 *
		 * http://stackoverflow.com/questions/4737243/fit-svg-to-the-size-of-
		 * object-container
		 *
		 * Another useful primer is here
		 * https://sarasoueidan.com/blog/svg-coordinate-systems/
		 */
		// svgRenderer.domElement.removeAttribute("width");
		// svgRenderer.domElement.removeAttribute("height");

		sb.append("var svg = svgContainer.innerHTML;\n");

		sb.append(
				"svg = svg.replace('<svg ', '<svg id=\"svg2\" xmlns=\"http://www.w3.org/2000/svg\" ');\n");

		// sb.append("svg = svg.replace(/<path/g,'\\n<path');\n");

		sb.append("var svgDataURI = '");
		sb.append(StringUtil.svgMarker);
		sb.append("' + btoa(unescape(encodeURIComponent(svg)));\n");

		sb.append("document.getElementById('svg2').src = svgDataURI;\n");
		// sb.append("console.log(svgDataURI);\n");

		sb.append("}\n");

		sb.append("function create(text, name, type) {\n");
		sb.append("var dlbtn = document.getElementById('dlbtn');\n");
		sb.append("var file = new Blob([text], {type: type});\n");
		sb.append("dlbtn.href = URL.createObjectURL(file);\n");
		sb.append(" dlbtn.download = name;\n");
		sb.append("}\n");

		sb.append("function exportSTL() {\n");
		sb.append("var export_stl = new THREE.STLExporter();\n");
		sb.append("var output = export_stl.parse(scene);\n");
		sb.append("create(output, 'output.stl', 'text/plain');\n");
		sb.append("}\n");

		sb.append("</script>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");
	}

	@Override
	public void getVertices(StringBuilder sb, double x, double y, double z) {
		super.getVertices(sb, x, y, z);

		if (xmin > x) {
			xmin = x;
		}
		if (ymin > y) {
			ymin = y;
		}
		if (zmin > z) {
			zmin = z;
		}
		if (xmax < x) {
			xmax = x;
		}
		if (ymax < y) {
			ymax = y;
		}
		if (zmax < z) {
			zmax = z;
		}
	}

	@Override
	protected void appendLightInCollada(StringBuilder sb) {
		// light added in three.js
	}

}

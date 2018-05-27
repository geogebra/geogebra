package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

/**
 * adapted from three.js loading collada example (MIT licence)
 * 
 * 
 *
 */
public class FormatColladaHTML extends FormatCollada {

	@Override
	public String getExtension() {
		return "html";
	}

	@Override
	public void getScriptStart(StringBuilder sb) {
		sb.append("<!DOCTYPE html>\n");
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append("<div id='container'></div>\n");
		sb.append(
				"<script src='https://cdnjs.cloudflare.com/ajax/libs/three.js/92/three.min.js'></script>\n");
		sb.append(
				"<script src='https://cdn.rawgit.com/mrdoob/three.js/r92/examples/js/loaders/ColladaLoader.js'></script>\n");
		sb.append("<script>\n");
		sb.append("var container, clock;\n");
		sb.append("var camera, scene, renderer, ggbExport;\n");
		sb.append("init();\n");
		sb.append("animate();\n");
		sb.append("function init() {\n");
		sb.append("container = document.getElementById( 'container' );\n");
		sb.append(
				"camera = new THREE.PerspectiveCamera( 45, window.innerWidth / window.innerHeight, 0.1, 2000 );\n");
		sb.append("camera.position.set( .5,.5,.5);\n");
		sb.append("camera.lookAt( new THREE.Vector3( 0, 0, 0 ) );\n");
		sb.append("scene = new THREE.Scene();\n");
		sb.append("clock = new THREE.Clock();\n");

		sb.append("var colladaDataURI = 'data:text/plain;charset=utf-8,");
		super.getScriptStart(sb);
	}

	@Override
	public void getScriptEnd(StringBuilder sb) {
		super.getScriptEnd(sb);

		sb.append("';\n");
		sb.append(
				"var loadingManager = new THREE.LoadingManager( function() {\n");
		sb.append("scene.add( ggbExport );\n");
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
		sb.append("renderer.setPixelRatio( window.devicePixelRatio );\n");
		sb.append(
				"renderer.setSize( window.innerWidth, window.innerHeight );\n");
		sb.append("container.appendChild( renderer.domElement );\n");

		sb.append("}\n");
		sb.append("function animate() {\n");
		sb.append("requestAnimationFrame( animate );\n");
		sb.append("render();\n");
		sb.append("}\n");
		sb.append("function render() {\n");
		sb.append("var delta = clock.getDelta();\n");
		sb.append("if ( ggbExport !== undefined ) {\n");
		sb.append("ggbExport.rotation.z += delta * 0.5;\n");
		sb.append("}\n");
		sb.append("renderer.render( scene, camera );\n");
		sb.append("}\n");
		sb.append("</script>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");

	}

}

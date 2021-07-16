using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Coherent.UIGT;


public class JsToUnity : MonoBehaviour {

    public GameObject generateMesh;
    private GenerateMesh generateMeshScript;
    private string Name;

    // Use this for initialization
    void Start () {
		if (generateMesh == null)
        {
            generateMesh = GameObject.Find("GenerateMesh");
        }
        generateMeshScript = generateMesh.GetComponent<GenerateMesh>();
	}
	
    [Coherent.UIGT.CoherentUIGTMethod("GGB3dExport", true)]
    void GGB3dExport(string name)
    {
        generateMeshScript.MeshFromString(name);
        //generateMeshScript.testSpeedForCohorentGt = name; // - for testing speed, separeta CohretntGT and Unity Mesh rendering
    }

    [Coherent.UIGT.CoherentUIGTMethod("OnRemove", true)]
    void FunctionToDelete(string name)
    {
        Debug.Log("In FunctionToDelete");
        generateMeshScript.FunctionToDelete(name);
    }
}

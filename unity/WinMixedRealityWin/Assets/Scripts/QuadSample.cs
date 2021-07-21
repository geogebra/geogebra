using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class QuadSample : MonoBehaviour {

    public int width;
    public int height;
    public Material material;

    // Use this for initialization
    void Start () {
        CreatePlane(width, height, true, material);
    }
	
	// Update is called once per frame
	void Update () {
		
	}

    public GameObject CreatePlane(float width, float height, bool collider, Material mat)
    {
        GameObject go = new GameObject("Quad");
        MeshFilter meshFilter = go.AddComponent(typeof(MeshFilter)) as MeshFilter;
        MeshRenderer meshRenderer = go.AddComponent(typeof(MeshRenderer)) as MeshRenderer;

        Mesh mesh = new Mesh();
        mesh.vertices = new Vector3[4]
        {
        new Vector3(0, 0, 0),
        new Vector3(width, 0, 0),
        new Vector3(0, height, 0),
        new Vector3(width, height, 0)
        };

        mesh.uv = new Vector2[]
        {
            new Vector2(0,0),
            new Vector2(0,1),
            new Vector2(1,1),
            new Vector2(1,0)
        };

        mesh.triangles = new int[]{0, 1, 2, 0, 2, 3};
        meshRenderer.material = mat;

        meshFilter.mesh = mesh;
        if (collider)
        {
            (go.AddComponent(typeof(MeshCollider)) as MeshCollider).sharedMesh = mesh;
        }

        mesh.RecalculateBounds();
        mesh.RecalculateNormals();

        return go;
    }
}

using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class FlipUVs : MonoBehaviour {

    public bool horizontFlip = false;
    public bool verticalFlip = false;
    public bool startFunctionChecker = false;

	// Use this for initialization
	void Start () {

        FlipQuadUV(true, false);
    }

	// Update is called once per frame
	void Update () {

        if (startFunctionChecker)
        {
            FlipQuadUV(horizontFlip, verticalFlip);
            startFunctionChecker = false;
        }
    }

    public void FlipQuadUV(bool horzFlip, bool vertFlip)
    {
        Mesh mesh = GetComponent<MeshFilter>().mesh;
        Vector2[] uvs = mesh.uv;
        if (uvs.Length != 4)
        {
            Debug.Log("Error: not a four vertices mesh");
            return;
        }
        for (int i = 0; i < uvs.Length; i++)
        {
            if (horzFlip)
            {
                if (Mathf.Approximately(uvs[i].x, 1.0f))
                    uvs[i].x = 0.0f;
                else
                    uvs[i].x = 1.0f;
            }
            if (vertFlip)
            {
                if (Mathf.Approximately(uvs[i].y, 1.0f))
                    uvs[i].y = 0.0f;
                else
                    uvs[i].y = 1.0f;
            }
        }
        mesh.uv = uvs;
    }
}

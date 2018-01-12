using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MainImage : MonoBehaviour {


    public Vector2 TextTiling;
    public Vector2 TextOffset;

    // Use this for initialization
    void Start () {

       
        
    }
	
	// Update is called once per frame
	void Update () {


        //GetComponent<Material>().mainTextureScale = TextTiling;
        //GetComponent<Material>().mainTextureOffset = TextOffset;

        GetComponent<Material>().mainTextureScale = new Vector2(TextTiling.x, TextTiling.y);
        GetComponent<Material>().mainTextureOffset = new Vector2(TextOffset.x, TextOffset.y);

        print("ds");

        // GetComponent<Material>().SetTextureScale("SomeInt", new Vector2(TextTiling.x, TextTiling.y));
        //GetComponent<Material>().SetTextureOffset("someint", new Vector2(TextOffset.x, TextOffset.y));

    }
}

using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ReflectionProbePosition : MonoBehaviour {

    public Camera myCamera;
    public  float distanceyCamera = -1.21f;

	// Use this for initialization
	void Start () {


        transform.position = transform.position + new Vector3(0, -1.23f, 0f);

    }
	
	// Update is called once per frame
	void Update () {

        if (myCamera = null)
        {
            myCamera = Camera.main;
        }
        //this.gameObject.transform.position = myCamera.transform.position;
        //this.gameObject.transform.position.y = distanceyCamera;


    }
}

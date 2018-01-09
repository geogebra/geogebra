using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ScreenshotCamera : MonoBehaviour {

    public Camera mainCamera;

	// Use this for initialization
	void Start () {
        if (mainCamera = null)
        {
            mainCamera = Camera.main;
        }
		
	}
	
	// Update is called once per frame
	void Update () {

        transform.position = mainCamera.transform.position;
        transform.rotation = mainCamera.transform.rotation;
    }
}

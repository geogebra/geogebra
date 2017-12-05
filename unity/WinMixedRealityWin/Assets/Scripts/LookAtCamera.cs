using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class LookAtCamera : MonoBehaviour {

    public Camera myCamera;


	// Use this for initialization
	void Start () {
		
	}
	
	// Update is called once per frame


    void Update()
    {
         if (myCamera == null)
         {
             myCamera = Camera.main;
         }
         transform.rotation = Quaternion.LookRotation(transform.position - myCamera.transform.position);
    }
        
    

    
}

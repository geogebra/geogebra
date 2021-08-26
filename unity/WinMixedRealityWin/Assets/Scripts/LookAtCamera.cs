using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class LookAtCamera : MonoBehaviour {

    public Camera camera;
	
    void Update()
    {
         if (camera == null)
         {
             camera = Camera.main;
         }
         transform.rotation = Quaternion.LookRotation(transform.position - camera.transform.position);
    } 
}

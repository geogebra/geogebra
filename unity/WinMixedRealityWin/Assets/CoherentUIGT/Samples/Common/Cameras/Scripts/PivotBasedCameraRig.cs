using UnityEngine;


namespace UnitySampleAssets.Cameras
{
    public abstract class PivotBasedCameraRig : AbstractTargetFollower
    {
        // This script is designed to be placed on the root object of a camera rig,
        // comprising 3 gameobjects, each parented to the next:

        // 	Camera Rig
        // 		Pivot
        // 			Camera

        protected Transform cam; // the transform of the camera
        protected Transform pivot; // the point at which the camera pivots around
        protected Vector3 lastTargetPosition;

        protected virtual void Awake()
        {
            // find the camera in the object hierarchy
            cam = GetComponentInChildren<Camera>().transform;
            pivot = cam.parent;
        }
    }
}
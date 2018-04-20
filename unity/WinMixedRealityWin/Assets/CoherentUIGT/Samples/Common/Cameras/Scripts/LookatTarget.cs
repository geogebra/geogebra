using UnityEngine;

namespace UnitySampleAssets.Cameras
{
    public class LookatTarget : AbstractTargetFollower
    {

        // A simple script to make one object look at another,
        // but with optional constraints which operate relative to
        // this gameobject's initial rotation.

        // Only rotates around local X and Y.

        // Works in local coordinates, so if this object is parented
        // to another moving gameobject, its local constraints will
        // operate correctly
        // (Think: looking out the side window of a car, or a gun turret
        // on a moving spaceship with a limited angular range)

        // to have no constraints on an axis, set the rotationRange greater than 360.

        [SerializeField] private Vector2 rotationRange;
        [SerializeField] private float followSpeed = 1;

        private Vector3 followAngles;
        protected Vector3 followVelocity;
        private Quaternion originalRotation;

        // Use this for initialization
        protected override void Start()
        {
            base.Start();
            originalRotation = transform.localRotation;
        }

        protected override void FollowTarget(float deltaTime)
        {
            // we make initial calculations from the original local rotation
            transform.localRotation = originalRotation;

            // tackle rotation around Y first
            Vector3 localTarget = transform.InverseTransformPoint(target.position);
            float yAngle = Mathf.Atan2(localTarget.x, localTarget.z)*Mathf.Rad2Deg;

            yAngle = Mathf.Clamp(yAngle, -rotationRange.y*0.5f, rotationRange.y*0.5f);
            transform.localRotation = originalRotation*Quaternion.Euler(0, yAngle, 0);

            // then recalculate new local target position for rotation around X
            localTarget = transform.InverseTransformPoint(target.position);
            float xAngle = Mathf.Atan2(localTarget.y, localTarget.z)*Mathf.Rad2Deg;
            xAngle = Mathf.Clamp(xAngle, -rotationRange.x*0.5f, rotationRange.x*0.5f);
            var targetAngles = new Vector3(followAngles.x + Mathf.DeltaAngle(followAngles.x, xAngle),
                                           followAngles.y + Mathf.DeltaAngle(followAngles.y, yAngle));

            // smoothly interpolate the current angles to the target angles
            followAngles = Vector3.SmoothDamp(followAngles, targetAngles, ref followVelocity, followSpeed);


            // and update the gameobject itself
            transform.localRotation = originalRotation*Quaternion.Euler(-followAngles.x, followAngles.y, 0);

        }
    }
}
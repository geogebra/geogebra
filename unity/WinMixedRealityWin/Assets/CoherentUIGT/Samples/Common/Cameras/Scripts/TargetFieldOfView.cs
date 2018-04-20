using UnityEngine;


namespace UnitySampleAssets.Cameras
{

    public class TargetFieldOfView : AbstractTargetFollower
    {

        // This script is primarily designed to be used with the "LookAtTarget" script to enable a
        // CCTV style camera looking at a target to also adjust its field of view (zoom) to fit the
        // target (so that it zooms in as the target becomes further away).
        // When used with a follow cam, it will automatically use the same target.

        [SerializeField] private float fovAdjustTime = 1;// the time taken to adjust the current FOV to the desired target FOV amount.
        [SerializeField] private float zoomAmountMultiplier = 2;// a multiplier for the FOV amount. The default of 2 makes the field of view twice as wide as required to fit the target.
        [SerializeField] private bool includeEffectsInSize = false;// changing this only takes effect on startup, or when new target is assigned.

        private float boundSize;
        private float fovAdjustVelocity;
        private Camera cam;
        private Transform lastTarget;

        // Use this for initialization
        protected override void Start()
        {
            base.Start();

            boundSize = MaxBoundsExtent(target, includeEffectsInSize);

            // get a reference to the actual camera component:
            cam = GetComponentInChildren<Camera>();
        }

        protected override void FollowTarget(float deltaTime)
        {

            // calculate the correct field of view to fit the bounds size at the current distance
            float dist = (target.position - transform.position).magnitude;
            float requiredFOV = Mathf.Atan2(boundSize, dist)*Mathf.Rad2Deg*zoomAmountMultiplier;

            cam.fieldOfView = Mathf.SmoothDamp(cam.fieldOfView, requiredFOV, ref fovAdjustVelocity, fovAdjustTime);
        }

        public override void SetTarget(Transform newTransform)
        {
            base.SetTarget(newTransform);
            boundSize = MaxBoundsExtent(newTransform, includeEffectsInSize);
        }

        public static float MaxBoundsExtent(Transform obj, bool includeEffects)
        {

            // get the maximum bounds extent of object, including all child renderers,
            // but excluding particles and trails, for FOV zooming effect.

            Renderer[] renderers = obj.GetComponentsInChildren<Renderer>();

            Bounds bounds = new Bounds();
            bool initBounds = false;
            foreach (Renderer r in renderers)
            {
                if (!((r is TrailRenderer) || (r is ParticleSystemRenderer)))
                {
                    if (!initBounds)
                    {
                        initBounds = true;
                        bounds = r.bounds;
                    }
                    else
                    {
                        bounds.Encapsulate(r.bounds);
                    }
                }
            }
            float max = Mathf.Max(bounds.extents.x, bounds.extents.y, bounds.extents.z);
            return max;

        }
    }
}
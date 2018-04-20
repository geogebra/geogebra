using UnityEngine;

namespace UnitySampleAssets.Cameras
{
    public abstract class AbstractTargetFollower : MonoBehaviour
    {
        public enum UpdateType // The available methods of updating are:
        {
            FixedUpdate, // Update in FixedUpdate (for tracking rigidbodies).
            LateUpdate, // Update in LateUpdate. (for tracking objects that are moved in Update)
        }

        [SerializeField] protected Transform target; // The target object to follow
        [SerializeField] private bool autoTargetPlayer = true; // Whether the rig should automatically target the player.
        [SerializeField] private UpdateType updateType; // stores the selected update type


        protected virtual void Start()
        {
            // if auto targeting is used, find the object tagged "Player"
            // any class inheriting from this should call base.Start() to perform this action!
            if (autoTargetPlayer)
            {
                FindAndTargetPlayer();
            }

        }

        private void FixedUpdate()
        {

            // we update from here if updatetype is set to Fixed, or in auto mode,
            // if the target has a rigidbody, and isn't kinematic.
            if (autoTargetPlayer && (target == null || !target.gameObject.activeSelf))
            {
                FindAndTargetPlayer();
            }
            if (updateType == UpdateType.FixedUpdate)
            {
                FollowTarget(Time.deltaTime);
            }
        }


        private void LateUpdate()
        {

            // we update from here if updatetype is set to Late, or in auto mode,
            // if the target does not have a rigidbody, or - does have a rigidbody but is set to kinematic.
            if (autoTargetPlayer && (target == null || !target.gameObject.activeSelf))
            {
                FindAndTargetPlayer();
            }
            if (updateType == UpdateType.LateUpdate)
            {
                FollowTarget(Time.deltaTime);
            }
        }


        protected abstract void FollowTarget(float deltaTime);

        public void FindAndTargetPlayer()
        {
	        // auto target an object tagged player, if no target has been assigned
	        var targetObj = GameObject.FindGameObjectWithTag("Player");
	        if (targetObj)
	        {
	            SetTarget(targetObj.transform);
	        }
        }


        public virtual void SetTarget(Transform newTransform)
        {
            target = newTransform;
        }

        public Transform Target
        {
            get { return this.target; }
        }

    }
}

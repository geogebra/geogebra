// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

using UnityEngine;

namespace HoloToolkit.Unity.Controllers
{
    public interface IPointerTarget
    {
        void OnPointerTarget(PhysicsPointer source);
    }

    public enum PointerSurfaceResultEnum
    {
        None,
        Valid,
        Invalid,
        HotSpot
    }

    public class PhysicsPointer : MonoBehaviour
    {
        public float TotalLength { get; protected set; }
        public Vector3 StartPoint { get; protected set; }
        public Vector3 TargetPoint { get; protected set; }
        public Vector3 StartPointNormal { get; protected set; }
        public Vector3 TargetPointNormal { get; protected set; }
        public Vector3 PointerForward { get; protected set; }
        public PointerSurfaceResultEnum TargetResult { get; protected set; }
        public virtual float TargetPointOrientation { get; set; }

        public Transform RaycastOrigin
        {
            get
            {
                if (raycastOrigin == null)
                    return transform;

                return raycastOrigin;
            }
            set
            {
                raycastOrigin = value;
            }
        }

        public Gradient GetColor(PointerSurfaceResultEnum targetResult)
        {
            switch (targetResult)
            {
                case PointerSurfaceResultEnum.None:
                default:
                    return lineColorNoTarget;

                case PointerSurfaceResultEnum.Valid:
                    return lineColorValid;

                case PointerSurfaceResultEnum.Invalid:
                    return lineColorInvalid;

                case PointerSurfaceResultEnum.HotSpot:
                    return lineColorHotSpot;
            }
        }

        public bool Active
        {
            get { return active; }
            set { active = value; }
        }

        [Header("Colors")]
        [SerializeField]
        protected Gradient lineColorValid;
        [SerializeField]
        protected Gradient lineColorInvalid;
        [SerializeField]
        protected Gradient lineColorHotSpot;
        [SerializeField]
        protected Gradient lineColorNoTarget;
        [Header("Physics")]
        [SerializeField]
        protected LayerMask validLayers = 1; // Default
        [SerializeField]
        protected LayerMask invalidLayers = 1 << 2; // Ignore raycast
        [SerializeField]
        protected bool detectTriggers = false;
        [Header("Input")]
        [SerializeField]
        protected bool active;
        [SerializeField]
        private Transform raycastOrigin;

        protected RaycastHit targetHit;

        public static bool CheckForHotSpot(Collider checkCollider, out NavigationHotSpot hotSpot)
        {
            hotSpot = null;

            if (checkCollider == null)
            {
                return false;
            }

            if (checkCollider.attachedRigidbody != null)
            {
                hotSpot = checkCollider.attachedRigidbody.GetComponent<NavigationHotSpot>();
            }
            else
            {
                hotSpot = checkCollider.GetComponent<NavigationHotSpot>();
            }

            return hotSpot != null;
        }

        public static bool CheckForPointerTarget(Collider checkCollider, PhysicsPointer pointer)
        {
            IPointerTarget target = null;

            if (checkCollider == null)
            {
                return false;
            }

            if (checkCollider.attachedRigidbody != null)
            {
                target = checkCollider.attachedRigidbody.GetComponent(typeof(IPointerTarget)) as IPointerTarget;
            }
            else
            {
                target = checkCollider.GetComponent(typeof(IPointerTarget)) as IPointerTarget;
            }

            if (target != null)
            {
                target.OnPointerTarget(pointer);
            }

            return target != null;
        }
    }
}

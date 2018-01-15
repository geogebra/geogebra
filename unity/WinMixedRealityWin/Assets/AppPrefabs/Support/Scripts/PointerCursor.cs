// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

using UnityEngine;

namespace HoloToolkit.Unity.Controllers
{
    [ExecuteInEditMode]
    public class PointerCursor : MonoBehaviour
    {
        [SerializeField]
        protected PhysicsPointer pointer;
        public PhysicsPointer Pointer
        {
            get { return pointer; }
        }

        [SerializeField]
        protected float sizeOnScreen = 0.015f;
        [SerializeField]
        protected float scaleAdjustTime = 0.5f;

        [SerializeField]
        private Renderer[] renderers;
        [SerializeField]
        private Transform pointerTransform;

        protected void OnEnable()
        {
            if (renderers == null || renderers.Length == 0)
            {
                renderers = pointerTransform.GetComponentsInChildren<Renderer>();
            }
        }

        protected void Update()
        {
            if (pointer == null)
            {
                return;
            }

            pointerTransform.position = pointer.TargetPoint;
            pointerTransform.forward = pointer.TargetPointNormal;

            // Update scale to match screen size
            float distanceFromCamera = Vector3.Distance(pointerTransform.position, Camera.main.transform.position);
            switch (pointer.TargetResult)
            {
                case PointerSurfaceResultEnum.HotSpot:
                    pointerTransform.localScale = Vector3.Lerp(pointerTransform.localScale, Vector3.one * (sizeOnScreen * 1.5f) / (1f / distanceFromCamera), scaleAdjustTime);
                    break;

                case PointerSurfaceResultEnum.Invalid:
                case PointerSurfaceResultEnum.Valid:
                    pointerTransform.localScale = Vector3.Lerp(pointerTransform.localScale, Vector3.one * sizeOnScreen / (1f / distanceFromCamera), scaleAdjustTime);
                    break;

                case PointerSurfaceResultEnum.None:
                    pointerTransform.localScale = Vector3.one * sizeOnScreen / 100;
                    break;
            }

            for (int i = 0; i < renderers.Length; i++)
            {
                // TODO figure out gradient - it may have transparency
                if (Application.isPlaying)
                {
                    renderers[i].material.color = pointer.GetColor(pointer.TargetResult).Evaluate(0.5f);
                }
                renderers[i].enabled = pointer.TargetResult != PointerSurfaceResultEnum.None;
            }
        }
    }
}
// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

using HoloToolkit.Unity.InputModule;
using UnityEngine;

#if UNITY_WSA && UNITY_2017_2_OR_NEWER
using UnityEngine.XR.WSA.Input;
#endif

namespace HoloToolkit.Unity.Controllers
{
    /// <summary>
    /// Routes controller input to a physics pointer
    /// </summary>
    public class PointerInput : AttachToController
    {
        [Header("PointerInput Elements")]
        [SerializeField]
        private PhysicsPointer pointer = null;

#if UNITY_WSA && UNITY_2017_2_OR_NEWER
        [SerializeField]
        private InteractionSourcePressType activePressType = InteractionSourcePressType.Select;
#endif

        private void Awake()
        {
            if (pointer == null)
            {
                pointer = GetComponent<PhysicsPointer>();
            }

            pointer.Active = false;
        }

        protected override void OnAttachToController()
        {
#if UNITY_WSA && UNITY_2017_2_OR_NEWER
            // Subscribe to interaction events
            InteractionManager.InteractionSourceUpdated += InteractionSourceUpdated;
            InteractionManager.InteractionSourcePressed += InteractionSourcePressed;
            InteractionManager.InteractionSourceReleased += InteractionSourceReleased;
#endif
        }

        protected override void OnDetachFromController()
        {
#if UNITY_WSA && UNITY_2017_2_OR_NEWER
            // Unsubscribe from interaction events
            InteractionManager.InteractionSourceUpdated -= InteractionSourceUpdated;
            InteractionManager.InteractionSourcePressed -= InteractionSourcePressed;
            InteractionManager.InteractionSourceReleased -= InteractionSourceReleased;
#endif
        }

#if UNITY_WSA && UNITY_2017_2_OR_NEWER
        /// <summary>
        /// Presses active
        /// </summary>
        /// <param name="obj"></param>
        private void InteractionSourcePressed(InteractionSourcePressedEventArgs obj)
        {
            if (obj.state.source.handedness == handedness && obj.pressType == activePressType)
            {
                pointer.Active = true;
            }
        }

        /// <summary>
        /// Updates target point orientation via thumbstick
        /// </summary>
        /// <param name="obj"></param>
        private void InteractionSourceUpdated(InteractionSourceUpdatedEventArgs obj)
        {
            if (obj.state.source.handedness == handedness && obj.state.thumbstickPressed)
            {
                float angle = 0f;
                Vector2 thumbstickPosition = obj.state.thumbstickPosition;
                if (thumbstickPosition.y != 0 && thumbstickPosition.x != 0)
                {
                    angle = Mathf.Atan2(thumbstickPosition.y, thumbstickPosition.x) * Mathf.Rad2Deg;
                }
                pointer.TargetPointOrientation = angle;
            }
        }

        /// <summary>
        /// Releases active
        /// </summary>
        /// <param name="obj"></param>
        private void InteractionSourceReleased(InteractionSourceReleasedEventArgs obj)
        {
            if (obj.state.source.handedness == handedness && obj.pressType == activePressType)
            {
                pointer.Active = false;
            }
        }
#endif
    }
}
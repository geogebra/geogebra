// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

using HoloToolkit.Unity.Controllers;
using MixedRealityToolkit.InputModule.Utilities;
using MixedRealityToolkit.InputModule;
using UnityEngine;

#if UNITY_WSA && UNITY_2017_2_OR_NEWER
using UnityEngine.XR.WSA.Input;
#endif

namespace HoloToolkit.Unity.ControllerExamples
{
    public class CameraLogo : AttachToController, IPointerTarget
    {

        [Header("ColorPickerWheel Elements")]
        [SerializeField]
        private bool visible = false;
        [SerializeField]
        private Renderer selectorRenderer;
        [SerializeField]
        private float inputScale = 1.1f;

        private Vector2 selectorPosition;

        private void Update()
        {
            if (controller == null)
            {
                return;
            }
        }

        protected override void OnAttachToController()
        {
#if UNITY_WSA && UNITY_2017_2_OR_NEWER
            // Subscribe to input now that we're parented under the controller
            InteractionManager.InteractionSourceUpdated += InteractionSourceUpdated;
#endif
        }

        protected override void OnDetachFromController()
        {
            //Visible = false;

#if UNITY_WSA && UNITY_2017_2_OR_NEWER
            // Unsubscribe from input now that we've detached from the controller
            InteractionManager.InteractionSourceUpdated -= InteractionSourceUpdated;
#endif
        }

        public void OnPointerTarget(PhysicsPointer source)
        {
         
        }

#if UNITY_WSA && UNITY_2017_2_OR_NEWER
        private void InteractionSourceUpdated(InteractionSourceUpdatedEventArgs obj)
        {
           selectorPosition = obj.state.touchpadPosition;
        }
#endif
    }
}
 
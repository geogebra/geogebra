// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

using HoloToolkit.Unity.Controllers;
using HoloToolkit.Unity.InputModule;
using UnityEngine;

#if UNITY_WSA && UNITY_2017_2_OR_NEWER
using UnityEngine.XR.WSA.Input;
#endif

namespace HoloToolkit.Unity.ControllerExamples
{
    public class CameraLogo : AttachToController, IPointerTarget
    {



        public bool Visible
        {
            get { return visible; }
            set
            {
                visible = value;
                if (value)
                {
                    lastTimeVisible = Time.unscaledTime;
                }
            }
        }

        public Color SelectedColor
        {
            get { return selectedColor; }
        }

        [Header("ColorPickerWheel Elements")]
        [SerializeField]
        private bool visible = false;
        //[SerializeField]
        //private Transform selectorTransform;
        [SerializeField]
        private Renderer selectorRenderer;
        [SerializeField]
        private float inputScale = 1.1f;
        [SerializeField]
        private Color selectedColor = Color.white;
        [SerializeField]
        private Texture2D colorWheelTexture;
        [SerializeField]
        private GameObject colorWheelObject;

        [SerializeField]
        //private float timeout = 2f;

        private Vector2 selectorPosition;
        private float lastTimeVisible;
        private bool visibleLastFrame = false;

        private void Update()
        {
            if (controller == null)
            {
                return;
            }

            /*if (Time.unscaledTime > lastTimeVisible + timeout)
            {
                visible = false;
            }*/

            
            visibleLastFrame = visible;

            if (!visible)
            {
                return;
            }

            // Transform the touchpad's input x, y position information to ColorPickerWheel's local position x, z
            /*Vector3 localPosition = new Vector3(selectorPosition.x * inputScale, 0.15f, selectorPosition.y * inputScale);
            if (localPosition.magnitude > 1)
            {
                localPosition = localPosition.normalized;
            }*/

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
            Visible = false;

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
 
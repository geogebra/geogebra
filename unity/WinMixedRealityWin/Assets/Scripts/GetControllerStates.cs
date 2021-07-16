// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

using System;
using UnityEngine;
using MixedRealityToolkit.Utilities;

#if UNITY_WSA && UNITY_2017_2_OR_NEWER
using System.Collections.Generic;
using UnityEngine.XR.WSA.Input;
using Coherent.UIGT;
#endif

namespace HoloToolkit.Unity
{
    public class GetControllerStates : MonoBehaviour
    {
#if UNITY_WSA && UNITY_2017_2_OR_NEWER
        private class ControllerState
        {
            public InteractionSourceHandedness Handedness;
            public Vector3 PointerPosition;
            public Quaternion PointerRotation;
            public Vector3 GripPosition;
            public Quaternion GripRotation;
            public bool Grasped;
            public bool MenuPressed;
            public bool SelectPressed;
            public float SelectPressedAmount;
            public bool ThumbstickPressed;
            public Vector2 ThumbstickPosition;
            public bool TouchpadPressed;
            public bool TouchpadTouched;
            public Vector2 TouchpadPosition;
        }

        private Dictionary<uint, ControllerState> controllers;
#endif

        // Text display label game objects
        public string LeftInfoTextPointerPosition;
        public string LeftInfoTextPointerRotation;
        public string LeftInfoTextGripPosition;
        public string LeftInfoTextGripRotation;
        public string LeftInfoTextGripGrasped;
        public string LeftInfoTextMenuPressed;
        public string LeftInfoTextTriggerPressed;
        public string LeftInfoTextTriggerPressedAmount;
        public string LeftInfoTextThumbstickPressed;
        public string LeftInfoTextThumbstickPosition;
        public string LeftInfoTextTouchpadPressed;
        public string LeftInfoTextTouchpadTouched;
        public string LeftInfoTextTouchpadPosition;
        public string RightInfoTextPointerPosition;
        public string RightInfoTextPointerRotation;
        public string RightInfoTextGripPosition;
        public string RightInfoTextGripRotation;
        public string RightInfoTextGripGrasped;
        public string RightInfoTextMenuPressed;
        public string RightInfoTextTriggerPressed;
        public string RightInfoTextTriggerPressedAmount;
        public string RightInfoTextThumbstickPosition;
        public string RightInfoTextTouchpadPressed;
        public string RightInfoTextTouchpadTouched;
        public string RightInfoTextTouchpadPosition;

        private void Awake()
        {
#if UNITY_WSA && UNITY_2017_2_OR_NEWER
            controllers = new Dictionary<uint, ControllerState>();

            InteractionManager.InteractionSourceDetected += InteractionManager_InteractionSourceDetected;

            InteractionManager.InteractionSourceLost += InteractionManager_InteractionSourceLost;
            InteractionManager.InteractionSourceUpdated += InteractionManager_InteractionSourceUpdated;
#endif
        }

        // public Varables to get infoState
        [Space(10)]
        [Header("Sources Motion Controller")]
        [Space(10)]

        public Vector3 PointerPosition;
        public Quaternion PointerRotation;
        public Vector3 GripPosition;
        public Quaternion GripRotation;
        public bool Grasped;
        public bool MenuPressed;
        public bool SelectPressed;
        public float SelectPressedAmount;
        public bool ThumbstickPressed;
        public Vector2 ThumbstickPosition;
        public bool TouchpadPressed;
        public bool TouchpadTouched;
        public Vector2 TouchpadPosition;

        [Space(10)]
        [Header("Sources Motion Left Controller")]
        [Space(10)]

        public Vector3 PointerPositionLeft;
        public Quaternion PointerRotationLeft;
        public Vector3 GripPositionLeft;
        public Quaternion GripRotationLeft;
        public bool GraspedLeft;
        public bool MenuPressedLeft;
        public bool SelectPressedLeft;
        public float SelectPressedAmountLeft;
        public bool ThumbstickPressedLeft;
        public Vector2 ThumbstickPositionLeft;
        public bool TouchpadPressedLeft;
        public bool TouchpadTouchedLeft;
        public Vector2 TouchpadPositionLeft;

        [Space(10)]
        [Header("Sources Motion Right Controller")]
        [Space(10)]

        public Vector3 PointerPositionRight;
        public Quaternion PointerRotationRight;
        public Vector3 GripPositionRight;
        public Quaternion GripRotationRight;
        public bool GraspedRight;
        public bool MenuPressedRight;
        public bool SelectPressedRight;
        public float SelectPressedAmountRight;
        public bool ThumbstickPressedRight;
        public Vector2 ThumbstickPositionRight;
        public bool TouchpadPressedRight;
        public bool TouchpadTouchedRight;
        public Vector2 TouchpadPositionRight;

        private void Start()
        {
            if (DebugPanel.Instance != null)
            {
                DebugPanel.Instance.RegisterExternalLogCallback(GetControllerInfo);
            }
        }

#if UNITY_WSA && UNITY_2017_2_OR_NEWER
        private void InteractionManager_InteractionSourceDetected(InteractionSourceDetectedEventArgs obj)
        {
            //Debug.LogFormat("{0} {1} Detected", obj.state.source.handedness, obj.state.source.kind);

            if (obj.state.source.kind == InteractionSourceKind.Controller && !controllers.ContainsKey(obj.state.source.id))
            {
                controllers.Add(obj.state.source.id, new ControllerState { Handedness = obj.state.source.handedness });
            }
        }

        private void InteractionManager_InteractionSourceLost(InteractionSourceLostEventArgs obj)
        {
            Debug.LogFormat("{0} {1} Lost", obj.state.source.handedness, obj.state.source.kind);

            controllers.Remove(obj.state.source.id);
        }

        private void InteractionManager_InteractionSourceUpdated(InteractionSourceUpdatedEventArgs obj)
        {
            ControllerState controllerState;
            if (controllers.TryGetValue(obj.state.source.id, out controllerState))
            {
                obj.state.sourcePose.TryGetPosition(out controllerState.PointerPosition, InteractionSourceNode.Pointer);
                obj.state.sourcePose.TryGetRotation(out controllerState.PointerRotation, InteractionSourceNode.Pointer);
                obj.state.sourcePose.TryGetPosition(out controllerState.GripPosition, InteractionSourceNode.Grip);
                obj.state.sourcePose.TryGetRotation(out controllerState.GripRotation, InteractionSourceNode.Grip);

                controllerState.Grasped = obj.state.grasped;
                controllerState.MenuPressed = obj.state.menuPressed;
                controllerState.SelectPressed = obj.state.selectPressed;
                controllerState.SelectPressedAmount = obj.state.selectPressedAmount;
                controllerState.ThumbstickPressed = obj.state.thumbstickPressed;
                controllerState.ThumbstickPosition = obj.state.thumbstickPosition;
                controllerState.TouchpadPressed = obj.state.touchpadPressed;
                controllerState.TouchpadTouched = obj.state.touchpadTouched;
                controllerState.TouchpadPosition = obj.state.touchpadPosition;
            }
        }
#endif

        private string GetControllerInfo()
        {
            string toReturn = string.Empty;
#if UNITY_WSA && UNITY_2017_2_OR_NEWER
            foreach (ControllerState controllerState in controllers.Values)
            {
                // Debug message
                toReturn += string.Format("Hand: {0}\nPointer: Position: {1} Rotation: {2}\n" +
                                          "Grip: Position: {3} Rotation: {4}\nGrasped: {5} " +
                                          "MenuPressed: {6}\nSelect: Pressed: {7} PressedAmount: {8}\n" +
                                          "Thumbstick: Pressed: {9} Position: {10}\nTouchpad: Pressed: {11} " +
                                          "Touched: {12} Position: {13}\n\n",
                                          controllerState.Handedness, controllerState.PointerPosition, controllerState.PointerRotation.eulerAngles,
                                          controllerState.GripPosition, controllerState.GripRotation.eulerAngles, controllerState.Grasped,
                                          controllerState.MenuPressed, controllerState.SelectPressed, controllerState.SelectPressedAmount,
                                          controllerState.ThumbstickPressed, controllerState.ThumbstickPosition, controllerState.TouchpadPressed,
                                          controllerState.TouchpadTouched, controllerState.TouchpadPosition);

                // Text label display
                if (controllerState.Handedness.Equals(InteractionSourceHandedness.Left))
                {             
                    PointerPositionLeft = controllerState.PointerPosition;
                    PointerRotationLeft = controllerState.PointerRotation;
                    GripPositionLeft = controllerState.GripPosition;
                    GripRotationLeft = controllerState.GripRotation;
                    GraspedLeft = controllerState.Grasped;
                    MenuPressedLeft = controllerState.MenuPressed;
                    SelectPressedLeft = controllerState.SelectPressed;
                    SelectPressedAmountLeft = controllerState.SelectPressedAmount;
                    ThumbstickPressedLeft = controllerState.ThumbstickPressed;
                    ThumbstickPositionLeft = controllerState.ThumbstickPosition;
                    TouchpadPressedLeft = controllerState.TouchpadPressed;
                    TouchpadTouchedLeft = controllerState.TouchpadTouched;
                    TouchpadPositionLeft = controllerState.TouchpadPosition;
                }
                else if (controllerState.Handedness.Equals(InteractionSourceHandedness.Right))
                {

                    PointerPositionRight = controllerState.PointerPosition;
                    PointerRotationRight = controllerState.PointerRotation;
                    GripPositionRight = controllerState.GripPosition;
                    GripRotationRight = controllerState.GripRotation;
                    GraspedRight = controllerState.Grasped;
                    MenuPressedRight = controllerState.MenuPressed;
                    SelectPressedRight = controllerState.SelectPressed;
                    SelectPressedAmountRight = controllerState.SelectPressedAmount;
                    ThumbstickPressedRight = controllerState.ThumbstickPressed;
                    ThumbstickPositionRight = controllerState.ThumbstickPosition;
                    TouchpadPressedRight = controllerState.TouchpadPressed;
                    TouchpadTouchedRight = controllerState.TouchpadTouched;
                    TouchpadPositionRight = controllerState.TouchpadPosition;
                }

                PointerPosition = controllerState.PointerPosition;
                PointerRotation = controllerState.PointerRotation;
                GripPosition = controllerState.GripPosition;
                GripRotation = controllerState.GripRotation;
                Grasped = controllerState.Grasped;
                MenuPressed = controllerState.MenuPressed;
                SelectPressed = controllerState.SelectPressed;
                SelectPressedAmount = controllerState.SelectPressedAmount;
                ThumbstickPressed = controllerState.ThumbstickPressed;
                ThumbstickPosition = controllerState.ThumbstickPosition;
                TouchpadPressed = controllerState.TouchpadPressed;
                TouchpadTouched = controllerState.TouchpadTouched;
                TouchpadPosition = controllerState.TouchpadPosition;
            }
#endif
            return toReturn.Substring(0, Math.Max(0, toReturn.Length - 2));
        }
    }
}
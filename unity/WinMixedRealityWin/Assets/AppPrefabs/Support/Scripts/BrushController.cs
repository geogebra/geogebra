// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

using System.Collections;
using UnityEngine;
using UnityEngine.XR.WSA.Input;

namespace HoloToolkit.Unity.ControllerExamples
{
    public class BrushController : MonoBehaviour
    {
        public enum DisplayModeEnum
        {
            InMenu,
            InHand,
            Hidden
        }

        public bool Draw
        {
            get { return draw; }
            set
            {
                if (draw != value)
                {
                    draw = value;
                    if (draw)
                    {
                        StartCoroutine(DrawOverTime());
                    }
                }
            }
        }

        public Vector3 TipPosition
        {
            get { return tip.position; }
        }

        [Header("Drawing Settings")]
        [SerializeField]
        private float minColorDelta = 0.01f;
        [SerializeField]
        private float minPositionDelta = 0.01f;
        [SerializeField]
        private float maxTimeDelta = 0.25f;
        [SerializeField]
        private Transform tip;
        [SerializeField]
        private GameObject strokePrefab;
        [SerializeField]
        private Transform brushObjectTransform;
        [SerializeField]
        private Renderer brushRenderer;

        private ColorPickerWheel colorPicker;
        private Color currentStrokeColor = Color.white;
        private bool draw = false;

        // Default storke width is defined in BrushThinStroke.prefab
        private float width = 0f;
        private float lastPointAddedTime = 0f;

        private void OnEnable()
        {
            // Subscribe to press and release events for drawing
            InteractionManager.InteractionSourcePressed += InteractionSourcePressed;
            InteractionManager.InteractionSourceReleased += InteractionSourceReleased;
        }

        private void OnDisable()
        {
            InteractionManager.InteractionSourcePressed -= InteractionSourcePressed;
            InteractionManager.InteractionSourceReleased -= InteractionSourceReleased;
        }

        private void Update()
        {
            if (!FindColorPickerWheel())
            {
                return;
            }

            brushRenderer.material.color = colorPicker.SelectedColor;
        }

        private void InteractionSourcePressed(InteractionSourcePressedEventArgs obj)
        {
            if (obj.state.source.handedness == InteractionSourceHandedness.Right && obj.pressType == InteractionSourcePressType.Select)
            {
                Draw = true;
                width = 0f;
            }
        }

        private void InteractionSourceReleased(InteractionSourceReleasedEventArgs obj)
        {
            if (obj.state.source.handedness == InteractionSourceHandedness.Right && obj.pressType == InteractionSourcePressType.Select)
            {
                Draw = false;
                width = 0f;
            }
        }

        private bool FindColorPickerWheel()
        {
            if (colorPicker == null)
            {
                colorPicker = FindObjectOfType<ColorPickerWheel>();
            }

            return colorPicker != null;
        }

        private IEnumerator DrawOverTime()
        {
            // Get the position of the tip
            Vector3 lastPointPosition = tip.position;
            // Then wait one frame and get the position again
            yield return null;

            // If we're not still drawing after one frame
            if (!draw)
            {
                // Abort drawing
                yield break;
            }

            Vector3 startPosition = tip.position;
            // Create a new brush stroke by instantiating stokePrefab
            GameObject newStroke = Instantiate(strokePrefab);
            LineRenderer line = newStroke.GetComponent<LineRenderer>();
            newStroke.transform.position = startPosition;
            line.SetPosition(0, tip.position);
            float initialWidth = line.widthMultiplier;

            // Generate points in an instantiated Unity LineRenderer
            while (draw)
            {
                // Move the last point to the draw point position
                line.SetPosition(line.positionCount - 1, tip.position);
                line.material.color = colorPicker.SelectedColor;
                brushRenderer.material.color = colorPicker.SelectedColor;
                lastPointAddedTime = Time.unscaledTime;
                line.widthMultiplier = Mathf.Lerp(initialWidth, initialWidth * 2, width);

                if (Vector3.Distance(lastPointPosition, tip.position) > minPositionDelta || Time.unscaledTime > lastPointAddedTime + maxTimeDelta)
                {
                    // Spawn a new point
                    lastPointAddedTime = Time.unscaledTime;
                    lastPointPosition = tip.position;
                    line.positionCount += 1;
                    line.SetPosition(line.positionCount - 1, lastPointPosition);
                }
                yield return null;
            }
        }
    }
}
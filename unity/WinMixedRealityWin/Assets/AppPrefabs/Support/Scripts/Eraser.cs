// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace HoloToolkit.Unity.ControllerExamples
{
    public class Eraser : Brush
    {
        [SerializeField]
        private float eraseRange = 0.1f;
        [SerializeField]
        private float eraseTime = 0.15f;

        private bool erasingStrokes = false;
        private Queue<LineRenderer> erasedStrokes = new Queue<LineRenderer>();

        // Instead of drawing, the eraser will remove existing strokes
        protected override IEnumerator DrawOverTime()
        {
            // Get all the brush strokes that currently exist
            List<GameObject> brushStrokes = new List<GameObject>(GameObject.FindGameObjectsWithTag("BrushStroke"));

            while (draw)
            {
                // Move backwards through the brush strokes, removing any we intersect with
                for (int i = brushStrokes.Count - 1; i >= 0; i--)
                {
                    // Do a crude check for proximity with the brush stroke's render bounds
                    LineRenderer lineRenderer = brushStrokes[i].GetComponent<LineRenderer>();
                    if (erasedStrokes.Contains(lineRenderer))
                        continue;

                    if (lineRenderer.bounds.Contains(TipPosition))
                    {
                        // If we're in bounds, check whether any point of the stroke is within range
                        Vector3[] positions = new Vector3[lineRenderer.positionCount];
                        lineRenderer.GetPositions(positions);
                        for (int j = 0; j < positions.Length; j++)
                        {
                            if (Vector3.Distance(positions[j], TipPosition) < eraseRange)
                            {
                                // Un-tag and erase the brush stroke
                                brushStrokes[i].tag = "Untagged";
                                brushStrokes.RemoveAt(i);
                                erasedStrokes.Enqueue(lineRenderer);
                                if (!erasingStrokes)
                                {
                                    // If we've just added a new stroke, start erasing them
                                    erasingStrokes = true;
                                    StartCoroutine(EraseStrokesOverTime());
                                }
                                break;
                            }
                        }
                    }
                }
                yield return null;
            }
            yield break;
        }

        private IEnumerator EraseStrokesOverTime()
        {
            while (erasedStrokes.Count > 0)
            {
                LineRenderer lineRenderer = erasedStrokes.Dequeue();
                float startTime = Time.unscaledTime;
                float startWidth = lineRenderer.widthMultiplier;
                // Get the positions from the line renderer
                Vector3[] startPositions = new Vector3[lineRenderer.positionCount];
                lineRenderer.GetPositions(startPositions);
                // Create a new array for their positions as they're being erased
                Vector3[] endPositions = new Vector3[lineRenderer.positionCount];
                while (Time.unscaledTime < startTime + eraseTime)
                {
                    float normalizedTime = (Time.unscaledTime - startTime) / eraseTime;
                    for (int i = 0; i < startPositions.Length; i++)
                    {
                        // Add a bit of random noise based on how far away the point is
                        Vector3 randomNoise = Random.insideUnitSphere * Vector3.Distance(endPositions[i], TipPosition) * 0.1f;
                        endPositions[i] = Vector3.Lerp(startPositions[i], TipPosition, normalizedTime) + randomNoise;
                        lineRenderer.SetPositions(endPositions);
                        // Make the line skinnier as we go
                        lineRenderer.widthMultiplier = Mathf.Lerp(startWidth, startWidth * 0.1f, normalizedTime);
                    }
                    yield return null;
                }
                GameObject.Destroy(lineRenderer.gameObject);
                yield return null;
            }

            erasingStrokes = false;
            yield break;
        }
    }
}
// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

using UnityEditor;
using UnityEngine;

namespace HoloToolkit.Unity.Design
{
    public class LineBaseInspector : Editor
    {
        public override void OnInspectorGUI()
        {
            base.OnInspectorGUI();
        }

        public virtual void OnSceneGUI()
        {
            if (Event.current.type == EventType.MouseDown)
            {
                mouseDown = true;
            }
            else if (Event.current.type == EventType.MouseUp)
            {
                mouseDown = false;
                recordingUndo = false;
            }

            LineBase line = (LineBase)target;

            if (line.ManualUpVectorBlend > 0)
            {
                DrawManualUpVectorHandles(line);
            }
        }

        protected void DrawManualUpVectorHandles(LineBase line)
        {
            if (line.ManualUpVectors == null || line.ManualUpVectors.Length < 2)
                line.ManualUpVectors = new Vector3[2];

            for (int i = 0; i < line.ManualUpVectors.Length; i++)
            {
                float normalizedLength = (1f / (line.ManualUpVectors.Length - 1)) * i;
                Vector3 currentPoint = line.GetPoint(normalizedLength);
                Vector3 currentUpVector = line.ManualUpVectors[i];
                float maxHandleLength = (HandleUtility.GetHandleSize(currentPoint) * rotationHandleLength);
                Vector3 upVectorPoint = currentPoint + (currentUpVector * (maxHandleLength * currentUpVector.magnitude));

                Handles.color = Color.Lerp(Color.black, Color.cyan, currentUpVector.magnitude);

                Handles.DrawDottedLine(currentPoint, upVectorPoint, rotationHandleSize);
                Handles.Label(upVectorPoint, currentUpVector.magnitude.ToString("0.00"));
                Vector3 newUpVectorPoint = Handles.FreeMoveHandle(
                    upVectorPoint, 
                    Quaternion.identity, 
                    HandleUtility.GetHandleSize(currentPoint) * rotationHandleSize, 
                    Vector3.zero, 
                    Handles.RectangleHandleCap); 
                if (newUpVectorPoint != upVectorPoint)
                {
                    if (!recordingUndo)
                    {
                        recordingUndo = true;
                        Undo.RegisterCompleteObjectUndo(line, "Edit Manual Up Vector");
                    }
                    Vector3 newUpVector = (newUpVectorPoint - currentPoint) / maxHandleLength;
                    if (newUpVector.magnitude > 1)
                        newUpVector.Normalize();

                    line.ManualUpVectors[i] = newUpVector;
                }
            }
        }

        private bool recordingUndo = false;
        private bool mouseDown = false;
        private float rotationHandleLength = 2f;
        private float rotationHandleSize = 0.1f;
    }
}
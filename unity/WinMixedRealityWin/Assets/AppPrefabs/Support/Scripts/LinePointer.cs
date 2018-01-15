// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

using HoloToolkit.Unity.Design;
using UnityEngine;

namespace HoloToolkit.Unity.Controllers
{
    [RequireComponent(typeof(Line))]
    [ExecuteInEditMode]
    public class LinePointer : PhysicsPointer
    {
        [SerializeField]
        private Line line;
        [SerializeField]
        protected Design.LineRenderer[] renderers;
        [SerializeField]
        private float maxDistance = 100f;

        protected void OnEnable()
        {
            if (renderers == null || renderers.Length == 0)
            {
                renderers = gameObject.GetComponentsInChildren<Design.LineRenderer>();
            }

            if (line == null)
            {
                line = gameObject.GetComponent<Line>();
            }
        }

        protected void OnDisable()
        {
            if (line != null)
            {
                line.enabled = false;
            }
        }

        protected void Update()
        {
            TargetResult = PointerSurfaceResultEnum.None;

            PointerForward = RaycastOrigin.forward;
            // Set the orientation based on our forward
            TargetPointOrientation = Quaternion.LookRotation(PointerForward).eulerAngles.y;
            // TODO use the controller to set additional orientation

            if (active)
            {
                StartPoint = RaycastOrigin.position;
                StartPointNormal = RaycastOrigin.forward;

                QueryTriggerInteraction queryTriggers = (detectTriggers ? QueryTriggerInteraction.Collide : QueryTriggerInteraction.Ignore);
                // Try to detect valid layers
                if (Physics.Raycast(StartPoint, StartPointNormal, out targetHit, maxDistance, validLayers.value, queryTriggers))
                {
                    // Make this a valid hit by default
                    TargetPoint = targetHit.point;
                    TargetPointNormal = targetHit.normal;
                    TargetResult = PointerSurfaceResultEnum.Valid;
                    // Then see if we've hit a hotspot that overrides this target point
                    NavigationHotSpot hotSpot = null;
                    if (PhysicsPointer.CheckForHotSpot(targetHit.collider, out hotSpot))
                    {
                        TargetPoint = hotSpot.transform.position;
                        TargetPointNormal = hotSpot.transform.up;
                        TargetResult = PointerSurfaceResultEnum.HotSpot;
                    }
                    TotalLength = Vector3.Distance(StartPoint, TargetPoint);
                    // Send a message to the thing we hit, if applicable
                    PhysicsPointer.CheckForPointerTarget(targetHit.collider, this);
                }
                else if (Physics.Raycast(StartPoint, StartPointNormal, out targetHit, maxDistance, invalidLayers.value, queryTriggers))
                {
                    // Invalid hit
                    TargetPoint = targetHit.point;
                    TargetPointNormal = targetHit.normal;
                    TargetResult = PointerSurfaceResultEnum.Invalid;
                    TotalLength = Vector3.Distance(StartPoint, TargetPoint);
                    // Send a message to the thing we hit, if applicable
                    PhysicsPointer.CheckForPointerTarget(targetHit.collider, this);
                }
                else
                {
                    // No hit at all
                    TargetResult = PointerSurfaceResultEnum.None;
                    TotalLength = maxDistance;
                    TargetPoint = StartPoint + PointerForward * maxDistance;
                }

                // Set the line & line renderer props
                line.enabled = true;
                line.SetFirstPoint(StartPoint);
                line.SetLastPoint(TargetPoint);

                for (int i = 0; i < renderers.Length; i++)
                {
                    renderers[i].LineColor = GetColor(TargetResult);
                }
            }
            else
            {
                line.enabled = false;
            }
        }
    }
}

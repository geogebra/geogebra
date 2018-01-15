// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

using System.Collections.Generic;
using UnityEngine;

namespace HoloToolkit.Unity.Design
{
    public abstract class LineBase : MonoBehaviour
    {
        protected const float MinRotationMagnitude = 0.0001f;

        public float UnclampedWorldLength
        {
            get
            {
                return GetUnclampedWorldLengthInternal();
            }
        }

        [Header("Basic Settings")]
        public LineUtils.SpaceEnum Space = LineUtils.SpaceEnum.Local;
        [Range(0f, 1f)]
        public float LineStartClamp = 0f;
        [Range(0f, 1f)]
        public float LineEndClamp = 1f;

        public virtual bool Loops
        {
            get
            {
                return loops;
            }
        }

        [Header("Rotation")]
        public LineUtils.RotationTypeEnum RotationType = LineUtils.RotationTypeEnum.Velocity;

        public bool FlipUpVector = false;

        public Vector3 OriginOffset = Vector3.zero;

        [Range(0f, 1f)]
        public float ManualUpVectorBlend = 0f;

        public Vector3[] ManualUpVectors = new Vector3[] { Vector3.up };

        [Range(0.0001f, 0.1f)]
        public float VelocitySearchRange = 0.02f;
        [Range(0f, 1f)]
        public float VelocityBlend = 0.5f;

        [Header("Distortion")]
        public AnimationCurve DistortionStrength = AnimationCurve.Linear(0f, 1f, 1f, 1f);

        // Abstract
        public abstract int NumPoints { get; }

        protected abstract void SetPointInternal(int pointIndex, Vector3 point);

        /// <summary>
        /// Get a point based on normalized distance along line
        /// Normalized distance will be pre-clamped
        /// </summary>
        /// <param name="normalizedLength"></param>
        /// <returns></returns>
        protected abstract Vector3 GetPointInternal(float normalizedLength);

        /// <summary>
        /// Get a point based on point index
        /// Point index will be pre-clamped
        /// </summary>
        /// <param name="pointIndex"></param>
        /// <returns></returns>
        protected abstract Vector3 GetPointInternal(int pointIndex);

        /// <summary>
        /// Gets the up vector at a normalized length along line (used for rotation)
        /// </summary>
        /// <param name="normalizedLength"></param>
        /// <returns></returns>
        protected virtual Vector3 GetUpVectorInternal(float normalizedLength)
        {
            return transform.forward;
        }

        /// <summary>
        /// Get the UNCLAMPED world length of the line
        /// </summary>
        /// <returns></returns>
        protected abstract float GetUnclampedWorldLengthInternal();

        // Public

        // Convenience
        public void SetFirstPoint(Vector3 point)
        {
            SetPoint(0, point);
        }

        public void SetLastPoint(Vector3 point)
        {
            SetPoint(NumPoints - 1, point);
        }

        /// <summary>
        /// Places all points between the first and last point in a straight line
        /// </summary>
        public virtual void MakeStraightLine()
        {
            if (NumPoints > 2)
            {
                Vector3 startPosition = GetPoint(0);
                Vector3 endPosition = GetPoint(NumPoints - 1);
                for (int i = 1; i < NumPoints - 2; i++)
                {
                    SetPoint(i, Vector3.Lerp(startPosition, endPosition, (1f / NumPoints * 1)));
                }
            }
        }

        /// <summary>
        /// Returns a normalized length corresponding to a world length
        /// Useful for determining LineStartClamp / LineEndClamp values
        /// </summary>
        /// <param name="worldLength"></param>
        /// <param name="searchResolution"></param>
        /// <returns></returns>
        public float GetNormalizedLengthFromWorldLength(float worldLength, int searchResolution = 10)
        {
            Vector3 lastPoint = GetUnclampedPoint(0f);
            Vector3 currentPoint = Vector3.zero;
            float normalizedLength = 0f;
            float distanceSoFar = 0f;
            for (int i = 1; i < searchResolution; i++)
            {
                normalizedLength = (1f / searchResolution) * i;
                currentPoint = GetUnclampedPoint(normalizedLength);
                distanceSoFar += Vector3.Distance(lastPoint, currentPoint);
                lastPoint = currentPoint;
                if (distanceSoFar >= worldLength)
                {
                    break;
                }
;
            }
            return Mathf.Clamp01(normalizedLength);
        }

        /// <summary>
        /// Gets the velocity along the line
        /// </summary>
        /// <param name="normalizedLength"></param>
        /// <returns></returns>
        public Vector3 GetVelocity(float normalizedLength)
        {
            Vector3 velocity = Vector3.zero;
            if (normalizedLength < VelocitySearchRange)
            {
                Vector3 currentPos = GetPoint(normalizedLength);
                Vector3 nextPos = GetPoint(normalizedLength + VelocitySearchRange);
                velocity = (nextPos - currentPos).normalized;
            }
            else
            {
                Vector3 currentPos = GetPoint(normalizedLength);
                Vector3 prevPos = GetPoint(normalizedLength - VelocitySearchRange);
                velocity = (currentPos - prevPos).normalized;
            }
            return velocity;
        }

        /// <summary>
        /// Gets the rotation of a point along the line at the specified length
        /// </summary>
        /// <param name="normalizedLength"></param>
        /// <param name="rotationType"></param>
        /// <returns></returns>
        public Quaternion GetRotation(float normalizedLength, LineUtils.RotationTypeEnum rotationType = LineUtils.RotationTypeEnum.None)
        {
            rotationType = (rotationType != LineUtils.RotationTypeEnum.None) ? rotationType : RotationType;
            Vector3 rotationVector = Vector3.zero;

            switch (rotationType)
            {
                case LineUtils.RotationTypeEnum.None:
                default:
                    break;

                case LineUtils.RotationTypeEnum.Velocity:
                    rotationVector = GetVelocity(normalizedLength);
                    break;

                case LineUtils.RotationTypeEnum.RelativeToOrigin:
                    Vector3 point = GetPoint(normalizedLength);
                    Vector3 origin = transform.TransformPoint(OriginOffset);
                    rotationVector = (point - origin).normalized;
                    break;

            }

            if (rotationVector.magnitude < MinRotationMagnitude)
                return transform.rotation;

            Vector3 upVector = GetUpVectorInternal(normalizedLength);

            if (ManualUpVectorBlend > 0f)
            {
                Vector3 manualUpVector = LineUtils.GetVectorCollectionBlend(ManualUpVectors, normalizedLength, Loops);
                upVector = Vector3.Lerp(upVector, manualUpVector, manualUpVector.magnitude);
            }

            if (FlipUpVector)
                upVector = -upVector;

            return Quaternion.LookRotation(rotationVector, upVector);
        }

        /// <summary>
        /// Gets the rotation of a point along the line at the specified index
        /// </summary>
        /// <param name="pointIndex"></param>
        /// <param name="rotationType"></param>
        /// <returns></returns>
        public Quaternion GetRotation(int pointIndex, LineUtils.RotationTypeEnum rotationType = LineUtils.RotationTypeEnum.None)
        {
            return GetRotation((float)pointIndex / NumPoints, (rotationType != LineUtils.RotationTypeEnum.None) ? rotationType : RotationType);
        }

        /// <summary>
        /// Gets a point along the line at the specified length
        /// </summary>
        /// <param name="normalizedLength"></param>
        /// <returns></returns>
        public Vector3 GetPoint(float normalizedLength)
        {
            if (distorters == null)
                FindDistorters();

            normalizedLength = ClampedLength(normalizedLength);
            switch (Space)
            {
                case LineUtils.SpaceEnum.Local:
                default:
                    return transform.TransformPoint(DistortPoint(GetPointInternal(normalizedLength), normalizedLength));

                case LineUtils.SpaceEnum.Global:
                    return DistortPoint(GetPointInternal(normalizedLength), normalizedLength);
            }
        }

        /// <summary>
        /// Gets a point along the line at the specified length without using LineStartClamp or LineEndClamp
        /// </summary>
        /// <param name="normalizedLength"></param>
        /// <returns></returns>
        public Vector3 GetUnclampedPoint(float normalizedLength)
        {
            if (distorters == null)
                FindDistorters();

            normalizedLength = Mathf.Clamp01(normalizedLength);
            switch (Space)
            {
                case LineUtils.SpaceEnum.Local:
                default:
                    return transform.TransformPoint(DistortPoint(GetPointInternal(normalizedLength), normalizedLength));

                case LineUtils.SpaceEnum.Global:
                    return DistortPoint(GetPointInternal(normalizedLength), normalizedLength);
            }
        }

        /// <summary>
        /// Gets a point along the line at the specified index
        /// </summary>
        /// <param name="pointIndex"></param>
        /// <returns></returns>
        public Vector3 GetPoint(int pointIndex)
        {
            if (pointIndex < 0 || pointIndex >= NumPoints)
                throw new System.IndexOutOfRangeException();

            switch (Space)
            {
                case LineUtils.SpaceEnum.Local:
                    return transform.TransformPoint(GetPointInternal(pointIndex));

                case LineUtils.SpaceEnum.Global:
                default:
                    return GetPointInternal(pointIndex);
            }
        }

        /// <summary>
        /// Sets a point in the line
        /// This function is not guaranteed to have an effect
        /// </summary>
        /// <param name="pointIndex"></param>
        /// <param name="point"></param>
        public void SetPoint(int pointIndex, Vector3 point)
        {
            if (pointIndex < 0 || pointIndex >= NumPoints)
                throw new System.IndexOutOfRangeException();

            switch (Space)
            {
                case LineUtils.SpaceEnum.Local:
                    SetPointInternal(pointIndex, transform.InverseTransformPoint(point));
                    break;

                case LineUtils.SpaceEnum.Global:
                default:
                    SetPointInternal(pointIndex, point);
                    break;
            }
        }

        public virtual void AppendPoint(Vector3 point)
        {
            // Does nothing by default
        }

        // Private & protected
        protected virtual void OnEnable()
        {
            // Reset this every time we're enabled
            // This will help to ensure that our distorters list is updated
            distorters = null;
        }

        private Vector3 DistortPoint(Vector3 point, float normalizedLength)
        {
            float strength = DistortionStrength.Evaluate(normalizedLength);
            for (int i = 0; i < distorters.Length; i++)
            {
                // Components may be added or removed
                if (distorters[i] != null)
                {
                    point = distorters[i].DistortPoint(point, strength);
                }
            }
            return point;
        }

        private float ClampedLength(float normalizedLength)
        {
            return Mathf.Lerp(Mathf.Max(LineStartClamp, 0.0001f), Mathf.Min(LineEndClamp, 0.9999f), Mathf.Clamp01(normalizedLength));
        }

        private void FindDistorters()
        {
            // Get all of the distorters attached to this gameobject
            // Sort by distort order
            Component[] distorterComponents = gameObject.GetComponents(typeof(IDistorter));
            List<IDistorter> distorterList = new List<IDistorter>();
            for (int i = 0; i < distorterComponents.Length; i++)
            {
                distorterList.Add((IDistorter)distorterComponents[i].GetComponent(typeof(IDistorter)));
            }
            distorterList.Sort(delegate (IDistorter d1, IDistorter d2)
            {
                return d1.DistortOrder.CompareTo(d2.DistortOrder);
            });
            distorters = distorterList.ToArray();
        }

        private IDistorter[] distorters;

        [SerializeField]
        protected bool loops = false;

#if UNITY_EDITOR
        protected virtual void OnDrawGizmos()
        {
            if (Application.isPlaying)
                return;

            // Only draw a gizmo if we don't have a line renderer
            LineRenderer lr = gameObject.GetComponent<LineRenderer>();
            if (lr != null)
                return;

            Vector3 firstPos = GetPoint(0f);
            Vector3 lastPos = firstPos;
            Gizmos.color = Color.Lerp(Color.white, Color.clear, 0.25f);
            int numSteps = 16;

            for (int i = 1; i < numSteps; i++)
            {
                float normalizedLength = (1f / numSteps) * i;
                Vector3 currentPos = GetPoint(normalizedLength);
                Gizmos.DrawLine(lastPos, currentPos);
                lastPos = currentPos;
            }

            if (Loops)
            {
                Gizmos.DrawLine(lastPos, firstPos);
            }
        }
#endif
    }
}
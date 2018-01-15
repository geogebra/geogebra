// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

using UnityEngine;

namespace HoloToolkit.Unity.Design
{
    public interface IDistorter
    {
        int DistortOrder { get; set; }

        Vector3 DistortPoint(Vector3 point, float strength);
    }

    public abstract class Distorter : MonoBehaviour, IDistorter
    {
        [SerializeField]
        protected int distortOrder = 0;
        public int DistortOrder
        {
            get { return distortOrder; }
            set { distortOrder = value; }
        }

        public abstract Vector3 DistortPoint(Vector3 point, float strength);
    }
}
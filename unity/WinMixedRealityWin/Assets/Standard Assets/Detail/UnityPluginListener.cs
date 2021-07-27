using UnityEngine;
using System;
using System.Collections.Generic;

namespace Coherent.UIGT
{
public class UnityPluginListener : IUnityPluginListener
{
	public event Action<IntPtr> UserImageTextureReleased;

	public override void OnUserImageTextureReleased(IntPtr texturePtr)
	{
		if (UserImageTextureReleased != null)
		{
			UserImageTextureReleased(texturePtr);
		}
	}
}
}
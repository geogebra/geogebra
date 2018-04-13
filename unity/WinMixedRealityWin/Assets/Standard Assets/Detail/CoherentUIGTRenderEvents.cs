using UnityEngine;
using System;
using System.Runtime.InteropServices;

namespace Coherent.UIGT
{
	public enum CoherentRenderEventType
	{
		DrawView = 1,
		SetNewRenderTarget = 2,
		IssueMouseOnUIQuery = 3,
		FetchMouseOnUIQuery = 4,
		CreateSystemRenderer = 5,
		CreateViewRenderer = 6,
		DestroySystemRenderer = 7,
		DestroyViewRenderer = 8,
		SetLiveViewTexture = 9,
	};

	public static class CoherentUIGTRenderEvents
	{
		[DllImport("CoherentUIGT_Native")] static extern IntPtr GetRenderEventFunc();
		static IntPtr sm_RenderEventFunc = GetRenderEventFunc();

		const byte COHERENT_GT_PREFIX = 213;

		public static void SendRenderEvent(
			CoherentRenderEventType evType,
			uint viewId)
		{
			int eventId = COHERENT_GT_PREFIX << 24;
			eventId |= ((int)(viewId & 0xFFFFF) << 4);
			eventId |= ((int)evType & 0xF);

			GL.IssuePluginEvent(sm_RenderEventFunc, eventId);
		}
	}
}

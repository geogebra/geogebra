#if UNITY_2_6 || UNITY_2_6_1 || UNITY_3_0 || UNITY_3_0_0 || UNITY_3_1 || UNITY_3_2 || UNITY_3_3 || UNITY_3_4 || UNITY_3_5 || UNITY_4_0 || UNITY_4_0_1
#define COHERENT_UNITY_PRE_4_1
#endif

using UnityEngine;
using System.Collections;
using System.Collections.Generic;

using Coherent.UIGT;

[DisallowMultipleComponent]
public class CoherentUIGTViewRenderer : MonoBehaviour
{
	bool hasDrawn;

	internal bool IsActive
	{
		get;
		set;
	}

	internal ushort ViewId
	{
		get;
		set;
	}

	public void IssueMouseOnUIQuery()
	{
		CoherentUIGTRenderEvents.SendRenderEvent(CoherentRenderEventType.IssueMouseOnUIQuery,
			ViewId);
	}

	public void FetchMouseOnUIQuery()
	{
		CoherentUIGTRenderEvents.SendRenderEvent(CoherentRenderEventType.FetchMouseOnUIQuery,
			ViewId);
	}

	private void Draw()
	{
		if (!IsActive || !enabled) return;

		#if COHERENT_UNITY_PRE_4_1
		EnableShaderKeywords();
		#endif

		CoherentUIGTRenderEvents.SendRenderEvent(CoherentRenderEventType.DrawView,
			ViewId);
	}

	void Awake()
	{
		ViewId = 0;
		IsActive = true;
	}

	void Update()
	{
		hasDrawn = false;
	}

	void OnPreRender()
	{
		IssueMouseOnUIQuery();
		Draw();
	}

	void OnWillRenderObject()
	{
		if (!hasDrawn)
		{
			Draw();
			hasDrawn = true;
		}
	}

	void OnPostRender()
	{
		FetchMouseOnUIQuery();
	}

	#if COHERENT_UNITY_PRE_4_1
	internal List<string> ShaderKeywords
	{
		get;
		set;
	}

	internal void EnableShaderKeywords()
	{
		if (!ShaderKeywords.Contains("COHERENT_CORRECT_GAMMA"))
			Shader.DisableKeyword("COHERENT_CORRECT_GAMMA");

		if (!ShaderKeywords.Contains("COHERENT_FLIP_Y"))
			Shader.DisableKeyword ("COHERENT_FLIP_Y");

		foreach (string keyword in ShaderKeywords)
		{
			Shader.EnableKeyword(keyword);
		}
	}
	#endif
}

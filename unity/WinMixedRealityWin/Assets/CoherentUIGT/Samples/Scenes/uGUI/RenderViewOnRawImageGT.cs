using UnityEngine;
using UnityEngine.UI;

[RequireComponent(typeof(Camera))]
[RequireComponent(typeof(RawImage))]
[RequireComponent(typeof(CoherentUIGTView))]
public class RenderViewOnRawImageGT : MonoBehaviour
{
	RawImage m_RawImageComponent;
	CoherentUIGTView m_ViewComponent;

	void Awake()
	{
		m_ViewComponent = GetComponent<CoherentUIGTView>();
		m_RawImageComponent = GetComponent<RawImage>();

		//Create a camera component and render texture for the View to draw on
		Camera cameraComponent = GetComponent<Camera>();
		cameraComponent.clearFlags = CameraClearFlags.SolidColor;
		cameraComponent.backgroundColor = new Color(0, 0, 0, 0);
		cameraComponent.cullingMask = 0;
		RenderTexture renderTexture = new RenderTexture(m_ViewComponent.Width,
		                                                m_ViewComponent.Height,
		                                                1, RenderTextureFormat.ARGB32,
		                                                RenderTextureReadWrite.Default);
		renderTexture.name = "CoherentViewRenderTexture";
		cameraComponent.targetTexture = renderTexture;
		m_RawImageComponent.texture = renderTexture;
	}
}

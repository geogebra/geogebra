using UnityEngine;
using UnityEngine.EventSystems;

[RequireComponent(typeof(RectTransform))]
[RequireComponent(typeof(CoherentUIGTView))]
public class InputForwardFromUnityGUIGT : MonoBehaviour, IPointerEnterHandler, IPointerExitHandler
{
	RectTransform m_RectTransform;
	CoherentUIGTView m_ViewComponent;
	Camera m_UICamera;

	public void OnPointerEnter(PointerEventData eventData)
	{
		m_ViewComponent.ReceivesInput = true;
	}

	public void OnPointerExit(PointerEventData eventData)
	{
		m_ViewComponent.ReceivesInput = false;
	}

	void Awake()
	{
		m_RectTransform = GetComponent<RectTransform>();
		m_ViewComponent = GetComponent<CoherentUIGTView>();

		m_ViewComponent.ClickToFocus = false;
		m_ViewComponent.UseCameraDimensions = false;

		Canvas[] canvases = GetComponentsInParent<Canvas>();
		Canvas rootCanvas = canvases[canvases.Length - 1];

		if (rootCanvas.renderMode != RenderMode.ScreenSpaceOverlay)
		{
			m_UICamera = rootCanvas.worldCamera;
		}
	}

	void Update()
	{
		if (m_ViewComponent.ReceivesInput)
		{
			Vector2 mousePosition = GetTranslatedInputCoordinates();
			m_ViewComponent.SetMousePosition((int)mousePosition.x, (int)mousePosition.y);
		}
	}

	Vector2 GetTranslatedInputCoordinates()
	{
		Vector2 rectPositionFlippedPivoted;
		if (!RectTransformUtility.ScreenPointToLocalPointInRectangle(GetComponent<RectTransform>(),
		                                                             Input.mousePosition,
		                                                             m_UICamera,
		                                                             out rectPositionFlippedPivoted))
		{
			return new Vector2(-1f, -1f);
		}

		Vector2 relativePositionFlippedPivoted = new Vector2(
			rectPositionFlippedPivoted.x / m_RectTransform.rect.size.x,
			rectPositionFlippedPivoted.y / m_RectTransform.rect.size.y);
		Vector2 relativePositionFlipped = relativePositionFlippedPivoted + m_RectTransform.pivot;
		Vector2 relativePosition = new Vector2(relativePositionFlipped.x, 1f - relativePositionFlipped.y);

		return new Vector2(m_ViewComponent.Width * relativePosition.x,
		                   m_ViewComponent.Height * relativePosition.y);
	}
}

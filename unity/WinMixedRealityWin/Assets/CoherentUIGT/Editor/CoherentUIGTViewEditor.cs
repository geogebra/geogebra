using UnityEngine;
using UnityEditor;

[CustomEditor(typeof(CoherentUIGTView))]
public class CoherentUIGTViewEditor : Editor
{
	private CoherentUIGTView m_Target;
	private CoherentUIGTFoldout[] m_Fields;

	private GUIContent m_ButtonContentEnabled;
	private GUIContent m_ButtonContentDisabled;

	public void OnEnable()
	{
		m_Target = target as CoherentUIGTView;
		m_Fields = CoherentUIGTExposeProperties.GetProperties(m_Target);
		m_ButtonContentEnabled = new GUIContent("Open in Coherent Editor",
			"Edit this page in Coherent Editor");
		m_ButtonContentDisabled = new GUIContent("Open in Coherent Editor",
			"Only local coui resources can be edited in Coherent Editor");
	}

	public override void OnInspectorGUI()
	{
		if (m_Target == null)
		{
			return;
		}

		this.DrawDefaultInspector();
		CoherentUIGTExposeProperties.Expose(m_Fields);

		GUI.enabled = m_Target.Page.StartsWith("coui://uiresources/");
		if (GUILayout.Button(GUI.enabled ? m_ButtonContentEnabled :
										   m_ButtonContentDisabled))
		{
			CoherentUIGTEditorMenu.LaunchCoherentEditorWithURL(m_Target.Page);
		}
	}
}

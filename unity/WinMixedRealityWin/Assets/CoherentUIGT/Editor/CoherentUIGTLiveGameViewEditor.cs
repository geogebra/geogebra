using UnityEditor;

[CustomEditor(typeof(CoherentUIGTLiveGameView))]
public class CoherentUIGTLiveGameViewEditor : Editor
{
	private CoherentUIGTLiveGameView m_Target;
	private CoherentUIGTFoldout[] m_Fields;

	public void OnEnable()
	{
		m_Target = target as CoherentUIGTLiveGameView;
		m_Fields = CoherentUIGTExposeProperties.GetProperties(m_Target);
	}

	public override void OnInspectorGUI()
	{
		if (m_Target == null)
		{
			return;
		}

		this.DrawDefaultInspector();
		CoherentUIGTExposeProperties.Expose(m_Fields);
	}
}

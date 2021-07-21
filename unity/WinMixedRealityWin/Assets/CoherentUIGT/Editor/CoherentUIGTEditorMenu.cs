using System.IO;
using UnityEngine;
using UnityEditor;
using Process = System.Diagnostics.Process;
using ProcessStartInfo = System.Diagnostics.ProcessStartInfo;
using Uri = System.Uri;

public static partial class CoherentUIGTEditorMenu
{
	[MenuItem("Coherent GT/Launch Debugger", false, 1)]
	static void LaunchDebugger()
	{
		string debuggerPath = Path.Combine(Application.dataPath, "CoherentUIGT/Editor/Debugger");

		if (Application.platform == RuntimePlatform.OSXEditor)
		{
			debuggerPath = Path.Combine(debuggerPath, "MacOSX/Debugger.app");
		}
		else if (Application.platform == RuntimePlatform.WindowsEditor)
		{
			debuggerPath = Path.Combine(debuggerPath, "Win/Debugger.exe");
		}
		else
		{
			Debug.LogError("Platform not supported.");
			return;
		}

		ProcessStartInfo startInfo = new ProcessStartInfo(debuggerPath);
		startInfo.WorkingDirectory = debuggerPath.Remove(debuggerPath.LastIndexOf('/'));

		Process.Start(startInfo);
	}

	[MenuItem("Coherent GT/Add Coherent System", false, 100)]
	static void AddCoherentSystem()
	{
		CoherentUIGTSystem system = Object.FindObjectOfType(typeof(CoherentUIGTSystem)) as CoherentUIGTSystem;

		if (system != null)
		{
			Debug.Log("Coherent GT System already added in scene.");
		}
		else
		{
			GameObject go = new GameObject("CoherentGTSystem");
			go.AddComponent<CoherentUIGTSystem>();
		}
	}

	[MenuItem("Coherent GT/Add World View", false, 101)]
	static void AddWorldView()
	{
		GameObject go = new GameObject("CoherentWorldView");
		go.AddComponent<MeshFilter>().mesh = GenerateQuad();
        go.AddComponent<MeshRenderer>().material = new Material(Shader.Find("Coherent/TransparentDiffuse"));
        go.AddComponent<MeshCollider>();
		go.AddComponent<CoherentUIGTView>();

		Debug.Log("CoherentWorldView gameobject added in scene.");
	}

	[MenuItem("Coherent GT/Add Screen View", false, 102)]
	static void AddScreenView()
	{
		if (Camera.main == null)
		{
			GameObject go = new GameObject("Main Camera");
			go.tag = "MainCamera";
			go.AddComponent<Camera>();
			go.AddComponent<GUILayer>();
			go.AddComponent<AudioListener>();
		}

		if (Camera.main.GetComponent<CoherentUIGTView>() != null)
		{
			Debug.Log("Coherent GT View already added on main camera.");
		}
		else
		{
			Camera.main.gameObject.AddComponent<CoherentUIGTView>();
			Debug.Log("Coherent GT View added on main camera.");
		}
	}

	[MenuItem("Coherent GT/Add Live View Camera", false, 103)]
	static void AddLiveViewCamera()
	{
		GameObject go = new GameObject("CoherentLiveViewCamera");
		go.AddComponent<Camera>().depth = -10;
		go.AddComponent<CoherentUIGTLiveGameView>();

		Debug.Log("CoherentLiveViewCamera gameobject added in scene.");
	}

	[MenuItem("Coherent GT/Open GT Documentation", false, 200)]
	static void CoherentGTDocumentation()
	{
		Application.OpenURL("https://coherent-labs.com/Documentation/unity-gt/");
	}

	static Mesh GenerateQuad()
	{
		Mesh mesh = new Mesh();
		mesh.name = "Generated Quad";
		mesh.vertices = new Vector3[]
		{
			new Vector3(-0.5f, -0.5f, 0f),
			new Vector3(-0.5f, 0.5f, 0f),
			new Vector3(0.5f, 0.5f, 0f),
			new Vector3(0.5f, -0.5f, 0f),

		};
		mesh.uv = new Vector2[]
		{
			new Vector2(0f, 1f),
			new Vector2 (0f, 0f),
			new Vector2(1f, 0f),
			new Vector2 (1f, 1f),
		};
		mesh.triangles = new int[] {0, 1, 2, 0, 2, 3};
		mesh.RecalculateNormals();

		return mesh;
	}
}

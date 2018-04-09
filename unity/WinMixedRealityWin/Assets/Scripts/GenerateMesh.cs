using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using System.Linq;

public class GenerateMesh : MonoBehaviour
{
    public bool renderFromtTextFile = true;
    [Space(5)]
    [Header("Input for Mesh")]
    [TextArea(15, 20)]
    public string inputString = "v 0.0 0.0 0.0 1.0 0.0 0.0 1.0 1.0 0.0 0.0 1.0 0.0 \nn 0.0 0.0 1.0 0.0 0.0 1.0 0.0 0.0 1.0 0.0 0.0 1.0 \nt 0 1 2 0 2 3 ";

    [Space(5)]

    [Header("Input paths for meshes")]
    public string axesPath;
    public string gridPath;
    public string surfacePath;

    [Header("Loaded attributes for Mesh")]
    public List<Vector3> meshVerticlesList;
    public List<Vector3> meshNormalsList;
    public List<int> meshTrianglesList;
    public List<Color> colorsList;
    public Material mat;

    public GameObject spawnerObj;
    private int debugCounter = 0;
    private int debugCounter2 = 0;
    private GameObject parentObj;
    private enum MeshParameter { Verticles, Normals, Triangles, Color };

    // Use this for initialization
    void Start()
    {
        if (spawnerObj == null)
        {
            spawnerObj = GameObject.Find("GeneratedModelsSpawner");
        }
        float testFloat = 12.5f;
        string testString = "15.8";
        testFloat = float.Parse(testString);
    }

    // Update is called once per frame
    void Update()
    {

    }

    public void GenerateGGBMesh()
    {
        MeshFromString(axesPath, "axes");
        MeshFromString(gridPath, "grid");
        MeshFromString(surfacePath, "surface");
    }

    public void MeshFromString(string path, string name)
    {
        if (parentObj == null)
        {
            parentObj = new GameObject("Generated Surface");
            parentObj.transform.position = spawnerObj.transform.position;
            parentObj.transform.localRotation = Quaternion.Euler(-90, 0, 0);
            parentObj.transform.localScale = new Vector3(0.4f, 0.4f, 0.4f);
        }

        int counter = 0;
        string line;
        float tempFloat = 0;
        int tempInt = 0;
        bool templBool = false;

        MeshParameter meshParameter;
        meshParameter = MeshParameter.Verticles;
        StreamReader streamReader;

        streamReader = new StreamReader("Assets/" + path + ".txt"); 

        meshVerticlesList = new List<Vector3>();

        while ((line = streamReader.ReadLine()) != null)
        {
            if (System.Char.IsLetter(line[0]))
            {
                //adding Enum
                switch (line[0])
                {
                    case 'v':
                        meshParameter = MeshParameter.Verticles;
                        meshVerticlesList = new List<Vector3>();
                        break;


                    case 't':
                        meshParameter = MeshParameter.Triangles;
                        meshTrianglesList = new List<int>();
                        break;

                    case 'n':
                        meshParameter = MeshParameter.Normals;
                        meshNormalsList = new List<Vector3>();
                        break;

                    case 'c':
                        meshParameter = MeshParameter.Color;
                        colorsList = new List<Color>();
                        break;
                }
            }

            //Spliting the line
            line = line.Substring(2, line.Length - 2);
            string[] bits = line.Split(' ');
            int axisCounter = 0;
            float _x = 0;
            float _y = 0;
            float _z = 0;
            float _t = 0;

            //Filling list from fragments
            foreach (string bit in bits)
            {
                switch (meshParameter)
                {
                    case MeshParameter.Verticles:
                        tempFloat = float.Parse(bit);
                        if (axisCounter == 0)
                        {
                            _x = tempFloat;
                            axisCounter++;
                        }
                        else if (axisCounter == 1)
                        {
                            _y = tempFloat;
                            axisCounter++;
                        }
                        else if (axisCounter >= 2)
                        {
                            _z = tempFloat;
                            meshVerticlesList.Add(new Vector3(_x, _y, _z));
                            axisCounter = 0;
                        }
                        break;

                    case MeshParameter.Triangles:

                        tempInt = int.Parse(bit);

                        meshTrianglesList.Add(tempInt);
                        break;

                    case MeshParameter.Normals:

                        tempFloat = float.Parse(bit);
                        if (axisCounter == 0)
                        {
                            _x = tempFloat;
                            axisCounter++;
                        }
                        else if (axisCounter == 1)
                        {
                            _y = tempFloat;
                            axisCounter++;
                        }
                        else if (axisCounter >= 2)
                        {
                            _z = tempFloat;
                            meshNormalsList.Add(new Vector3(_x, _y, _z));
                            axisCounter = 0;
                        }
                        break;

                    case MeshParameter.Color:

                        tempFloat = float.Parse(bit);
                        if (axisCounter == 0)
                        {
                            _x = tempFloat;
                            axisCounter++;
                        }
                        else if (axisCounter == 1)
                        {
                            _y = tempFloat;
                            axisCounter++;
                        }
                        else if (axisCounter == 2)
                        {
                            _z = tempFloat;
                            axisCounter++;
                        }
                        else if (axisCounter >= 3)
                        {
                            _t = tempFloat;
                            colorsList.Add(new Color(_x, _y, _z, _t));
                            axisCounter = 0;
                        }
                        break;
                }
            }
        }
        // Create new Mesh
        GameObject go = new GameObject(name);
        MeshFilter mf = go.AddComponent(typeof(MeshFilter)) as MeshFilter;
        MeshRenderer meshRenderer = go.AddComponent(typeof(MeshRenderer)) as MeshRenderer;
        Mesh mesh = new Mesh();

        mesh.vertices = meshVerticlesList.ToArray();
        int[] myTriangleArray = new int[meshTrianglesList.Count];
        int icounter = 0;
        foreach (int i in meshTrianglesList)
        {
            myTriangleArray[icounter] = i;
            icounter++;
        }
        mesh.triangles = new int[4176];
        mesh.triangles = myTriangleArray;
        mesh.normals = meshNormalsList.ToArray();
        
        //Assigning material
        if (mat != null)
        {
            meshRenderer.material = mat;
        }
        else
        {
            meshRenderer.material = new Material(Shader.Find("Diffuse"));
        }

        mesh.colors = colorsList.ToArray();

        //mf.mesh = mesh;


        //Creating Double sides mesh script
        var vertices = mesh.vertices;
        var colors = mesh.colors;
        var normals = mesh.normals;
        var szV = vertices.Length;
        var newVerts = new Vector3[szV * 2];
        var newColors = new Color[szV * 2];
        var newNorms = new Vector3[szV * 2];
        for (var j = 0; j < szV; j++)
        {
            // duplicate vertices and uvs:
            newVerts[j] = newVerts[j + szV] = vertices[j];
            newColors[j] = newColors[j + szV] = colors[j];
            // copy the original normals...
            newNorms[j] = normals[j];
            // and revert the new ones
            newNorms[j + szV] = -normals[j];
        }
        var triangles = mesh.triangles;
        var szT = triangles.Length;
        var newTris = new int[szT * 2]; // double the triangles
        for (var i = 0; i < szT; i += 3)
        {
            // copy the original triangle
            newTris[i] = triangles[i];
            newTris[i + 1] = triangles[i + 1];
            newTris[i + 2] = triangles[i + 2];
            // save the new reversed triangle
            var j = i + szT;
            newTris[j] = triangles[i] + szV;
            newTris[j + 2] = triangles[i + 1] + szV;
            newTris[j + 1] = triangles[i + 2] + szV;
        }
        mesh.vertices = newVerts;
        mesh.colors = newColors;
        mesh.normals = newNorms;
        mesh.triangles = newTris; // assign triangles last!

        mf.mesh = mesh;
        go.transform.parent = parentObj.transform;
        go.transform.position = parentObj.transform.position;
        go.transform.rotation = parentObj.transform.rotation;
        go.transform.localScale = parentObj.transform.localScale;

    }
}

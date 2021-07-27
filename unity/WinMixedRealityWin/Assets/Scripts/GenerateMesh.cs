using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using System.Linq;
using Coherent.UIGT;


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
    public string finalPath;

    [Header("Loaded attributes for Mesh")]
    public List<Vector3> meshVerticlesList;
    public List<Vector3> meshNormalsList;
    public List<int> meshTrianglesList;
    public List<Color> colorsList;
    public Material matSurface;
    public Material matCurves;
    public Material matAxes;
    public string currentName;
    public string previouseName;

    public GameObject spawnerObj;
    private GameObject parentObj;
    private enum MeshParameter { ModelName, MeshName, Verticles, Normals, Triangles, Color };

    [HideInInspector]
    public string testSpeedForCohorentGt;


    public GameObject[] functionsArray;

    // Use this for initialization
    void Start()
    {
        functionsArray = new GameObject[10];

        if (spawnerObj == null)
        {
            spawnerObj = GameObject.Find("GeneratedModelsSpawner");
        }
        

        if (matSurface == null)
        {
            matSurface = new Material(Shader.Find("Diffuse"));
        }
        if (matCurves == null)
        {
            matSurface = new Material(Shader.Find("Diffuse"));
        }
        if (matAxes == null)
        {
            matAxes = new Material(Shader.Find("Diffuse"));
        }
    }
    public void TestSpeedForCohorentGt()
    {
        MeshFromString(testSpeedForCohorentGt);
    }

    public void MeshFromString(string GGBString)
    {
        if (parentObj == null)
        {
            parentObj = GameObject.Find("Generated Surface");
            if (parentObj == null)
            {
                parentObj = new GameObject("Generated Surface");
                parentObj.transform.position = spawnerObj.transform.position;
                parentObj.transform.localRotation = Quaternion.Euler(-90, 0, 0);
                parentObj.transform.localScale = new Vector3(0.4f, 0.4f, 0.4f);
            }           
        }        

        int counter = 0;
        string line;
        float tempFloat = 0;
        int tempInt = 0;
        bool templBool = false;
        string modelName;
        GameObject currentParrent = parentObj;

        MeshParameter meshParameter;
        meshParameter = MeshParameter.ModelName;

        StreamReader streamReader;
        StringReader stringReader;

        stringReader = new StringReader(GGBString);
        meshVerticlesList = new List<Vector3>();

        int ObjCount = 0;
        int[] myTriangleArray;

        while ((line = stringReader.ReadLine()) != null)
        {

            if (System.Char.IsLetter(line[0]) || line[0] == '#') 
                                                                 
            //adding Enum
            {
                switch (line[0])
                {
                    case '#':
                        if (line[1] == '#')
                        {
                            meshParameter = MeshParameter.ModelName;                           
                        }
                        else
                        {
                            meshParameter = MeshParameter.MeshName;
                        }                       
                        break;

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
                    case MeshParameter.ModelName:
                        int i = 9;
                        modelName = bit;
                        GameObject go = new GameObject(modelName);
                        currentParrent = go;
                        go.transform.parent = parentObj.transform;
                        go.transform.position = parentObj.transform.position;
                        go.transform.rotation = parentObj.transform.rotation;
                        go.transform.localScale = Vector3.one;
                        go.layer = 8; // 8 is for layer
                        i = ConvertLetterToIndex(modelName);
                        if (functionsArray[i] != null)
                        {
                            Destroy(functionsArray[i]);                            
                        }
                        functionsArray[i] = go;
                        break;
                        

                    case MeshParameter.MeshName:
                        currentName = bit;
                        if (ObjCount > 0)
                        {
                            myTriangleArray = listToArray(meshTrianglesList);

                            CreateMesh(previouseName, meshVerticlesList.ToArray(), myTriangleArray,
                                        meshNormalsList.ToArray(), colorsList.ToArray(), matSurface, currentParrent);
                        }
                        ObjCount++;
                        previouseName = currentName;
                        break;

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

        // Create Mesh
        myTriangleArray = listToArray(meshTrianglesList);
                
        CreateMesh(previouseName, meshVerticlesList.ToArray(), myTriangleArray,
            meshNormalsList.ToArray(), colorsList.ToArray(), matAxes, currentParrent);        
    }

    private void CreateMesh(string nameOfMesh, Vector3[] verticesOfMesh, int[] trianglesOfMesh, Vector3[] normalsOfMesh, Color[] colorsOfMesh, Material material, GameObject _currentParent)
    {
        GameObject go = new GameObject(nameOfMesh);
        MeshFilter mf = go.AddComponent(typeof(MeshFilter)) as MeshFilter;
        MeshRenderer meshRenderer = go.AddComponent(typeof(MeshRenderer)) as MeshRenderer;
        Mesh mesh = new Mesh();

        mesh.vertices = verticesOfMesh;
        mesh.triangles = new int[trianglesOfMesh.Length];
        mesh.triangles = trianglesOfMesh;
        mesh.normals = meshNormalsList.ToArray();
        mesh.colors = colorsOfMesh;
        meshRenderer.material = material;

        //for surfaces double size
        if (nameOfMesh == "surfaces")
        {
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
        }
        Material _mat = matAxes;

        switch (previouseName)
        {
            case "surfaces":
                _mat = matSurface;
                break;

            case "curves":
                _mat = matCurves;
                break;

            case "axes":
                _mat = matAxes;
                break;
        }

        meshRenderer.material = _mat;

        mf.mesh = mesh;
        go.transform.parent = _currentParent.transform;
        go.transform.position = parentObj.transform.position;
        go.transform.rotation = parentObj.transform.rotation;
        Vector3 v = go.transform.rotation.eulerAngles;
        go.transform.rotation = Quaternion.Euler(-90, v.y, v.z);

        go.transform.localScale = parentObj.transform.localScale;
        go.layer = 8; // 8 is for "function" layer
    }

    private int[] listToArray(List<int> list)
    {
        int[] myTriangleArray = new int[list.Count];
        int icounter = 0;
        foreach (int i in list)
        {
            myTriangleArray[icounter] = i;
            icounter++;
        }
        return myTriangleArray;
    }

    public void FunctionToDelete(string name)
    {
        int index = ConvertLetterToIndex(name);
        Destroy(functionsArray[index]);
    }

    private int ConvertLetterToIndex (string letter)
    {
        int i = 9;
        switch (letter)
        {
            case "a":
                i = 0;
                break;
            case "b":
                i = 1;
                break;
            case "c":
                i = 2;
                break;
            case "d":
                i = 3;
                break;
            case "e":
                i = 4;
                break;
            case "f":
                i = 5;
                break;
        }
        return i;
    }
}

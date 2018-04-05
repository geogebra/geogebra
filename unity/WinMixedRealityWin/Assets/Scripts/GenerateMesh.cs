using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using System.Linq;

public class GenerateMesh : MonoBehaviour
{

    [Space(5)]
    [Header("Input for Mesh")]
    [TextArea(15, 20)]
    public string inputString = "v 0.0 0.0 0.0 1.0 0.0 0.0 1.0 1.0 0.0 0.0 1.0 0.0 \nn 0.0 0.0 1.0 0.0 0.0 1.0 0.0 0.0 1.0 0.0 0.0 1.0 \nt 0 1 2 0 2 3 ";

    [Space(5)]
    [Header("Loaded attributes for Mesh")]
    public List<Vector3> meshVerticlesList;
    public List<Vector3> meshNormalsList;
    public List<int> meshTrianglesList;
    public List<Color> colorsList;
    public Material mat;

    private enum MeshParameter { Verticles, Normals, Triangles, Color };

    // Use this for initialization
    void Start()
    {

        float testFloat = 12.5f;
        string testString = "15.8";
        testFloat = float.Parse(testString);
        MeshFromString(inputString);
    }

    // Update is called once per frame
    void Update()
    {

    }

    public void MeshFromString(string inputString)
    {
        int counter = 0;
        string line;
        float tempFloat = 0;
        int tempInt = 0;
        bool templBool = false;

        MeshParameter meshParameter;
        meshParameter = MeshParameter.Verticles;
        StringReader streamReader = new StringReader(inputString);

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
            else
            {
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
        GameObject go = new GameObject("Quad from String");
        MeshFilter mf = go.AddComponent(typeof(MeshFilter)) as MeshFilter;
        MeshRenderer meshRenderer = go.AddComponent(typeof(MeshRenderer)) as MeshRenderer;
        Mesh mesh = new Mesh();

        mesh.vertices = meshVerticlesList.ToArray();
        mesh.triangles = meshTrianglesList.ToArray();
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
        mf.mesh = mesh;
    }
}

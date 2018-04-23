using UnityEngine;
using System.Collections;

public class Propeller : MonoBehaviour
{
	public float m_Speed = 360f;

	void Update()
	{
		transform.RotateAround(transform.position, Vector3.up, m_Speed * Time.deltaTime);
	}
}

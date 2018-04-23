using UnityEngine;
using System.Collections;

public class Turret : MonoBehaviour
{
	public Laser projectilePrefab;

	public void LookAtTarget(Vector3 worldPoint)
	{
		transform.LookAt(worldPoint);
	}

	public void Fire()
	{
		Laser instance = Instantiate(projectilePrefab) as Laser;
		instance.transform.position = transform.position;
		instance.transform.rotation = transform.rotation;
		Destroy(instance.gameObject, 10f);
	}
}

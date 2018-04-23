using UnityEngine;
using System.Collections;

public class Aiming : MonoBehaviour
{
	public Turret[] turrets;

	void Update()
	{
		Ray ray = Camera.main.ViewportPointToRay(Vector3.one * 0.5f);
		RaycastHit hitInfo;

		if (Physics.Raycast(ray, out hitInfo, 1000f))
		{
			foreach (Turret turret in turrets)
			{
				turret.LookAtTarget(hitInfo.point);
			}
		}
		else
		{
			foreach (Turret turret in turrets)
			{
				turret.LookAtTarget(ray.GetPoint(1000f));
			}
		}
	}
}

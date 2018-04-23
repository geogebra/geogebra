using UnityEngine;
using System.Collections;

public class Shooting : MonoBehaviour
{
	public Turret[] turrets;
	public float energy = 100f;
	public float cooldownTime = 0.25f;
	int turretIndex;
	float cooldown;

	void Update()
	{
		if (Input.GetAxis("Fire1") > 0.1f)
		{
			if (cooldown <= 0f && energy > 0f)
			{
				turrets[turretIndex].Fire();
				energy -= 1f;
				MainUI.SetEnergy(energy / 100f);

				turretIndex = ++turretIndex % turrets.Length;

				cooldown = cooldownTime;
			}
		}

		if (cooldown > 0f)
		{
			cooldown -= Time.deltaTime;
		}
	}
}

using UnityEngine;
using System.Collections;

public class Asteroid : MonoBehaviour
{
	public Asteroid[] asteroidPrefabs;
	public ParticleSystemMultiplier explosionPrefab;
	Rigidbody _rigidbody;

	void Awake()
	{
		_rigidbody = GetComponent<Rigidbody>();
	}

	void Start()
	{
		_rigidbody.AddTorque(Random.insideUnitSphere * 0.1f, ForceMode.VelocityChange);
		_rigidbody.AddForce(Random.insideUnitSphere * 0.1f, ForceMode.VelocityChange);
	}

	void OnCollisionEnter(Collision collision)
	{
		_rigidbody.detectCollisions = false;
		StartCoroutine("FadeAndDestroy");
		StartCoroutine("SpawnChild");
	}

	IEnumerator SpawnChild()
	{
		if (transform.localScale.x > 0.25f)
		{
			Asteroid asteroidClone = Instantiate(asteroidPrefabs[Random.Range(0, asteroidPrefabs.Length)]) as Asteroid;
			asteroidClone.transform.position = transform.position;
			asteroidClone.transform.rotation = Random.rotation;
			asteroidClone.transform.localScale = Vector3.one * (transform.localScale.x - 0.25f);
			yield return null;
			asteroidClone.gameObject.SetActive(true);
		}

		ParticleSystemMultiplier explosionInstance = Instantiate(explosionPrefab) as ParticleSystemMultiplier;
		explosionInstance.multiplier = transform.localScale.x;
		explosionInstance.transform.position = transform.position;
	}

	IEnumerator FadeAndDestroy()
	{
		Material material = GetComponent<MeshRenderer>().material;
		Color color = material.color;

		while (color.a > 0f)
		{
			color.a -= 3f * Time.deltaTime;
			material.color = color;
			yield return null;
		}

		Destroy(gameObject);
	}
}

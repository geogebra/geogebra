using UnityEngine;
using System.Collections;

public class Laser : MonoBehaviour
{
	public Transform explosionPrefab;
	public float speed;
	Rigidbody _rigidbody;

	void Awake()
	{
		_rigidbody = GetComponent<Rigidbody>();
	}

	void Start()
	{
		_rigidbody.AddForce(transform.forward * speed, ForceMode.VelocityChange);
	}

	void OnCollisionEnter(Collision collision)
	{
		int awardedPoints = (int)(Random.value * 1000f);
		Spaceship.AddScore(awardedPoints);

		Transform instance = Instantiate(explosionPrefab) as Transform;
		instance.position = transform.position;
		Destroy(gameObject);
	}
}

using UnityEngine;
using System.Collections;

public class Spaceship : MonoBehaviour
{
	public static int score;

	public float health = 100f;
	public float fuel = 1000f;
	public float maxAcceleration = 20f;
	public float turnSpeed = 1f;
	public float acceleration = 10f;
	public Truster[] trusters;
	public ParticleSystemMultiplier explosionPrefab;
	Rigidbody _rigidbody;
	float pitch;
	float yaw;
	int roll;
	float lastAcceleration;

	public static void AddScore(int value)
	{
		score += value;
		MainUI.SetScore(score);
	}

	void Awake()
	{
		_rigidbody = GetComponent<Rigidbody>();
		Physics.IgnoreLayerCollision(2, 2);
	}

	void Update()
	{
		foreach (Truster truster in trusters)
		{
			truster.SetPower(acceleration / maxAcceleration);
		}
		if (fuel > 0f)
		{
			acceleration = Mathf.Clamp(acceleration + Input.GetAxis("Mouse ScrollWheel") * (acceleration + 1f), 0f, maxAcceleration);
		}

		if (Input.GetAxis("Jump") > 0.1f)
		{
			if (lastAcceleration == 0f)
			{
				lastAcceleration = acceleration;
			}
			acceleration = Mathf.Min(acceleration + maxAcceleration * Time.deltaTime, maxAcceleration);
		}
		else if (lastAcceleration != 0f)
		{
			acceleration = lastAcceleration;
			lastAcceleration = 0f;
		}

		pitch = Input.GetAxis("Vertical");
		yaw = Input.GetAxis("Horizontal");
		roll = 0;

		if (Input.GetKey(KeyCode.Q))
		{
			roll++;
		}
		else if (Input.GetKey(KeyCode.E))
		{
			roll--;
		}

		if (Input.GetAxis("Fire2") > 0.1f)
		{
			Ray ray = Camera.main.ViewportPointToRay(Vector3.one * 0.5f);
			RaycastHit hitInfo;

			if (Physics.Raycast(ray, out hitInfo, 1000f))
			{
				MainUI.ShowScan();
			}
			else
			{
				MainUI.ShowOutOfRange();
			}
		}

		MainUI.SetCoordinates(transform.position);
	}

	void FixedUpdate()
	{
		if (fuel > 0f)
		{
			_rigidbody.AddRelativeForce(0f, 0f, acceleration, ForceMode.Acceleration);
			_rigidbody.AddRelativeTorque(pitch * turnSpeed, yaw * turnSpeed, roll * turnSpeed, ForceMode.Acceleration);
			fuel -= (Mathf.Abs(pitch) + Mathf.Abs(yaw) + Mathf.Abs(roll)) * turnSpeed * Time.deltaTime;
			fuel -= acceleration * Time.deltaTime;
		}
		else
		{
			fuel = 0f;
			acceleration = 0f;
		}

		MainUI.SetFuel(fuel);
	}

	void OnCollisionEnter(Collision collision)
	{
		health -= collision.relativeVelocity.sqrMagnitude;

		if (health <= 0f)
		{
			Camera.main.transform.parent = null;
			ParticleSystemMultiplier instance = Instantiate(explosionPrefab) as ParticleSystemMultiplier;
			instance.multiplier = 2f;
			instance.transform.position = transform.position;
			Destroy(gameObject);

			MainUI.SetHealth(0);
			MainUI.ShowGameOver();
		}
		else
		{
			MainUI.SetHealth(health);
		}
	}
}

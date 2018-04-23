#if !UNITY_5 || UNITY_5_0 || UNITY_5_1 || UNITY_5_2
#define COHERENT_UNITY_PRE_5_3
#endif

using UnityEngine;
using System.Collections;

public class Truster : MonoBehaviour
{
	public float minEmitionRate = 0f;
	public float maxEmitionRate = 100f;
	public float minStartSpeed = 0f;
	public float maxStartSpeed = 10f;
	public float minLightIntensity = 0f;
	public float maxLightIntensity = 1f;
	ParticleSystem _particleSystem;
	Light pointLight;

	void Awake()
	{
		_particleSystem = GetComponent<ParticleSystem>();
		#if COHERENT_UNITY_PRE_5_3
		_particleSystem.emissionRate = 0f;
		#else
		ParticleSystem.EmissionModule em = _particleSystem.emission;
		em.rate = new ParticleSystem.MinMaxCurve(0f);
		#endif

		_particleSystem.startSpeed = 0f;
		pointLight = GetComponent<Light>();
	}

	public void SetPower(float power)
	{
		#if COHERENT_UNITY_PRE_5_3
		_particleSystem.emissionRate = Mathf.Lerp(minEmitionRate, maxEmitionRate, power);
		#else
		ParticleSystem.EmissionModule em = _particleSystem.emission;
		em.rate = new ParticleSystem.MinMaxCurve(Mathf.Lerp(minEmitionRate, maxEmitionRate, power));
		#endif

		_particleSystem.startSpeed = Mathf.Lerp(minStartSpeed, maxStartSpeed, power);
		pointLight.intensity = Mathf.Lerp(minLightIntensity, maxLightIntensity, power);
	}
}

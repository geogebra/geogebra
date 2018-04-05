#if !UNITY_5 || UNITY_5_0 || UNITY_5_1 || UNITY_5_2
#define COHERENT_UNITY_PRE_5_3
#endif

using System;
using System.Collections;
using UnityEngine;
using Random = UnityEngine.Random;

public class ParticleSystemDestroyer : MonoBehaviour
{
    // allows a particle system to exist for a specified duration,
    // then shuts off emission, and waits for all particles to expire
    // before destroying the gameObject

    public float minDuration = 8;
    public float maxDuration = 10;

    private float m_MaxLifetime;
    private bool m_EarlyStop;


    private IEnumerator Start()
    {
        var systems = GetComponentsInChildren<ParticleSystem>();

        // find out the maximum lifetime of any particles in this effect
        foreach (var system in systems)
        {
            m_MaxLifetime = Mathf.Max(system.startLifetime, m_MaxLifetime);
        }

        // wait for random duration

        float stopTime = Time.time + Random.Range(minDuration, maxDuration);

        while (Time.time < stopTime || m_EarlyStop)
        {
            yield return null;
        }

        // turn off emission
        foreach (var system in systems)
        {
			#if COHERENT_UNITY_PRE_5_3
			system.enableEmission = false;
			#else
			ParticleSystem.EmissionModule em = system.emission;
			em.enabled = false;
			#endif
        }
        BroadcastMessage("Extinguish", SendMessageOptions.DontRequireReceiver);

        // wait for any remaining particles to expire
        yield return new WaitForSeconds(m_MaxLifetime);

        Destroy(gameObject);
    }


    public void Stop()
    {
        // stops the particle system early
        m_EarlyStop = true;
    }
}

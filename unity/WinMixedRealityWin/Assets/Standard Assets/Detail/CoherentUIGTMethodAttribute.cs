using System;
using System.Reflection;
using System.Linq;
using System.Linq.Expressions;
using System.Collections.Generic;
using UnityEngine;

namespace Coherent.UIGT
{
	[AttributeUsage(AttributeTargets.Method)]
	public class CoherentUIGTMethodAttribute : Attribute
	{
		public string ScriptEventName { get; set; }
		public bool IsEvent { get; set; }

		public CoherentUIGTMethodAttribute(string scriptEventName)
			: this(scriptEventName, false)
		{
		}

		public CoherentUIGTMethodAttribute(string scriptEventName, bool isEvent)
		{
			ScriptEventName = scriptEventName;
			IsEvent = isEvent;
		}
	}

	public struct CoherentUIGTMethodBindingInfo
	{
		public string ScriptEventName { get; set; }
		public MethodInfo Method { get; set; }
		public Delegate BoundFunction { get; set; }
		public bool IsEvent { get; set; }
	}

	public static class CoherentUIGTMethodHelper
	{
		static Dictionary<Type, List<CoherentUIGTMethodBindingInfo>> s_CoherentMethodsCache;

		static CoherentUIGTMethodHelper()
		{
			s_CoherentMethodsCache = new Dictionary<Type, List<CoherentUIGTMethodBindingInfo>>();
		}

		private static CoherentUIGTMethodBindingInfo BindMethod(CoherentUIGTMethodBindingInfo method, Component component)
		{
			return (method.Method != null)
				? new CoherentUIGTMethodBindingInfo() {
						ScriptEventName = method.ScriptEventName,
						BoundFunction = ToDelegate(method.Method, component),
						IsEvent = method.IsEvent
					}
				: method;
		}


		private static List<CoherentUIGTMethodBindingInfo> BindMethods(List<CoherentUIGTMethodBindingInfo> methods, Component component)
		{
			return methods.Select((x) => BindMethod(x, component)).ToList();
		}

		private static List<CoherentUIGTMethodBindingInfo> GetCoherentMethodsInComponent(Component component)
		{
			List<CoherentUIGTMethodBindingInfo> coherentMethods = new List<CoherentUIGTMethodBindingInfo>();

			Type type = component.GetType();

			List<CoherentUIGTMethodBindingInfo> cachedCoherentMethods;
			if (s_CoherentMethodsCache.TryGetValue(type, out cachedCoherentMethods))
			{
				return BindMethods(cachedCoherentMethods, component);
			}

			// Iterate methods of each type
			BindingFlags bindingFlags = BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Static | BindingFlags.Instance;
			foreach (MethodInfo methodInfo in type.GetMethods(bindingFlags))
			{
				// Iterate custom attributes
				var attributes = methodInfo.GetCustomAttributes(typeof(CoherentUIGTMethodAttribute), true);
				foreach (object customAttribute in attributes)
				{
					CoherentUIGTMethodAttribute coherentMethodAttribute = (customAttribute as CoherentUIGTMethodAttribute);

					if (methodInfo.IsStatic)
					{
						coherentMethods.Add(new CoherentUIGTMethodBindingInfo(){
							ScriptEventName = coherentMethodAttribute.ScriptEventName,
							BoundFunction = ToDelegate(methodInfo, null),
							IsEvent = coherentMethodAttribute.IsEvent
						});
					}
					else
					{
						coherentMethods.Add(new CoherentUIGTMethodBindingInfo(){
							ScriptEventName = coherentMethodAttribute.ScriptEventName,
							Method = methodInfo,
							IsEvent = coherentMethodAttribute.IsEvent
						});
					}
				}
			}

			s_CoherentMethodsCache.Add(type, coherentMethods);

			return BindMethods(coherentMethods, component);
		}

		public static List<CoherentUIGTMethodBindingInfo> GetCoherentMethodsInGameObject(GameObject gameObject)
		{
			List<CoherentUIGTMethodBindingInfo> coherentMethods = new List<CoherentUIGTMethodBindingInfo>();

			Component[] components = gameObject.GetComponents(typeof(MonoBehaviour));

			foreach (var item in components)
			{
				MonoBehaviour monoBehaviour = item as MonoBehaviour;
				if (monoBehaviour == null || !monoBehaviour.enabled)
				{
					continue;
				}
				coherentMethods.AddRange(GetCoherentMethodsInComponent(item));
			}

			return coherentMethods;
		}

		/// <summary>
		/// Builds a Delegate instance from the supplied MethodInfo object and a target to invoke against.
		/// </summary>
		public static Delegate ToDelegate(MethodInfo methodInfo, object target)
		{
			if (methodInfo == null) throw new ArgumentNullException("Cannot create a delegate instance from null MethodInfo!");

			Type delegateType;

			var typeArgs = methodInfo.GetParameters()
				.Select(p => p.ParameterType)
				.ToList();

			if (methodInfo.ReturnType == typeof(void))
			{
				delegateType = Expression.GetActionType(typeArgs.ToArray());
			}
			else
			{
				typeArgs.Add(methodInfo.ReturnType);
				delegateType = Expression.GetFuncType(typeArgs.ToArray());
			}

			var result = (target == null)
				? Delegate.CreateDelegate(delegateType, methodInfo)
				: Delegate.CreateDelegate(delegateType, target, methodInfo);

			return result;
		}
	}
}

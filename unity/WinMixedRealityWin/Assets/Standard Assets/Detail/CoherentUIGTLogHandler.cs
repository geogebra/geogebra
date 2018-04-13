using UnityEngine;

namespace Coherent.UIGT
{
	class UnityGTLogHandler : ILogHandler
	{
		public override void WriteLog(Severity severity, string message, uint length)
		{
			switch (severity)
			{
			case Severity.Trace:
			case Severity.Debug:
			case Severity.Info:
				Debug.Log(string.Format("[Coherent GT] ({0}) {1}", severity, message));
				break;
			case Severity.Warning:
				Debug.LogWarning(string.Format("[Coherent GT] ({0}) {1}", severity, message));
				break;
			case Severity.AssertFailure:
			case Severity.Error:
				Debug.LogError(string.Format("[Coherent GT] ({0}) {1}", severity, message));
				break;
			default:
				Debug.Log(string.Format("[Coherent GT] ({0}) {1}", severity, message));
				break;
			}
		}

		public override void Assert(string message)
		{
			// Do nothing; The log will be written by WriteLog with severity level AssertFailure
		}
	}
}

using Coherent.UIGT;

// all properties / fields for Options will be visible to Coherent GT
[CoherentType(PropertyBindingFlags.All)]
public struct GameOptionsGT
{
	public string Backend;
	public uint Width;
	public uint Height;

	public string Username
	{
		get {
		#if UNITY_STANDALONE_WIN
			return System.Security.Principal.WindowsIdentity.GetCurrent().Name;
		#else
			return "Coherent";
		#endif
		}
	}

	// rename the NetPort property to NetworkPort
	[CoherentProperty("NetworkPort")]
	public uint NetPort { get; set; }
}

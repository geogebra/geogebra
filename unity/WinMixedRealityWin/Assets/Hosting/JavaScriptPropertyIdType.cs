namespace ChakraHost.Hosting
{
    #if ENABLE_WINMD_SUPPORT
    /// <summary>
    ///     Type enumeration of a JavaScript property
    /// </summary>
    public enum JavaScriptPropertyIdType
    {
        /// <summary>
        ///     Type enumeration of a JavaScript string property
        /// </summary>
        String,
        /// <summary>
        ///     Type enumeration of a JavaScript symbol property
        /// </summary>
        Symbol
    };
#endif
}
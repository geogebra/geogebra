namespace ChakraHost.Hosting
{
    #if ENABLE_WINMD_SUPPORT
    /// <summary>
    ///     Allocation callback event type.
    /// </summary>
    public enum JavaScriptMemoryEventType
    {
        /// <summary>
        ///     Indicates a request for memory allocation.
        /// </summary>
        Allocate = 0,

        /// <summary>
        ///     Indicates a memory freeing event.
        /// </summary>
        Free = 1,

        /// <summary>
        ///     Indicates a failed allocation event.
        /// </summary>
        Failure = 2
    }
#endif
}

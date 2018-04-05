/**
 * Dispatches a custom event, passing a reworked data to the event object
 * @param {Event} event
 * @param {string} eventName
 * @param data
 */
export function dispatch(eventName: string, data?) {
    document.body.dispatchEvent(new CustomEvent(eventName, {detail: data}));
}

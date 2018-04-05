export default class Search {
    public input: Element;

    /**
     * The initialization method, which assigns the
     * input property
     * @param {Element} input
     * @returns {Search}
     */
    public init(input: Element) {
        this.input = input;

        return this;
    }

    /**
     * Attach an event listener to the input element
     * @param {string} event
     * @param {EventListenerOrEventListenerObject} fn
     * @param {Element} element
     * @returns {Search}
     */
    public listen(event: string, fn: EventListenerOrEventListenerObject, element = this.input) {
        if (!element) {
            throw new Error('Search: No input element found.');
        }

        element.addEventListener(event, fn);

        return this;
    }

    /**
     * Remove an event listener from the input element
     * @param {string} event
     * @param {EventListenerOrEventListenerObject} fn
     */
    public stopListening(event: string, fn: EventListenerOrEventListenerObject, element = this.input): Search {
        element.removeEventListener(event, fn);

        return this;
    }


    /**
     * Trigger an Event - click, input, mouseover, etr.
     * @param {string} eventName - event name, currently supports build-in events
     * @returns {Search}
     *
     * @example Search.trigger('input');
     * @example Search.trigger('click');
     */
    public trigger(eventName: string) {
        const event = new Event(eventName, {
            bubbles: true,
            cancelable: true
        });

        this.input.dispatchEvent(event);

        return this;
    }

    /**
     * Attach a mousedown event listener to an element and
     * blur the input element when event is triggered.
     *
     * If no element is passed, the input element is immediately blurred.
     * @param {HTMLElement} element
     * @returns {Search}
     */
    public focusout(element?: HTMLElement) {
        const handler = (e: Event) => {
            (this.input as HTMLElement).blur();
        };

        if (element) {
            element.removeEventListener('mousedown', handler);
            element.addEventListener('mousedown', handler);
        } else if (this.input === document.activeElement) {
            (this.input as HTMLElement).blur();
        }

        return this;
    }

    /**
     * Reset the search input value
     * @returns {Search}
     */
    public reset() {
        (this.input as HTMLInputElement).value = '';

        return this;
    }

    public getInput() {
        return (this.input as HTMLInputElement).value;
    }
}

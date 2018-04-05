interface Config {
    scroller: any;
    buffer: number;
    total: number;
    itemHeight: number;
}

export default class VirtualList {
    private config: Config;
    private element;
    private total: number;
    public vListDOM: HTMLElement[];
    private _renderAnimationFrame: number;
    private scroller: any | HTMLElement;
    private itemsToRender: number;
    private buffer: number;
    private lastRepaint: null | number;
    private lastFrom: number;
    public instantiated: boolean;

    constructor() {
        this.instantiated = false;
    }

    /**
     * Initialize the virtual list library. Populate library's properties
     * and trigger the rendering function.
     * @param element
     * @param config {Config}
     * @param {string} DOM
     * @public
     */
    public init(element, config, DOM: HTMLElement[]) {
        this.element = element;
        this.config = config;
        this.buffer = config.buffer;
        this.total = config.total;
        this.lastRepaint = null;
        this.scroller = config.scroller;
        this.itemsToRender = Math.ceil(this.scroller.height() / this.config.itemHeight) + this.buffer;
        this.vListDOM = DOM;
        this.instantiated = true;

        this.scroller.off('scroll');
        this.scroller.on('scroll', () => {
            render();
        });

        /**
         * The render function is triggered on every frame using requestAnimationFrame
         * and checks if the scrollTop value is changed. If it is not, it stops execution.
         * If it finds a difference, it triggers renderChunk.
         */
        const render = () => {
            const scrollTop = this.getScrollPosition();
            const lastRepaint = this.lastRepaint;

            if (scrollTop === lastRepaint) {
                return;
            }

            if (!lastRepaint || Math.abs(lastRepaint - scrollTop) > 0) {
                this.lastRepaint = scrollTop;
                this.renderChunk();
            }

        };

        this.calculateScroll();
        render();

        return this;
    }

    public listen(event: string, fn: EventListenerOrEventListenerObject): VirtualList {
        document.body.addEventListener(event, fn);

        return this;
    }

    public stopListening(event: string, fn: EventListenerOrEventListenerObject): VirtualList {
        document.body.removeEventListener(event, fn);

        return this;
    }

    public updateDOM(DOM: HTMLElement[]) {
        this.vListDOM = DOM;

        this.scroller.scrollTop(0);
        this.total = DOM.length;

        this.refresh();
    }

    /**
     * Calculate the scroll of the asset panel wrapper by multiplying
     * the total asset count to the asset item's height and adds
     * overflowY: scroll to the wrapper element.
     * @private
     */
    protected calculateScroll() {
        this.element.css('height', (this.total * this.config.itemHeight) + 'px');

        if (this.scroller.height() < this.element.height()) {
            this.scroller.css('overflowY', 'scroll');
        }
    }

    /**
     * Remove an asset from the vListDOM array and change all
     * vListDOM items indexes.
     * Used in runtimeEditor._attachLibraryClickEvent();
     * @param {string} index - the specific index of the element, than needs to be removed.
     * @public
     */
    public removeAsset(index: string): void {
        if (!this.instantiated) {
            throw new Error('Cannot remove asset. The Virtual Library is not instantiated.');
        }

        this.vListDOM.splice(Number(index), 1);
        this.total -= 1;

        this.vListDOM.forEach((node, i) => {
            node.style.top = (i * 34) + 'px';
            node.setAttribute('vList-index', String(i));
        });

        this.refresh();
    }

    /**
     * Recalculate the height of the asset panel
     * and render a chunk of assets
     * @public
     */
    public refresh(): void {
        if (!this.instantiated) {
            throw new Error('Cannot rerender the library. The Virtual Library is not instantiated.');
        }

        this.calculateScroll();
        this.renderChunk();
    }

    public destoy(): void {
        window.cancelAnimationFrame(this._renderAnimationFrame);
    }

    /**
     * Checks if the virtual library has been instantiated.
     * Used in editor.resizeTimelineVertical
     * @returns {boolean}
     */
    public isInstantiated(): boolean {
        return this.instantiated;
    }

    /**
     * Get the scroll position of the list based on config or current scrollTop value
     * of the element
     * @returns {function | number}
     * @private
     */
    private getScrollPosition(): number {
        return this.scroller.scrollTop();
    }

    /**
     * Get a part of the whole asset panel, called chunk,
     * and append it to the asset panel wrapper
     * @private
     */
    private renderChunk(): void | boolean {
        const scrollTop = this.getScrollPosition();
        this.itemsToRender = Math.ceil(this.scroller.height() / this.config.itemHeight) + (this.buffer * 2);

        const estFrom = scrollTop / 34 < 0 ? 0 : Math.ceil(scrollTop / 34);
        const from = estFrom - this.buffer <= 0 ? 0 : estFrom - this.buffer;

        this.lastFrom = from;

        const to = from + this.itemsToRender;
        let itemsRendered = this.vListDOM.slice(from, to);

        this.element.html('');
        this.element.append(itemsRendered);
    }

    /**
     * Search the vListDOM array for an element with a specific name and
     * return it's vlist-index.
     * @param {string} name
     * @returns {string}
     */
    public findIndex(name: string): string | null {
        const element = this.vListDOM
            .find(node => {
                const dataLink = node.querySelector('[data-link]');
                return dataLink ? dataLink.getAttribute('data-link') === name : false;
            });

        return element ? element.getAttribute('vlist-index') : null;
    }
}

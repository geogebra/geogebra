describe('Construction-preserving touch navigation', () => {
    const sendTouch = (type, points) => cy.then(() => {
        const frame = Cypress.$('iframe.aut-iframe', window.top.document)[0];
        const bounds = frame.getBoundingClientRect();
        const scale = bounds.width / frame.offsetWidth;
        return Cypress.automation('remote:debugger:protocol', {
            command: 'Input.dispatchTouchEvent',
            params: {type, touchPoints: points.map(([id, x, y]) => ({
                id, x: bounds.left + x * scale, y: bounds.top + y * scale
            }))}
        });
    });

    beforeEach(() => {
        cy.visit('geometry.html?prerelease=true');
        cy.window().should(win => expect(win.ggbApplet?.setMode).to.be.a('function'));
        cy.window().then(win => {
            win.ggbApplet.setMode(1);
            win.ggbApplet.reset();
            win.ggbApplet.setMode(1);
        });
    });

    it('captures real touch pointers and preserves a pending segment through a pinch', () => {
        cy.window().then(win => {
            const api = win.ggbApplet;
            const view = JSON.parse(api.getViewProperties(1));
            const x = view.left + 150;
            const y = view.top + 150;
            api.setMode(15);
            sendTouch('touchStart', [[1, x, y]]);
            sendTouch('touchEnd', []);
            cy.then(() => expect(api.getObjectNumber()).to.equal(1));
            const scale = view.invXscale;
            sendTouch('touchStart', [[1, x, y]]);
            sendTouch('touchStart', [[1, x, y], [2, x + 150, y]]);
            sendTouch('touchMove', [[1, x, y], [2, x + 250, y]]);
            sendTouch('touchEnd', []);
            cy.then(() => {
                expect(JSON.parse(api.getViewProperties(1)).invXscale).to.be.lessThan(scale);
                expect(api.getObjectNumber()).to.equal(1);
                expect(api.getMode()).to.equal(15);
            });
            sendTouch('touchStart', [[3, x + 100, y + 100]]);
            sendTouch('touchEnd', []);
            cy.then(() => expect(api.getObjectNumber()).to.equal(3));
        });
    });

    it('discards a cancelled point and accepts the next contact', () => {
        cy.window().then(win => {
            const api = win.ggbApplet;
            const view = JSON.parse(api.getViewProperties(1));
            const point = [[1, view.left + 150, view.top + 150]];
            sendTouch('touchStart', point);
            sendTouch('touchCancel', []);
            cy.then(() => expect(api.getObjectNumber()).to.equal(0));
            sendTouch('touchStart', point);
            sendTouch('touchEnd', []);
            cy.then(() => expect(api.getObjectNumber()).to.equal(1));
        });
    });

    it('keeps navigating when a captured finger moves over the Tools panel', () => {
        cy.window().then(win => {
            const api = win.ggbApplet;
            const view = JSON.parse(api.getViewProperties(1));
            const x = view.left + 150;
            const y = view.top + 150;
            sendTouch('touchStart', [[1, x, y]]);
            sendTouch('touchStart', [[1, x, y], [2, x + 150, y]]);
            sendTouch('touchMove', [[1, view.left - 100, y], [2, x + 150, y]]);
            sendTouch('touchEnd', []);
            cy.then(() => {
                expect(JSON.parse(api.getViewProperties(1)).invXscale).to.be.lessThan(view.invXscale);
                expect(api.getObjectNumber()).to.equal(0);
            });
        });
    });

    it('still creates points with the existing mouse lifecycle', () => {
        cy.get('[data-test=euclidianView]')
            .mouseEvent('down', 150, 150)
            .mouseEvent('up');
        cy.window().then(win => expect(win.ggbApplet.getObjectNumber()).to.equal(1));
    });

});

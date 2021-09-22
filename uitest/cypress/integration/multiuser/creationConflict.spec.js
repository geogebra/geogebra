describe('Multiuser object creation conflict tests', () => {
	beforeEach(() => {
		cy.visit('GeoGebraLiveTest.html');
	});

	const conicCommandA = "conicElement = Circle[(0, 0), 1]";
	const conicCommandB = "conicElement = Ellipse[(1, 2), (1, -2), 2.5]";

	const conicA = `<element type=\"conic\" label=\"conicElement\">
					<show object=\"true\" label=\"false\"/>
					<objColor r=\"0\" g=\"0\" b=\"0\" alpha=\"0\"/>
					<layer val=\"0\"/>
					<ordering val=\"0\"/>
					<labelMode val=\"0\"/>
					<lineStyle thickness=\"5\" type=\"0\" typeHidden=\"1\" opacity=\"178\"/>
					<eigenvectors x0=\"1\" y0=\"0\" z0=\"1.0\" x1=\"0\" y1=\"1\" z1=\"1.0\"/>
					<matrix A0=\"1\" A1=\"1\" A2=\"-1\" A3=\"0\" A4=\"0\" A5=\"0\"/>
					<eqnStyle style="specific"/>
				</element>`;

	const conicB = `<element type=\"conic\" label=\"conicElement\">
					<show object=\"true\" label=\"false\"/>
					<objColor r=\"0\" g=\"0\" b=\"0\" alpha=\"0\"/>
					<layer val=\"0\"/>
					<ordering val=\"0\"/>
					<labelMode val=\"0\"/>
					<lineStyle thickness=\"5\" type=\"0\" typeHidden=\"1\" opacity=\"178\"/>
					<eigenvectors x0=\"0\" y0=\"1\" z0=\"1.0\" x1=\"-1\" y1=\"0\" z1=\"1.0\"/>
					<matrix A0=\"100\" A1=\"36\" A2=\"-125\" A3=\"0\" A4=\"-100\" A5=\"0\"/>
					<eqnStyle style="implicit"/>
				</element>`;

	const cleanup = (str) => {
		return str.replace(/\n/g, '').replace(/\t/g, '');
	}

	const testConicConflict = (firstUser, secondUser) => {
		cy.window().then((win) => {
			return new Promise((resolve, _) => {
				win.onAppletsLoaded = () => {
					win.apis[firstUser].evalCommand(conicCommandA);
					win.apis[firstUser].updateConstruction();
					win.apis[secondUser].evalCommand(conicCommandB);
					win.apis[secondUser].updateConstruction();
					resolve(win);
				}
			});
		}).then((win) => {
			// wait until synchronization happens
			cy.wait(5000);
			cy.wrap(win);
		}).then((win) => {
			[0, 1, 2].forEach((user) => {
				expect(win.apis[user].getAllObjectNames()).to.have.members(['conicElement', 'conicElement_1']);
				expect(cleanup(win.apis[user].getXML('conicElement')))
					.to.equal(cleanup(conicB.replace('<ordering val="0"/>', '<ordering val="1"/>')));
				expect(cleanup(win.apis[user].getXML('conicElement_1')))
					.to.equal(cleanup(conicA.replace(/conicElement/g, 'conicElement_1')));
			})
		});
	}

	it("Test double conic conflict [0-1]", () => {
		testConicConflict(0, 1);
	});

	it("Test double conic conflict [0-2]", () => {
		testConicConflict(0, 2);
	});

	it("Test double conic conflict [1-2]", () => {
		testConicConflict(1, 2);
	});
})

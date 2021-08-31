describe('Multiuser object creation conflict tests', () => {
	beforeEach(() => {
		cy.visit('GeoGebraLiveTest.html');
	});

	const strokeCommandA = 'stroke1 := PolyLine[(-0.90000,2.3000), (-0.98000,2.3000), (-1.0600,2.2200), (-1.1200,2.1400), (-1.1600,2.0800), (-1.1600,1.9600), (-1.1600,1.8400), (-1.1400,1.7800), (-1.0800,1.7200), (-1.0000,1.6600), (-0.90000,1.6400), (-0.80000,1.6400), (-0.72000,1.6400), (-0.64000,1.6800), (-0.58000,1.7800), (-0.54000,1.8600), (-0.52000,1.9200), (-0.52000,2.0000), (-0.52000,2.1000), (-0.60000,2.1600), (-0.68000,2.2000), (-0.74000,2.2200), (-0.82000,2.2400), (-0.88000,2.2200), (-0.94000,2.2000), (-0.98000,2.1400), (NaN,NaN), true]';
	const strokeCommandB = 'stroke1 := PolyLine[(-0.20000,2.2800), (-0.18000,2.2000), (-0.18000,2.1200), (-0.18000,2.0400), (-0.16000,1.9800), (-0.14000,1.9000), (-0.14000,1.8200), (-0.14000,1.7400), (NaN,NaN), (-0.12000,1.9400), (-0.060000,1.9600), (0.020000,1.9800), (0.080000,2.0000), (0.14000,2.0400), (0.24000,2.0400), (0.30000,2.0800), (0.36000,2.1000), (0.42000,2.1200), (NaN,NaN), (-0.10000,1.8800), (-0.040000,1.8600), (0.020000,1.8200), (0.080000,1.8000), (0.16000,1.7600), (0.24000,1.7000), (0.34000,1.6400), (NaN,NaN), true]'

	const strokeA = `
				<expression label="stroke1" exp="PolyLine[(-0.90000,2.3000), (-0.98000,2.3000), (-1.0600,2.2200), (-1.1200,2.1400), (-1.1600,2.0800), (-1.1600,1.9600), (-1.1600,1.8400), (-1.1400,1.7800), (-1.0800,1.7200), (-1.0000,1.6600), (-0.90000,1.6400), (-0.80000,1.6400), (-0.72000,1.6400), (-0.64000,1.6800), (-0.58000,1.7800), (-0.54000,1.8600), (-0.52000,1.9200), (-0.52000,2.0000), (-0.52000,2.1000), (-0.60000,2.1600), (-0.68000,2.2000), (-0.74000,2.2200), (-0.82000,2.2400), (-0.88000,2.2200), (-0.94000,2.2000), (-0.98000,2.1400), (NaN,NaN), true]" />
				<element type="penstroke" label="stroke1">
					<show object="true" label="false" ev="8"/>
					<objColor r="0" g="0" b="0" alpha="0"/>
					<layer val="0"/>
					<ordering val="0"/>
					<labelMode val="0"/>
					<auxiliary val="false"/>
					<lineStyle thickness="5" type="0" typeHidden="1" opacity="178"/>
				</element>
			`;
	const strokeB = `
				<expression label="stroke1" exp="PolyLine[(-0.20000,2.2800), (-0.18000,2.2000), (-0.18000,2.1200), (-0.18000,2.0400), (-0.16000,1.9800), (-0.14000,1.9000), (-0.14000,1.8200), (-0.14000,1.7400), (NaN,NaN), (-0.12000,1.9400), (-0.060000,1.9600), (0.020000,1.9800), (0.080000,2.0000), (0.14000,2.0400), (0.24000,2.0400), (0.30000,2.0800), (0.36000,2.1000), (0.42000,2.1200), (NaN,NaN), (-0.10000,1.8800), (-0.040000,1.8600), (0.020000,1.8200), (0.080000,1.8000), (0.16000,1.7600), (0.24000,1.7000), (0.34000,1.6400), (NaN,NaN), true]" />
				<element type="penstroke" label="stroke1">
					<show object="true" label="false" ev="8"/>
					<objColor r="0" g="0" b="0" alpha="0"/>
					<layer val="0"/>
					<ordering val="0"/>
					<labelMode val="0"/>
					<auxiliary val="false"/>
					<lineStyle thickness="5" type="0" typeHidden="1" opacity="178"/>
				</element>
			`;

	const cleanup = (str) => {
		return str.replace(/\n/g, '').replace(/\t/g, '');
	}

	const testStrokeConflict = (firstUser, secondUser) => {
		cy.window().then((win) => {
			win.onAppletsLoaded = () => {
				win.apis[firstUser].evalCommand(strokeCommandA);
				win.apis[firstUser].updateConstruction();
				win.apis[secondUser].evalCommand(strokeCommandB);
				win.apis[secondUser].updateConstruction();
			}
		});

		cy.get(".jsloaded")

		cy.wait(7000);

		cy.window().then((win) => {
			[0, 1, 2].forEach((user) => {
				expect(win.apis[user].getAllObjectNames()).to.have.members(['stroke1', 'stroke1_1']);
				expect(cleanup(win.apis[user].getAlgorithmXML('stroke1')))
					.to.equal(cleanup(strokeB.replace('<ordering val="0"/>', '<ordering val="1"/>')));
				expect(cleanup(win.apis[user].getAlgorithmXML('stroke1_1')))
					.to.equal(cleanup(strokeA.replace(/stroke1/g, 'stroke1_1')));
			})
		});
	}

	it("Test double stroke conflict [0-1]", () => {
		testStrokeConflict(0, 1);
	});

	it("Test double stroke conflict [0-2]", () => {
		testStrokeConflict(0, 2);
	});

	it("Test double stroke conflict [1-2]", () => {
		testStrokeConflict(1, 2);
	});
})

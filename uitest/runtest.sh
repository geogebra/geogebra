npm install --force || exit 1
npx http-server -p 8888 content & npx wait-on http://localhost:8888
npx cypress run --env userAgent="Chrome iPad" --spec="cypress/integration/**/*-iPad.spec.js" || true
npx cypress run || true
npx lighthouse http://localhost:8888/editor.html --output json --output-path=./lighthouse.report.json --chrome-flags="--headless --no-sandbox"

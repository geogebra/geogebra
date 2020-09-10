npm install || exit 1
npx http-server -p 8888 content & npx wait-on http://localhost:8888
npx cypress run --env userAgent="Chrome iPad" --spec="cypress/integration/**/*-iPad.spec.js" || true
npx cypress run || true

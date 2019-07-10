pipeline {
    agent any
    triggers { 
        cron('H 10,19 * * 1-5') 
    }
    stages {
        stage('build') {
            steps {
                sh label: 'test', script: './gradlew :common-jre:test :desktop:test :common-jre:jacocoTestReport :web:test'
                sh label: 'static analysis', script: './gradlew checkPmd :editor-base:spotbugsMain :web:spotbugsMain :desktop:spotbugsMain :ggbjdk:spotbugsMain :common-jre:spotbugsMain --max-workers=1'
                sh label: 'code style', script: './gradlew :web:cpdCheck checkAllStyles'
            }
        }
        stage('reports') {
            steps {
                junit '**/build/test-results/test/*.xml'
                recordIssues tools: [
                 cpd(pattern: '**/build/reports/cpd/cpdCheck.xml'), 
                    checkStyle(pattern: '**/build/reports/checkstyle/*.xml')
                ]
                recordIssues qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]], tools: [
                    spotBugs(pattern: '**/build/reports/spotbugs/*.xml', useRankAsPriority: true), 
                    pmdParser(pattern: '**/build/reports/pmd/main.xml')
                ]
            }
        }
    }
}

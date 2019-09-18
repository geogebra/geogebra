pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                sh label: 'clean', script: './gradlew clean'
                sh label: 'build web', script: './gradlew :web:compileGwt :web:symlinkIntoWar -Pgdraft=true'
            }
        }
        stage('archive') {
            steps {
                archiveArtifacts 'web/war/web3d/**, web/war/webSimple/**, web/war/*.html, web/war/*.json'
            }
        }
    }
    post {
        always { 
           cleanWs() 
        }
        failure {
            slackSend(color: 'danger', tokenCredentialId: 'slack.token', username: 'jenkins',
                message:  "${env.JOB_NAME} [${env.BUILD_NUMBER}]: Build failed. (<${env.BUILD_URL}|Open>)")
        }
        unstable {
            slackSend(color: 'warning', tokenCredentialId: 'slack.token', username: 'jenkins',
                message:  "${env.JOB_NAME} [${env.BUILD_NUMBER}]: Unstable. (<${env.BUILD_URL}|Open>)")
        }
        fixed {
            slackSend(color: 'good', tokenCredentialId: 'slack.token', username: 'jenkins',
                message:  "${env.JOB_NAME} [${env.BUILD_NUMBER}]: Back to normal. (<${env.BUILD_URL}|Open>)")
        }
    }
}

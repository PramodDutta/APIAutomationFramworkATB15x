pipeline {
    agent any

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['e2e', 'sample', 'all', 'individual'],
            description: 'Select the test suite to run'
        )
        string(
            name: 'SPECIFIC_TEST',
            defaultValue: '',
            description: 'Specific test class to run (e.g., com.thetestingacademy.tests.individual.TestCreateBooking). Only used when TEST_SUITE is "individual"'
        )
        booleanParam(
            name: 'GENERATE_ALLURE',
            defaultValue: true,
            description: 'Generate Allure Report'
        )
    }

    environment {
        JAVA_HOME = tool name: 'JDK23', type: 'jdk'
        MAVEN_HOME = tool name: 'Maven3', type: 'maven'
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${env.PATH}"
    }

    tools {
        jdk 'JDK23'
        maven 'Maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '=== Checking out source code ==='
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '=== Building the project ==='
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('Run E2E Tests') {
            when {
                expression { params.TEST_SUITE == 'e2e' }
            }
            steps {
                echo '=== Running E2E Integration Tests ==='
                sh 'mvn test -Dsurefire.suiteXmlFiles=testng-e2e.xml'
            }
        }

        stage('Run Sample Tests') {
            when {
                expression { params.TEST_SUITE == 'sample' }
            }
            steps {
                echo '=== Running Sample Tests ==='
                sh 'mvn test -Dsurefire.suiteXmlFiles=testng-sample.xml'
            }
        }

        stage('Run All Tests') {
            when {
                expression { params.TEST_SUITE == 'all' }
            }
            steps {
                echo '=== Running All Tests ==='
                sh 'mvn test'
            }
        }

        stage('Run Individual Test') {
            when {
                expression { params.TEST_SUITE == 'individual' && params.SPECIFIC_TEST != '' }
            }
            steps {
                echo "=== Running Individual Test: ${params.SPECIFIC_TEST} ==="
                sh "mvn test -Dtest=${params.SPECIFIC_TEST}"
            }
        }

        stage('Generate Allure Report') {
            when {
                expression { params.GENERATE_ALLURE == true }
            }
            steps {
                echo '=== Generating Allure Report ==='
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'allure-results']]
                ])
            }
        }
    }

    post {
        always {
            echo '=== Archiving Test Results ==='
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            echo '=== Archiving Logs ==='
            archiveArtifacts artifacts: 'logs/*.log', allowEmptyArchive: true
        }

        success {
            echo '=== Pipeline completed successfully ==='
            slackSend(
                channel: '#api-automation',
                color: 'good',
                message: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
            )
        }

        failure {
            echo '=== Pipeline failed ==='
            slackSend(
                channel: '#api-automation',
                color: 'danger',
                message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})"
            )
        }

        cleanup {
            cleanWs()
        }
    }
}

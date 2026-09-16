pipeline {
    agent any

    environment {
        IMAGE_NAME = 'task-manager'
        IMAGE_TAG  = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code from GitHub...'
                checkout scm
            }
        }

        stage('Maven Build & Test') {
            steps {
                echo 'Compiling and running Maven tests...'
                sh 'mvn clean test package'
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker image: ${IMAGE_NAME}:${IMAGE_TAG}..."
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG}  ."
            }
        }
    }

    post {
        
        success {
            echo 'Jenkins pipeline completed successfully!'
        }
        failure {
            echo 'Jenkins pipeline failed.'
        }
    }
}

pipeline {
    agent any

    environment {
        AWS_REGION   = 'ap-southeast-2'
        ECR_REGISTRY = '278061313986.dkr.ecr.ap-southeast-2.amazonaws.com'
        IMAGE_NAME   = 'taskmanagement'
        IMAGE_TAG    = "${env.BUILD_NUMBER}"
        FULL_IMAGE   = "${ECR_REGISTRY}/${IMAGE_NAME}"
        APP_INSTANCE_ID = "i-0556e346dce404efb"
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
                echo "Building Docker image: ${FULL_IMAGE}:${IMAGE_TAG}..."
                sh "docker build -t ${FULL_IMAGE}:${IMAGE_TAG} -t ${FULL_IMAGE}:latest ."
            }
        }

        stage('ECR Login & Push') {
            steps {
                echo 'Logging into Amazon ECR...'
                sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"
                
                echo 'Pushing Docker image to Amazon ECR...'
                sh "docker push ${FULL_IMAGE}:${IMAGE_TAG}"
                sh "docker push ${FULL_IMAGE}:latest"
            }
        }
        stage('SSM Test') {
        steps {
            echo 'Testing AWS Systems Manager connection to Application EC2...'

            sh """
                aws ssm send-command \
                --region ${AWS_REGION} \
                --instance-ids ${APP_INSTANCE_ID} \
                --document-name "AWS-RunShellScript" \
                --parameters 'commands=["echo Jenkins SSM connection successful"]'
            """
             }
        }
}

    post {
        always {
            echo 'Cleaning up local Docker images and workspace to save space...'
            sh "docker rmi ${FULL_IMAGE}:${IMAGE_TAG} ${FULL_IMAGE}:latest || true"
            sh "docker image prune -f || true"
            cleanWs()
        }
        success {
            echo 'Jenkins pipeline completed successfully!'
        }
        failure {
            echo 'Jenkins pipeline failed.'
        }
    }
}

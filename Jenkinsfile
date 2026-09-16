pipeline {
    agent any

    environment {
        AWS_REGION      = 'ap-southeast-2'
        ECR_REGISTRY    = '278061313986.dkr.ecr.ap-southeast-2.amazonaws.com'
        IMAGE_NAME      = 'taskmanagement'
        IMAGE_TAG       = "${env.BUILD_NUMBER}"
        FULL_IMAGE      = "${ECR_REGISTRY}/${IMAGE_NAME}"
        APP_INSTANCE_ID = 'i-0556e346dce404efb'
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

        stage('Deploy to Application EC2 via SSM') {
    steps {
        echo "Deploying ${FULL_IMAGE}:${IMAGE_TAG} to Application EC2 (${APP_INSTANCE_ID}) via AWS SSM..."

        sh """
            COMMAND_ID=\$(aws ssm send-command \
                --region ${AWS_REGION} \
                --instance-ids ${APP_INSTANCE_ID} \
                --document-name "AWS-RunShellScript" \
                --comment "Deploying ${FULL_IMAGE}:${IMAGE_TAG}" \
                --parameters 'commands=[
                    "set -e",
                    "echo Logging into Amazon ECR on App EC2...",
                    "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}",
                    "echo Pulling Docker image ${FULL_IMAGE}:${IMAGE_TAG}...",
                    "docker pull ${FULL_IMAGE}:${IMAGE_TAG}",
                    "echo Stopping and removing existing taskmanagement container...",
                    "docker stop taskmanagement || true",
                    "docker rm taskmanagement || true",
                    "echo Starting new taskmanagement container...",
                    "docker run -d --name taskmanagement --restart unless-stopped --network management-task-network -p 8080:8080 -e DB_HOST=my-postgres -e DB_PORT=5432 -e DB_NAME=taskdb -e DB_USERNAME=postgres -e DB_PASSWORD=root ${FULL_IMAGE}:${IMAGE_TAG}",
                    "echo Waiting for application to initialize...",
                    "sleep 10",
                    "echo Performing health check...",
                    "curl -f http://localhost:8080/actuator/health",
                    "echo Deployment successfully completed!"
                ]' \
                --query "Command.CommandId" \
                --output text)

            echo "SSM Command sent. Command ID: \${COMMAND_ID}"
            echo "Waiting for remote execution on EC2 instance ${APP_INSTANCE_ID}..."

            aws ssm wait command-executed \
                --region ${AWS_REGION} \
                --command-id "\${COMMAND_ID}" \
                --instance-id ${APP_INSTANCE_ID} || true

            echo "--- SSM Execution Output ---"

            aws ssm get-command-invocation \
                --region ${AWS_REGION} \
                --command-id "\${COMMAND_ID}" \
                --instance-id ${APP_INSTANCE_ID} \
                --query "[Status, StandardOutputContent, StandardErrorContent]" \
                --output text
        """
    }
}
    }
}

    post {
        always {
            echo 'Cleaning up local Docker images and workspace on Jenkins server...'
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

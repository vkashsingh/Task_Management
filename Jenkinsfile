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
        echo "Deploying ${FULL_IMAGE}:${IMAGE_TAG} to Application EC2..."

        sh """
            # Send deployment commands to Application EC2

            COMMAND_ID=\\$(aws ssm send-command \\
                --region ${AWS_REGION} \\
                --instance-ids ${APP_INSTANCE_ID} \\
                --document-name "AWS-RunShellScript" \\
                --comment "Deploy ${FULL_IMAGE}:${IMAGE_TAG}" \\
                --parameters 'commands=[

                    # Login to ECR
                    "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}",

                    # Pull new application image
                    "docker pull ${FULL_IMAGE}:${IMAGE_TAG}",

                    # Stop and remove old container
                    "docker stop taskmanagement || true",
                    "docker rm taskmanagement || true",

                    # Start new application container
                    "docker run -d --name taskmanagement --restart unless-stopped --network management-task-network -p 8080:8080 -e DB_HOST=my-postgres -e DB_PORT=5432 -e DB_NAME=taskdb -e DB_USERNAME=postgres -e DB_PASSWORD=root ${FULL_IMAGE}:${IMAGE_TAG}",

                    # Wait for application startup
                    "sleep 10",

                    # Verify application health
                    "curl -f http://localhost:8080/actuator/health",

                    # Remove stopped containers
                    "docker container prune -f"

                ]' \\
                --query "Command.CommandId" \\
                --output text)

            echo "SSM Command ID: \\$COMMAND_ID"

            # Wait for deployment to finish
            aws ssm wait command-executed \\
                --region ${AWS_REGION} \\
                --command-id "\\$COMMAND_ID" \\
                --instance-id ${APP_INSTANCE_ID}

            # Show deployment result
            aws ssm get-command-invocation \\
                --region ${AWS_REGION} \\
                --command-id "\\$COMMAND_ID" \\
                --instance-id ${APP_INSTANCE_ID} \\
                --query "[Status, StandardOutputContent, StandardErrorContent]" \\
                --output text
        """
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
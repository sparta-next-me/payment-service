pipeline {
    agent any

    environment {
        APP_NAME        = "payment-service"
        NAMESPACE       = "next-me"
        REGISTRY        = "ghcr.io"
        GH_OWNER        = "sparta-next-me"
        IMAGE_REPO      = "payment-service"
        FULL_IMAGE      = "${REGISTRY}/${GH_OWNER}/${IMAGE_REPO}:latest"
        TZ              = "Asia/Seoul"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                // payment-env 크레덴셜 사용
                withCredentials([file(credentialsId: 'payment-env', variable: 'ENV_FILE')]) {
                    sh '''
                      set -a
                      . "$ENV_FILE"
                      set +a
                      ./gradlew clean bootJar --no-daemon
                    '''
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'ghcr-credential', usernameVariable: 'USER', passwordVariable: 'TOKEN')]) {
                    sh """
                      docker build -t ${FULL_IMAGE} .
                      echo "${TOKEN}" | docker login ${REGISTRY} -u "${USER}" --password-stdin
                      docker push ${FULL_IMAGE}
                    """
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([
                    file(credentialsId: 'k3s-kubeconfig', variable: 'KUBECONFIG_FILE'),
                    file(credentialsId: 'payment-env', variable: 'ENV_FILE')
                ]) {
                    sh '''
                      export KUBECONFIG=${KUBECONFIG_FILE}

                      echo "Updating K8s Secret: payment-env..."
                      kubectl delete secret payment-env -n ${NAMESPACE} --ignore-not-found
                      kubectl create secret generic payment-env --from-env-file=${ENV_FILE} -n ${NAMESPACE}

                      echo "Applying manifests from payment-service.yaml..."
                      kubectl apply -f payment-service.yaml -n ${NAMESPACE}

                      echo "Monitoring rollout status..."
                      kubectl rollout status deployment/payment-service -n ${NAMESPACE}
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning up Docker resources..."
            // 1. 방금 빌드한 이미지 삭제
            sh "docker rmi ${FULL_IMAGE} || true"
            // 2. [중요] 사용하지 않는 모든 매달린(dangling) 이미지 및 컨테이너 삭제 (용량 확보 핵심)
            sh "docker system prune -f"
        }
        success {
            echo "Successfully deployed ${APP_NAME}!"
        }
        failure {
            echo "Deployment failed."
        }
    }
}

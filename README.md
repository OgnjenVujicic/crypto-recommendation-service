# Crypto Recommendation Service

## Build and Run application

### Building Docker Image

1. `cd cryptorecommendation/docker`
2. 
- Option 1: 
 If you prefer packaging application by yourself using maven and already have jar file generated:
  `./docker-build.sh`
- Option 2: If you prefer jar file to be generated automatically:
  `./maven-package-docker-build.sh`
3. Check that the Docker image is successfully created

### Run App using Docker

1. Inside same directory run: `docker-compose up -d`

Test the app: http://localhost:8080/swagger-ui/index.html


### Run App using K8s

This part was added in order to show how it would be deployed using Kubernetes, being closer to
production ready state, as we introduce container orchestrator with horizontal scaling of our microservice.

1. `cd cryptorecommendation/k8s`
2. We use Helm to help manage our k8s manifest files: `helm install crypto-recommendation crypto-recommendation-chart/`
3. Optionally, if we want, we can more easily deploy to production with prepared prod values:
   `helm install crypto-recommendation crypto-recommendation-chart/ --values env/prod-values.yaml`

We can check our k8s deployments: `kubectl get all -n=crypto-app`

Test the app: http://localhost:8080/swagger-ui/index.html


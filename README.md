# Highgarden

docker image at <a href="https://hub.docker.com/r/amumurst/highgarden/">dockerhub</a>
##Running
####Dependencies
* sbt 1.x
####Sbt
1. ```sbt run``` 

access the server with `curl localhost:8080`
####Docker
Make sure to have docker installed
1. create docker image with ```sbt docker```
2. `docker run -d -i -p 8080:8080 <imageId>`

run `docker ps` to see the running image <br/>
access the server with `curl localhost:8080`

###Kubernetes
1. Install kubernetes comandline client, <a href="https://kubernetes.io/docs/tasks/tools/install-kubectl/#download-as-part-of-the-google-cloud-sdk">kubectl</a>
2. `kubectl create -f deployment.yaml`

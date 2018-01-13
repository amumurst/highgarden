# Highgarden

docker image at <a href="https://hub.docker.com/r/amumurst/highgarden/">dockerhub</a>
##Running
#Dependencies
* sbt 1.x
####sbt
1. ```sbt run``` 

access the server with `curl localhost:8080`
####docker
Make sure to have docker installed
1. create docker image with ```sbt docker```
2. `docker run -d -i -p 8080:8080 <imageId>`

run `docker ps` to see the running image <br/>
access the server with `curl localhost:8080`
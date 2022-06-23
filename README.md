# Highgarden

Why the name _highgarden_? Why not.

Highgarden is an example all-in-one webservice. It mounts up a REST-like service for maintaining a list of cars.

```scala 
case class Car(id: Long, licenseNumber: String, color: String, name: Option[String])
```

| Http                           | Body        | Description                              | Returns                  |
|--------------------------------|-------------|------------------------------------------|--------------------------|
| GET localhost:8080/cars        | None        | Lists all cars in database               | List of cars             |
| GET localhost:8080/cars/$id    | None        | Get car with id $id                      | The requested car or 404 |
| POST localhost:8080/cars       | Car         | Insert car with id                       | The inserted car         |
| PATCH localhost:8080/cars/$id  | Car         | Update car $id                           | The updated car or 404   |
| PUT localhost:8080/cars/$id    | Car         | Update car $id or insert if not existent | The updated/inserted car |
| PUT localhost:8080/cars        | List\[Car\] | Replace all cars with list               | The new list of cars     |
| DELETE localhost:8080/cars/$id | None        | Delete car                               | None                     |
| DELETE localhost:8080/cars     | None        | Delete all cars                          | None                     |

### About
Highgarden is built upon:

| Library                                                                      | Description                                    |
|------------------------------------------------------------------------------|------------------------------------------------|
| <a href="https://github.com/scala/scala">Scala</a>                           | Scala programming language                     |
| <a href="https://github.com/sbt/sbt">Sbt</a>                                 | Buildtool for scala                            |
| <a href="https://github.com/sbt/sbt-assembly">Sbt-assembly</a>               | Create fatjars from sbt, used in docker images |
| <a href="https://github.com/marcuslonnberg/sbt-docker">Sbt-docker</a>        | Create docker images from sbt                  |
| <a href="https://github.com/scalameta/scalafmt">Scalafmt</a>                 | Codeformatter for Scala                        |
| <a href="https://github.com/typelevel/cats">Cats</a>                         | Functional library and effect monad            |
| <a href="https://github.com/tpolecat/doobie">Doobie</a>                      | Functional JDBC for database access            |
| <a href="https://github.com/circe/circe">Circe</a>                           | JSON parsing and encoding                      |
| <a href="https://github.com/http4s/http4s">Http4s</a>                        | HTTP services, server and client               |
| <a href="https://github.com/zonkyio/embedded-postgres">embedded-postgres</a> | Embedded database for testing                  |
| <a href="https://github.com/flyway/flyway">Flyway</a>                        | Database migrations                            |

Docker image available at <a href="https://hub.docker.com/r/amumurst/highgarden/">Dockerhub</a>



### Running

#### Sbt
1. ```sbt run``` 

Access the server with `curl localhost:8080/cars`

#### Docker
Make sure to have docker installed
1. create docker image with ```sbt docker```
2. `docker run -d -i -p 8080:8080 <imageId>`
3. run ```sbt dockerPush``` to push image to dockerhub

Run `docker ps` to see the running image <br/>
Access the server with `curl localhost:8080/cars`

#### Kubernetes
1. Install kubernetes comandline client, <a href="https://kubernetes.io/docs/tasks/tools/install-kubectl/#download-as-part-of-the-google-cloud-sdk">kubectl</a>
2. Set up client and connect to your cluster
3. `kubectl create -f deployment.yaml`

Since deployment is based on latest-tag you can get the newest version by deleting the old pods.

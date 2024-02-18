# Wave Application Configuration

Set Wave configuration values using environment variables or in [`config.yml`](./config.yml) configuration file

### config.yml configuration

Declare YAML configuration values in [`config.yml`](./config.yml)
```
wave:
    mail:
        from: "wave-app@seqera.io"
```

YAML configuration keys on this page are listed in "dot" notation, i.e., the mail from value in the snippet above is represented as `wave.mail.from` in the tables that follow.
Environment variables for various attributes has been listed in third column, either you can provide those attributes values in config.yml or using corresponding environment variable.

## General configuration

General configuration options such as wave application name, port, whether to allow anonymous access (without tower token), wave and Seqera platform url.

- **`micronaut.application.name`**: the name of the Wave application. By default, it is set to `wave-app`. *Optional*.

- **`micronaut.server.port`**: the port used by the Wave server. The default port is `8080`. *Optional*.

- **`wave.allowAnonymous`**: specifies whether anonymous access to the Wave server is permitted. By default, it is set to `false`, meaning authentication is required. Change this option based on your security requirements. *Mandatory*.

- **`wave.server.url`**: the URL for the Wave server. You can also set it using `${WAVE_SERVER_URL}` environment variable. *Optional*.

- **`wave.tokens.cache.duration`**: the duration for cached tokens generated by Wave. The default is `1h`, and you can modify it according to your token caching preferences. *Optional*.

- **`tower.endpoint.url`**: the URL pointing to the Seqera platform API service. The default is pointing to Seqera hosted platform [`https://api.tower.nf`](https://api.tower.nf). *Optional*.

- **`logger.levels.io.seqera`**: the [logger level](https://logging.apache.org/log4j/2.x/manual/customloglevels.html)  for the `io.seqera`package. By default, it is set to `TRACE`, providing detailed logging. Adjust this based on logging requirements. *Optional*.


## Container registry configuration

The generic format for the attributes is `wave.registries.<registry_name>.username` and `wave.registries.<registry_name>.password`.
You need to specify all the repositories you will use in the respective wave installation.
Below are the standard format for known registries, but you can change registry name `(azurecr.io)` to specific one like `seqeralabs.azurecr.io `.

**Note**: Container registry credentials can be defined in [`config.yml`](./config.yml) too. These configurations are important for the wave authentication to the repositories used to push or pull artifacts.

- **`wave.registries.default`**: the default Docker registry for Wave. The default is `docker.io`, and it represents the Docker Hub. *Optional*.

- **`wave.registries.docker.io.username`**: the Docker Hub username for authentication. It can be set using `${DOCKER_USER}` environment variable. *Optional*.

- **`wave.registries.docker.io.password`**: the Docker Hub password or PAT (Personal Access Token) for authentication. It can be set using `${DOCKER_PAT}` environment variable. *Optional*.

- **`wave.registries.quay.io.username`**: the Quay.io username for authentication. It can be set using `${QUAY_USER}` environment variable. *Optional*.

- **`wave.registries.quay.io.password`**: the Quay.io password or PAT for authentication. It can be set using `${QUAY_PAT}` environment variable. *Optional*.

- **`wave.registries.<AWS ECR Repo name>.username`**: the AWS ECR (Elastic Container Registry) username for authentication. An example AWS ECR Repo name is `195996028523.dkr.ecr.eu-west-1.amazonaws.com`. It can be set using `${AWS_ACCESS_KEY_ID}` environment variable. *Optional*.

- **`wave.registries.<AWS ECR Repo name>.password`**: the AWS ECR password for authentication. An example AWS ECR Repo name is `195996028523.dkr.ecr.eu-west-1.amazonaws.com`. It can be set using `${AWS_SECRET_ACCESS_KEY}` environment variable. *Optional*.

- **`wave.registries.<azurecr Repo name>.username`**: the Azure Container Registry username for authentication. An example of an Azure Container Registry Repo name is `seqeralabs.azurecr.io`. It can be set using `${AZURECR_USER}` environment variable. *Optional*.

- **`wave.registries.<azurecr Repo name>.password`**: the Azure Container Registry password or PAT for authentication. An example of an Azure Container Registry Repo name is `seqeralabs.azurecr.io`. It can be set using `${AZURECR_PAT}` environment variable. *Optional*.

## HTTP client configuration

- **`wave.httpclient.connectTimeout`**: sets the connection timeout for the HTTP client. Its default value is `20s`. *Optional*.

- **`wave.httpclient.retry.delay`**: sets the delay for HTTP client retries. Its default value is `1s`. *Optional*.

- **`wave.httpclient.retry.attempts`**: defines the number of HTTP client retry attempts. Its default value is `5`. *Optional*.

- **`wave.httpclient.retry.maxDelay`**: sets the maximum delay for HTTP client retries. *Optional*.

- **`wave.httpclient.retry.jitter`**: introduces jitter for HTTP client retries. Its default value is `0.25`. *Optional*.

- **`wave.httpclient.retry.multiplier`**: defines the multiplier for HTTP client retries. Its default value is `1.0`. *Optional*.

- **`micronaut.http.services.stream-client.read-timeout`**: sets the read timeout for the streaming HTTP client. Its default value is `30s`. *Optional*.

- **`micronaut.http.services.stream-client.read-idle-timeout`**: configures the read idle timeout for the streaming HTTP client. Its default value is `120s`. *Optional*.

## Container build process configuration

- **`wave.build.timeout`**: the timeout for the build process. Its default value is `5m` (5 minutes), providing a reasonable time frame for the build operation. *Optional*.

- **`wave.build.workspace`**: defines the path to the directory used by Wave to store artifacts such as Dockerfiles, Trivy cache for scan, Kaniko context, authentication configuration files, etc. For example, `/efs/wave/build`. *Mandatory*.

- **`wave.build.cleanup`**: determines the cleanup strategy after the build process. Options include `OnSuccess`, meaning cleanup occurs only if the build is successful. *Optional*.

- **`wave.build.kaniko-image`**: specifies the [Kaniko](https://github.com/GoogleContainerTools/kaniko) Docker image used in the Wave build process. The default is `gcr.io/kaniko-project/executor:v1.19.2`. *Optional*.

- **`wave.build.singularity-image`**: sets the [Singularity](https://quay.io/repository/singularity/singularity?tab=tags) image used in the build process. The default is `quay.io/singularity/singularity:v3.11.4-slim`. *Optional*.

- **`wave.build.singularity-image-arm64`**: the ARM64 version of the Singularity image for the build process. The default is `quay.io/singularity/singularity:v3.11.4-slim-arm64`. *Optional*.

- **`wave.build.repo`**: specifies the Docker container repository for the Docker images built by Wave. This setting is required to define where the images will be stored. *Mandatory*.

- **`wave.build.cache`**: determines the Docker container repository used to cache layers of images built by Wave. *Mandatory*.

- **`wave.build.status.delay`**: sets the delay between build status checks. Its default value is `5s`, providing a reasonable interval for status checks. *Optional*.

- **`wave.build.status.duration`**: defines the duration for build status checks. Its default value is `1d` (1 day), indicating how long the system should check the build status. *Optional*.

- **`wave.build.public`**: indicates whether the Docker container repository is public. If set to true, Wave freeze will prefer this public repository over `wave.build.repo`. *Optional*.

- **`wave.build.compress-caching`**: determines whether to compress cache layers produced by the build process. The default is `true`, enabling compression for more efficient storage. *Optional*.


### Spack configuration for wave build process

Spack configuration consists of the path of its secret file, the mount path for the secret file in the spack container, and the optional S3 bucket name for the spack binary cache.

**Note**: these configuration are mandatory to support Spack in a wave installation.

- **`wave.build.spack.secretKeyFile`**: the path to the file containing the PGP private key used to [sign Spack packages built by Wave](https://spack.readthedocs.io/en/latest/binary_caches.html#build-cache-signing). For example, `/efs/wave/spack/key`. *Mandatory*.

- **`wave.build.spack.secretMountPath`**: sets the mount path inside the Spack Docker image for the PGP private key specified by `wave.build.spack.secretKeyFile`. For instance `/var/seqera/spack/key`. Indicating where the PGP private key should be mounted inside the Spack Docker image. *Mandatory*.

- **`wave.build.spack.cacheBucket`**: specifies the S3 bucket for the Spack binary cache, for example, `s3://spack-binarycache`. *Optional*.

### Build process logs configuration

This configuration specifies attributes for the persistence of the logs fetched from containers or k8s pods used for building requested images, which can be accessed later and also attached to the build completion email.

- **`wave.build.logs.bucket`**: the AWS S3 bucket where Wave will store build process logs. *Mandatory*.

- **`wave.build.logs.prefix`**: sets the prefix to be used for build process log files in the specified S3 bucket. *Optional*.

- **`wave.build.logs.maxLength`**: determines the maximum number of bytes that can be read from a log file. If a log file exceeds this limit, it will be truncated. The default value is `100000` (100 KB). *Optional*.


### Kubernetes configuration for container build process

Kubernetes configuration has options specific for k8s, and most of them, except CPU and memory, are the same for the build and scan process.

**Note**: only applies when using Kubernetes.

- **`wave.build.k8s.namespace`**: the Kubernetes namespace where Wave will run its build pods. This is a required setting, specifying the namespace to isolate and manage the Wave build processes within the Kubernetes cluster. *Mandatory*.

- **`wave.build.k8s.storage.claimName`**: the volume claim name for the Wave build Kubernetes pods. *Optional*.

- **`wave.build.k8s.storage.mountPath`**: defines the volume mount path on Wave build Kubernetes pods. *Optional*.

- **`wave.build.k8s.labels`**: allows you to set labels on Wave build Kubernetes pods. *Optional*.

- **`wave.build.k8s.node-selector`**: configures the node selector for Wave build Kubernetes pods. *Optional*.

- **`wave.build.k8s.service-account`**: specifies the Kubernetes service account name to be used by Wave build pods. *Optional*.

- **`wave.build.k8s.resources.requests.cpu`**: sets the amount of [CPU resources](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/#resource-units-in-kubernetes) to allocate to Wave build processes. For example, it could be configured as `2` or `1500Mi` (1.5 CPU cores). *Optional*.

- **`wave.build.k8s.resources.requests.memory`**: determines the amount of [memory resources](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/#resource-units-in-kubernetes) to allocate to Wave build processes. For instance, it could be set to `3Gi` or `2000Mi` (3 or 2000 Megabytes). *Optional*.


## Container scan process configuration

Scan process configuration lets the user provide a [Trivy docker image](https://hub.docker.com/r/aquasec/trivy) with any tag and severity levels of vulnerability that need to be scanned.

- **`wave.scan.enabled`**: specifies whether vulnerability scanning is enabled or disabled. It's `false` by default and can be enabled by changing it to `true`. *Optional*.

- **`wave.scan.severity`**: the [severity levels](https://aquasecurity.github.io/trivy/v0.22.0/vulnerability/examples/filter/) to report in vulnerability scanning. For example, you can configure it with `MEDIUM,HIGH,CRITICAL` to include vulnerabilities of these severity levels in the scan report. *Optional*.

- **`wave.scan.image.name`**: the [Trivy docker image](https://hub.docker.com/r/aquasec/trivy) used for container security scanning. The default value is `aquasec/trivy:0.47.0`. This the image that Wave will use to perform vulnerability scanning on containers. *Optional*.


### Kubernetes configuration for Wave scan process

Wave scan process uses the same k8s configuration of the build process except for CPU and memory requirements for the k8s pod.

**Note**: only applies when using Kubernetes.

- **`wave.scan.k8s.resources.requests.cpu`**: the amount of [CPU resources](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/#resource-units-in-kubernetes) allocated to Wave scan processes. For instance, you can set it to `2` or `1500Mi` (1.5 CPU cores). *Optional*.

- **`wave.scan.k8s.resources.requests.memory`**: the [memory resources](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/#resource-units-in-kubernetes) allocated to Wave scan processes. For example, it could be set to `3Gi` or `2000Mi` (3 or 2000 Megabytes). *Optional*.

## Rate limit configuration

Rate limit configuration controls the limits of anonymous and authenticated users' access to Wave.

**Note**: Change these properties to tweak rate limits in wave.

- **`rate-limit.build.anonymous`**: the rate limit for anonymous build requests. By default, it allows up to 10 build requests per hour (`10/1h`) from anonymous users. This setting controls the frequency at which anonymous users can trigger build processes in Wave. *Mandatory*.

- **`rate-limit.build.authenticated`**: the rate limit for authenticated build requests. By default, it allows up to 10 build requests per minute (`10/1m`) from authenticated users. This setting governs the rate at which authenticated users can initiate build processes in Wave. *Mandatory*.

- **`rate-limit.pull.anonymous`**: the rate limit for anonymous pull requests. It is set to allow up to 100 pull requests per hour (`100/1h`) from anonymous users by default. This setting controls how frequently anonymous users can perform pull operations in Wave. *Mandatory*.

- **`rate-limit.pull.authenticated`**: the rate limit for authenticated pull requests. By default, it allows up to 100 pull requests per minute (`100/1m`) from authenticated users. This setting governs the rate at which authenticated users can perform pull operations in Wave. *Mandatory*.

- **`wave.denyPaths`**: user to filter out API calls for specific artifacts like manifests, which doesn't exist. *Optional*.

## Database and cache configuration

- **`redis.uri`**: the Uniform Resource Identifier (URI) for connecting to Redis, a popular in-memory data store. By default, it uses the format `redis://${REDIS_HOST:redis}:${REDIS_PORT:6379}`, allowing customization of the Redis host and port through environment variables. *Mandatory*.

- **`redis.pool.enabled`**: whether to enable the Redis pool. It is set to `true` by default, enabling the use of a connection pool for efficient management of connections to the Redis server. *Optional*.

- **`surreal.default.ns`**: the namespace for the Surreal database. It can be set using `${SURREALDB_NS}` environment variable. *Mandatory*.

- **`surreal.default.db`**: the name of the Surreal database. It can be set using`${SURREALDB_DB}` environment variable. This setting defines the target database within the Surreal database system that Wave should interact with. *Mandatory*.

- **`surreal.default.url`**: the URL for connecting to the Surreal database. It can be set using `${SURREALDB_URL}` environment variable. This URL defines the endpoint that Wave uses to establish a connection with the Surreal database. *Mandatory*.

- **`surreal.default.user`**: the username used for authentication when connecting to the Surreal database. It can be set using `${SURREALDB_USER}` environment variable. *Mandatory*.

- **`surreal.default.password`**: the password used for authentication when connecting to the Surreal database. It can be set using `${SURREALDB_PASSWORD}` environment variable. *Mandatory*.

- **`surreal.default.init-db`**: whether to create database tables, records and indices at application startup  and `db`. *Optional*.

## Blob Cache configuration

Wave offers a feature to provide a cache for Docker blobs, which improves the performance of supplying blobs to the client. If you use Kubernetes, Wave can also use the k8s pod to delegate the transfer task for scalability.

- **`wave.blobCache.enabled`**: whether to enable the blob cache. It is `false` by default. *Optional*.

- **`wave.blobCache.s5cmdImage`**: the Docker image that supplies the [s5cmd tool](https://github.com/peak/s5cmd). This tool is used to upload blob binaries to the S3 bucket. The default image used by Wave is `cr.seqera.io/public/wave/s5cmd:v2.2.2`. *Optional*.

- **`wave.blobCache.status.delay`**: the time delay in checking the status of the transfer of the blob binary from the repository to the cache. Its default value is `5s`. *Optional*.

- **`wave.blobCache.status.duration`**: the time for which Wave will store the blob binary in cache. Its default value is `5d`. *Optional*.

- **`wave.blobCache.timeout`**: timeout for blob binary transfer, after which Wave will throw a `TransferTimeoutException` exception. Its default value is `5m`. *Optional*.

- **`wave.blobCache.baseUrl`**: the URL, which will override the base URL (part of URL before the blob path) of blobs sent to the end client. *Optional*.

- **`wave.blobCache.signing-strategy`**: the URL signing strategy for different services. Currently, Wave offers it for AWS S3 and Cloudflare and you can use the respective values to enable them `aws-presigned-url` and `cloudflare-waf-token`. *Mandatory*. 

- **`wave.blobCache.cloudflare.lifetime`**: the validity of the cloud flare WAF token. *Mandatory*.

- **`wave.blobCache.cloudflare.urlSignatureDuration`**: the validity of the AWS S3 URL signature. Its default value is `30m`. *Optional*.

- **`wave.blobCache.cloudflare.secret-key`**: the [Cloudflare secret](https://developers.cloudflare.com/waf/custom-rules/use-cases/configure-token-authentication/) to create the WAF token. *Mandatory*.

- **`wave.blobCache.storage.bucket`**: the name of Cloudflare or S3 bucket. For example, `s3://wave-blob-cache`. *Mandatory*.

- **`wave.blobCache.storage.region`**: the AWS region where the bucket is created. *Mandatory*.

- **`wave.blobCache.storage.endpoint`**: the URL for the storage location. This will be used for the download or upload of blob binaries. *Optional*.

- **`wave.blobCache.storage.accessKey`**: the access key (part of credentials) to access the resources of the service used for caching. *Optional*.

- **`wave.blobCache.storage.secretKey`**: the secret key (part of credentials) to access the resources of the service used for caching. *Optional*.

- **`wave.blobCache.requestsCpu`**: the amount of [CPU resources](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/#resource-units-in-kubernetes) allocated to the k8s pod used for blob binary transfers. *Optional*.

- **`wave.blobCache.requestsMemory`**: the [memory resources](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/#resource-units-in-kubernetes) allocated to the k8s pod used for blob binary transfers. *Optional*.

## Email configuration

Email id to send emails from on the behalf of the Wave service.

- **`mail.from`**: specifies the sender's email address for Wave notifications. This email address serves as the point of origin for any emails sent by Wave, providing a recognizable source for notifications. This setting is crucial for configuring the sender identity when Wave sends email notifications. *Mandatory*.
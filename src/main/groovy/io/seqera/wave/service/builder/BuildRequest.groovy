package io.seqera.wave.service.builder

import java.nio.file.Path
import java.time.Instant
import java.util.concurrent.CompletableFuture

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import io.seqera.wave.core.ContainerPlatform
import io.seqera.wave.tower.User
import io.seqera.wave.util.DigestFunctions
import static io.seqera.wave.util.StringUtils.trunc
/**
 * Model a container builder result
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
@EqualsAndHashCode(includes = 'id,targetImage')
@CompileStatic
class BuildRequest {

    /**
     * Unique request Id. This is computed as a consistent hash generated from
     * the container build assets e.g. Dockerfile. Therefore the same container build
     * request should result in the same `id`
     */
    final String id

    /**
     * The dockerfile content corresponding to this request
     */
    final String dockerFile

    /**
     * The conda file recipe associated with this request
     */
    final String condaFile

    /**
     * The build context work directory
     */
    final Path workDir

    /**
     * The target fully qualified image of the built container. It includes the target registry name
     */
    final String targetImage

    /**
     * The (tower) user made this request
     */
    final User user

    /**
     * Container platform
     */
    final ContainerPlatform platform

    /**
     * Container repository for caching purposes
     */
    final String cacheRepository

    /**
     * Build request start time
     */
    final Instant startTime

    /**
     * Build jon unique id
     */
    final String job

    /**
     * Reference to the future build job result
     */
    volatile CompletableFuture<BuildResult> result

    BuildRequest(String dockerFile, Path workspace, String repo, String condaFile, User user, ContainerPlatform platform, String cacheRepo) {
        this.id = computeDigest(dockerFile,condaFile,platform)
        this.dockerFile = dockerFile
        this.condaFile = condaFile
        this.targetImage = "${repo}:${id}"
        this.user = user
        this.platform = platform
        this.cacheRepository = cacheRepo
        this.workDir = workspace.resolve(id).toAbsolutePath()
        this.startTime = Instant.now()
        this.job = "${id}-${startTime.toEpochMilli().toString().md5()[-5..-1]}"
    }

    static private String computeDigest(String dockerFile, String condaFile, ContainerPlatform platform) {
        def content = platform.toString()
        content += dockerFile
        if( condaFile )
            content += condaFile
        return DigestFunctions.md5(content)
    }

    @Override
    String toString() {
        return "BuildRequest[id=$id; targetImage=$targetImage; user=$user; dockerFile=${trunc(dockerFile)}; condaFile=${trunc(condaFile)}]"
    }

}

package io.seqera.wave.service

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.seqera.wave.api.ContainerConfig
import io.seqera.wave.model.ContainerCoordinates
/**
 * Model a container request
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
@Canonical
@CompileStatic
class ContainerRequestData {

    final Long userId
    final Long workspaceId
    final String containerImage
    final String containerFile
    final ContainerConfig containerConfig

    ContainerCoordinates coordinates() { ContainerCoordinates.parse(containerImage) }
}

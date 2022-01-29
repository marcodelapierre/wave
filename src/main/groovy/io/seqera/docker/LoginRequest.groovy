package io.seqera.docker

import groovy.transform.Canonical

/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
@Canonical
class LoginRequest {
    String username
    String password
}

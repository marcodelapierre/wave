/*
 *  Wave, containers provisioning service
 *  Copyright (c) 2023-2024, Seqera Labs
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.seqera.wave.service.builder

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path
import java.time.OffsetDateTime

import io.seqera.wave.api.BuildContext
import io.seqera.wave.api.ContainerConfig
import io.seqera.wave.api.ImageNameStrategy
import io.seqera.wave.core.ContainerPlatform
import io.seqera.wave.tower.PlatformId
import io.seqera.wave.tower.User
/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
class BuildRequestTest extends Specification {

    def 'should create docker build request'() {
        given:
        def USER = new PlatformId(new User(id:1, email: 'foo@user.com'))
        def CONTENT = 'FROM foo'
        def PATH = Path.of('somewhere')
        def BUILD_REPO = 'docker.io/wave'
        def CACHE_REPO = 'docker.io/cache'
        def SCAN_ID = '123456'
        def IP_ADDR = '10.20.30.40'
        def OFFSET = '+2'
        def CONFIG = new ContainerConfig(env: ['FOO=1'])
        def CONTEXT = Mock(BuildContext)
        def PLATFORM = ContainerPlatform.of('amd64')
        def FORMAT = BuildFormat.DOCKER
        def CONTAINER_ID = BuildRequest.computeDigest(CONTENT, null, null, PLATFORM, BUILD_REPO, CONTEXT)
        def TARGET_IMAGE = BuildRequest.makeTarget(FORMAT, BUILD_REPO, CONTAINER_ID, null, null, null)

        when:
        def req = new BuildRequest(
                CONTAINER_ID,
                CONTENT,
                null,
                null,
                PATH,
                TARGET_IMAGE,
                USER,
                PLATFORM,
                CACHE_REPO,
                IP_ADDR,
                '{"config":"json"}',
                OFFSET,
                CONFIG,
                SCAN_ID,
                CONTEXT,
                FORMAT
        )

        then:
        req.containerId == '181ec22b26ae6d04'
        req.targetImage == "docker.io/wave:${req.containerId}"
        req.containerFile == CONTENT
        req.identity == USER
        req.configJson == '{"config":"json"}'
        req.cacheRepository == CACHE_REPO
        req.format == BuildFormat.DOCKER
        req.condaFile == null
        req.spackFile == null
        req.platform == ContainerPlatform.of('amd64')
        req.configJson == '{"config":"json"}'
        req.scanId == SCAN_ID
        req.ip == IP_ADDR
        req.offsetId == OFFSET
        req.containerConfig == CONFIG
        req.buildContext == CONTEXT
        and:
        !req.isSpackBuild

        // ==== provide a Conda recipe ====
        when:
        def CONDA_RECIPE = '''\
                dependencies:
                    - samtools=1.0
                '''
        and:
        CONTAINER_ID = BuildRequest.computeDigest(CONTENT, CONDA_RECIPE, null, PLATFORM, BUILD_REPO, CONTEXT)
        TARGET_IMAGE = BuildRequest.makeTarget(FORMAT, BUILD_REPO, CONTAINER_ID, CONDA_RECIPE, null, null)
        req = new BuildRequest(
                CONTAINER_ID,
                CONTENT,
                CONDA_RECIPE,
                null,
                PATH,
                TARGET_IMAGE,
                USER,
                PLATFORM,
                CACHE_REPO,
                IP_ADDR,
                '{"config":"json"}',
                OFFSET,
                CONFIG,
                SCAN_ID,
                CONTEXT,
                FORMAT
        )
        then:
        req.containerId == '8026e3a63b5c863f'
        req.targetImage == 'docker.io/wave:samtools-1.0--8026e3a63b5c863f'
        req.condaFile == CONDA_RECIPE
        req.spackFile == null
        and:
        !req.isSpackBuild

        // ===== spack content ====
        def SPACK_RECIPE = '''\
            spack:
              specs: [bwa@0.7.15]
            '''

        when:
        CONTAINER_ID = BuildRequest.computeDigest(CONTENT, null, SPACK_RECIPE, PLATFORM, BUILD_REPO, CONTEXT)
        TARGET_IMAGE = BuildRequest.makeTarget(FORMAT, BUILD_REPO, CONTAINER_ID, null, SPACK_RECIPE, null)
        req = new BuildRequest(
                CONTAINER_ID,
                CONTENT,
                null,
                SPACK_RECIPE,
                PATH,
                TARGET_IMAGE,
                USER,
                PLATFORM,
                CACHE_REPO,
                IP_ADDR,
                '{"config":"json"}',
                OFFSET,
                CONFIG,
                SCAN_ID,
                CONTEXT,
                FORMAT
        )
        then:
        req.containerId == '8726782b1d9bb8fb'
        req.targetImage == 'docker.io/wave:bwa-0.7.15--8726782b1d9bb8fb'
        req.spackFile == SPACK_RECIPE
        req.condaFile == null
        and:
        req.isSpackBuild
    }

    def 'should create singularity build request'() {
        given:
        def USER = new PlatformId(new User(id:1, email: 'foo@user.com'))
        def CONTENT = 'From: foo'
        def PATH = Path.of('somewhere')
        def BUILD_REPO = 'docker.io/wave'
        def CACHE_REPO = 'docker.io/cache'
        def IP_ADDR = '10.20.30.40'
        def OFFSET = '+2'
        def CONFIG = new ContainerConfig(env: ['FOO=1'])
        def CONTEXT = Mock(BuildContext)
        def PLATFORM = ContainerPlatform.of('amd64')
        def FORMAT = BuildFormat.SINGULARITY
        def CONTAINER_ID = BuildRequest.computeDigest(CONTENT, null, null, PLATFORM, BUILD_REPO, CONTEXT)
        def TARGET_IMAGE = BuildRequest.makeTarget(FORMAT, BUILD_REPO, CONTAINER_ID, null, null, null)

        when:
        def req = new BuildRequest(
                CONTAINER_ID,
                CONTENT,
                null,
                null,
                PATH,
                TARGET_IMAGE,
                USER,
                PLATFORM,
                CACHE_REPO,
                IP_ADDR,
                '{"config":"json"}',
                OFFSET,
                CONFIG,
                null,
                CONTEXT,
                FORMAT
        )
        then:
        req.containerId == 'd78ba9cb01188668'
        req.targetImage == "oras://docker.io/wave:${req.containerId}"
        req.containerFile == CONTENT
        req.identity == USER
        req.configJson == '{"config":"json"}'
        req.cacheRepository == CACHE_REPO
        req.format == BuildFormat.SINGULARITY
        req.platform == ContainerPlatform.of('amd64')
        req.configJson == '{"config":"json"}'
        req.ip == IP_ADDR
        req.offsetId == OFFSET
        req.containerConfig == CONFIG
        req.buildContext == CONTEXT
        and:
        !req.isSpackBuild

    }

    def 'should check equals and hash code'() {
        given:
        def USER = new PlatformId(new User(id:1, email: 'foo@user.com'))
        def PATH = Path.of('somewhere')
        def BUILD_REPO = 'docker.io/wave'
        def CACHE_REPO = 'docker.io/cache'
        def PLATFORM = ContainerPlatform.of('amd64')
        def FORMAT = BuildFormat.DOCKER
        def CONDA_CONTENT = 'salmon=1.2.3'
        def FOO_CONTENT = 'from foo'
        def BAR_CONTENT = 'from bar'
        and:
        def CONTAINER_ID1 = BuildRequest.computeDigest(FOO_CONTENT, null, null, PLATFORM, BUILD_REPO, null)
        def TARGET_IMAGE1 = BuildRequest.makeTarget(FORMAT, BUILD_REPO, CONTAINER_ID1, null, null, null)
        def req1 = new BuildRequest(CONTAINER_ID1, FOO_CONTENT, null, null, PATH, TARGET_IMAGE1, USER, PLATFORM, CACHE_REPO, "10.20.30.40", '{"config":"json"}', null, null, null, null, FORMAT)
        and:
        def req2 = new BuildRequest(CONTAINER_ID1, FOO_CONTENT, null, null, PATH, TARGET_IMAGE1, USER, PLATFORM, CACHE_REPO, "10.20.30.40", '{"config":"json"}', null, null, null, null, FORMAT)
        and:
        def CONTAINER_ID3 = BuildRequest.computeDigest(BAR_CONTENT, null, null, PLATFORM, BUILD_REPO, null)
        def TARGET_IMAGE3 = BuildRequest.makeTarget(FORMAT, BUILD_REPO, CONTAINER_ID3, null, null, null)
        def req3 = new BuildRequest(CONTAINER_ID3, BAR_CONTENT, null, null, PATH, TARGET_IMAGE3, USER, PLATFORM, CACHE_REPO, "10.20.30.40", '{"config":"json"}', null, null, null, null, FORMAT)
        and:
        def CONTAINER_ID4 = BuildRequest.computeDigest(BAR_CONTENT, CONDA_CONTENT, null, PLATFORM, BUILD_REPO, null)
        def TARGET_IMAGE4 = BuildRequest.makeTarget(FORMAT, BUILD_REPO, CONTAINER_ID4, CONDA_CONTENT, null, null)
        def req4 = new BuildRequest(CONTAINER_ID4, BAR_CONTENT, CONDA_CONTENT, null, PATH, TARGET_IMAGE4, USER, PLATFORM, CACHE_REPO, "10.20.30.40", '{"config":"json"}', null, null, null, null, FORMAT)
        and:
        def req5 = new BuildRequest(CONTAINER_ID4, BAR_CONTENT, CONDA_CONTENT, null, PATH, TARGET_IMAGE4, USER, PLATFORM, CACHE_REPO, "10.20.30.40", '{"config":"json"}', null, null, null, null, FORMAT)
        and:
        CONDA_CONTENT = 'salmon=1.2.5'
        def CONTAINER_ID6 = BuildRequest.computeDigest(BAR_CONTENT, CONDA_CONTENT, null, PLATFORM, BUILD_REPO, null)
        def TARGET_IMAGE6 = BuildRequest.makeTarget(FORMAT, BUILD_REPO, CONTAINER_ID6, CONDA_CONTENT, null, null)
        def req6 = new BuildRequest(CONTAINER_ID4, BAR_CONTENT, CONDA_CONTENT, null, PATH, TARGET_IMAGE6, USER, PLATFORM, CACHE_REPO, "10.20.30.40", '{"config":"json"}', null, null, null, null, FORMAT)
        and:
        def req7 = new BuildRequest(CONTAINER_ID4, BAR_CONTENT, CONDA_CONTENT, null, PATH, TARGET_IMAGE6, USER, PLATFORM, CACHE_REPO, "10.20.30.40", '{"config":"json"}', "UTC+2", null, null, null, FORMAT)

        expect:
        req1 == req2
        req1 != req3
        and:
        req4 == req5
        req4 != req6
        and:
        req1 != req5
        req1 != req6
        req1 != req7

        and:
        req1.hashCode() == req2.hashCode()
        req1.hashCode() != req3.hashCode()
        and:
        req4.hashCode() == req5 .hashCode()
        req4.hashCode() != req6.hashCode()
        and:
        req1.hashCode() != req5.hashCode()
        req1.hashCode() != req6.hashCode()

        and:
        req1.offsetId == OffsetDateTime.now().offset.id
        req7.offsetId == 'UTC+2'
    }

    def 'should make request target' () {
        expect:
        BuildRequest.makeTarget(BuildFormat.DOCKER, 'quay.io/org/name', '12345', null, null, null)
                == 'quay.io/org/name:12345'
        and:
        BuildRequest.makeTarget(BuildFormat.SINGULARITY, 'quay.io/org/name', '12345', null, null, null)
                == 'oras://quay.io/org/name:12345'

        and:
        def conda = '''\
        dependencies:
        - salmon=1.2.3
        '''
        BuildRequest.makeTarget(BuildFormat.DOCKER, 'quay.io/org/name', '12345', conda, null, null)
                == 'quay.io/org/name:salmon-1.2.3--12345'

        and:
        def spack = '''\
         spack:
            specs: [bwa@0.7.15]
        '''
        BuildRequest.makeTarget(BuildFormat.DOCKER, 'quay.io/org/name', '12345', null, spack, null)
                == 'quay.io/org/name:bwa-0.7.15--12345'

    }

    @Shared def CONDA1 = '''\
                dependencies:
                    - samtools=1.0
                '''

    @Shared def CONDA2 = '''\
                dependencies:
                    - samtools=1.0
                    - bamtools=2.0
                    - multiqc=1.15
                '''

    @Shared def SPACK1 = '''\
            spack:
              specs: [bwa@0.7.15]
            '''

    @Shared def SPACK2 = '''\
            spack:
              specs: [bwa@0.7.15, salmon@1.1.1]
        '''

    @Unroll
    def 'should make request target with name strategy' () {
        expect:
        BuildRequest.makeTarget(
                BuildFormat.valueOf(FORMAT),
                REPO,
                ID,
                CONDA,
                SPACK,
                STRATEGY ? ImageNameStrategy.valueOf(STRATEGY) : null) == EXPECTED

        where:
        FORMAT        | REPO              | ID        | CONDA | SPACK | STRATEGY      | EXPECTED
        'DOCKER'      | 'foo.com/build'   | '123'     | null  | null  | null          | 'foo.com/build:123'
        'DOCKER'      | 'foo.com/build'   | '123'     | null  | null  | 'none'        | 'foo.com/build:123'
        'DOCKER'      | 'foo.com/build'   | '123'     | null  | null  | 'tagPrefix'   | 'foo.com/build:123'
        'DOCKER'      | 'foo.com/build'   | '123'     | null  | null  | 'imageSuffix' | 'foo.com/build:123'
        and:
        'SINGULARITY' | 'foo.com/build'   | '123'     | null  | null  | null          | 'oras://foo.com/build:123'
        'SINGULARITY' | 'foo.com/build'   | '123'     | null  | null  | 'none'        | 'oras://foo.com/build:123'
        'SINGULARITY' | 'foo.com/build'   | '123'     | null  | null  | 'tagPrefix'   | 'oras://foo.com/build:123'
        'SINGULARITY' | 'foo.com/build'   | '123'     | null  | null  | 'imageSuffix' | 'oras://foo.com/build:123'
        and:
        'DOCKER'      | 'foo.com/build'   | '123'     | CONDA1| null  | null          | 'foo.com/build:samtools-1.0--123'
        'DOCKER'      | 'foo.com/build'   | '123'     | CONDA1| null  | 'none'        | 'foo.com/build:123'
        'DOCKER'      | 'foo.com/build'   | '123'     | CONDA1| null  | 'tagPrefix'   | 'foo.com/build:samtools-1.0--123'
        'DOCKER'      | 'foo.com/build'   | '123'     | CONDA1| null  | 'imageSuffix' | 'foo.com/build/samtools:1.0--123'
        and:
        'SINGULARITY' | 'foo.com/build'   | '123'     | CONDA1| null  | null          | 'oras://foo.com/build:samtools-1.0--123'
        'SINGULARITY' | 'foo.com/build'   | '123'     | CONDA1| null  | 'none'        | 'oras://foo.com/build:123'
        'SINGULARITY' | 'foo.com/build'   | '123'     | CONDA1| null  | 'tagPrefix'   | 'oras://foo.com/build:samtools-1.0--123'
        'SINGULARITY' | 'foo.com/build'   | '123'     | CONDA1| null  | 'imageSuffix' | 'oras://foo.com/build/samtools:1.0--123'
        and:
        'DOCKER'      | 'foo.com/build'   | '123'     | CONDA2| null  | null          | 'foo.com/build:samtools-1.0_bamtools-2.0_multiqc-1.15--123'
        'DOCKER'      | 'foo.com/build'   | '123'     | CONDA2| null  | 'none'        | 'foo.com/build:123'
        'DOCKER'      | 'foo.com/build'   | '123'     | CONDA2| null  | 'tagPrefix'   | 'foo.com/build:samtools-1.0_bamtools-2.0_multiqc-1.15--123'
        'DOCKER'      | 'foo.com/build'   | '123'     | CONDA2| null  | 'imageSuffix' | 'foo.com/build/samtools_bamtools_multiqc:123'

        and:
        'DOCKER'      | 'foo.com/build'   | '123'     | null  | SPACK1| null          | 'foo.com/build:bwa-0.7.15--123'
        'DOCKER'      | 'foo.com/build'   | '123'     | null  | SPACK1| 'none'        | 'foo.com/build:123'
        'DOCKER'      | 'foo.com/build'   | '123'     | null  | SPACK1| 'tagPrefix'   | 'foo.com/build:bwa-0.7.15--123'
        'DOCKER'      | 'foo.com/build'   | '123'     | null  | SPACK1| 'imageSuffix' | 'foo.com/build/bwa:0.7.15--123'

        and:
        'DOCKER'      | 'foo.com/build'   | '123'     | null  | SPACK2| null          | 'foo.com/build:bwa-0.7.15_salmon-1.1.1--123'
        'DOCKER'      | 'foo.com/build'   | '123'     | null  | SPACK2| 'none'        | 'foo.com/build:123'
        'DOCKER'      | 'foo.com/build'   | '123'     | null  | SPACK2| 'tagPrefix'   | 'foo.com/build:bwa-0.7.15_salmon-1.1.1--123'
        'DOCKER'      | 'foo.com/build'   | '123'     | null  | SPACK2| 'imageSuffix' | 'foo.com/build/bwa_salmon:123'
    }

    @Unroll
    def 'should normalise tag' () {
        expect:
        BuildRequest.normaliseTag(TAG,12)  == EXPECTED
        where:
        TAG                     | EXPECTED
        null                    | null
        ''                      | null
        and:
        'foo'                   | 'foo'
        'FOO123'                | 'FOO123'
        'aa-bb_cc.dd'           | 'aa-bb_cc.dd'
        and:
        'one(two)three'         | 'onetwothree'
        '12345_67890_12345'     | '12345_67890'
        '123456789012345_1'     | '123456789012'
        and:
        'aa__'                  | 'aa'
        'aa..--__'              | 'aa'
        '..--__bb'              | 'bb'
        '._-xyz._-'             | 'xyz'
    }

    def 'should normalise name' () {
        expect:
        BuildRequest.normaliseName(NAME, 12)  == EXPECTED
        where:
        NAME                    | EXPECTED
        null                    | null
        ''                      | null
        and:
        'foo'                   | 'foo'
        'foo/bar'               | 'foo/bar'
        'FOO123'                | 'foo123'
        'aa-bb_cc.dd'           | 'aa-bb_cc.dd'
        and:
        'one(two)three'         | 'onetwothree'
        '12345_67890_12345'     | '12345_67890'
        '123456789012345_1'     | '123456789012'
        and:
        'aa__'                  | 'aa'
        'aa..--__'              | 'aa'
        '..--__bb'              | 'bb'
        '._-xyz._-'             | 'xyz'
    }

    def 'should parse legacy id' () {
        expect:
        BuildRequest.legacyBuildId(BUILD_ID) == EXPECTED
        where:
        BUILD_ID        | EXPECTED
        null            | null
        'foo'           | null
        'foo_01'        | 'foo'
    }

}

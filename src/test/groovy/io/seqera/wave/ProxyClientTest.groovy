package io.seqera.wave

import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification

import io.micronaut.context.ApplicationContext
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.seqera.wave.auth.RegistryAuth
import io.seqera.wave.auth.RegistryAuthService
import io.seqera.wave.auth.RegistryCredentialsProvider
import io.seqera.wave.auth.RegistryLookupService
import io.seqera.wave.proxy.ProxyClient
import io.seqera.wave.test.DockerRegistryContainer
import jakarta.inject.Inject
/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
@MicronautTest
class ProxyClientTest extends Specification implements DockerRegistryContainer{

    @Inject
    @Shared
    ApplicationContext applicationContext

    @Inject RegistryLookupService lookupService
    @Inject RegistryAuthService loginService
    @Inject RegistryCredentialsProvider credentialsProvider

    def setupSpec() {
        initRegistryContainer(applicationContext)
    }

    def 'should call target blob' () {
        given:
        def IMAGE = 'library/hello-world'
        and:
        def proxy = new ProxyClient()
                .withImage(IMAGE)
                .withRegistry(getLocalTestRegistryInfo())
                .withLoginService(loginService)

        when:
        def resp1 = proxy.getString('/v2/library/hello-world/manifests/latest')
        and:
        println resp1.body()
        then:
        resp1.statusCode() == 200
    }

    def 'should call target blob on quay' () {
        given:
        def REG = 'quay.io'
        def IMAGE = 'biocontainers/fastqc'
        def registry = lookupService.lookup(REG)
        def creds = credentialsProvider.getCredentials(REG)
        and:
        def proxy = new ProxyClient()
                .withImage(IMAGE)
                .withRegistry(registry)
                .withLoginService(loginService)
                .withCredentials(creds)

        when:
        def resp1 = proxy.getString('/v2/biocontainers/fastqc/blobs/sha256:a3ed95caeb02ffe68cdd9fd84406680ae93d633cb16422d00e8a7c22955b46d4')
        and:
        then:
        resp1.statusCode() == 200
    }

    def 'should lookup aws registry' () {
        when:
        def registry = lookupService.lookup('195996028523.dkr.ecr.eu-west-1.amazonaws.com')
        then:
        registry.name == '195996028523.dkr.ecr.eu-west-1.amazonaws.com'
        registry.host == new URI('https://195996028523.dkr.ecr.eu-west-1.amazonaws.com')
        registry.auth.realm == new URI('https://195996028523.dkr.ecr.eu-west-1.amazonaws.com/')
        registry.auth.service == 'ecr.amazonaws.com'
        registry.auth.type == RegistryAuth.Type.Basic
    }

    @Requires({System.getenv('AWS_ACCESS_KEY_ID') && System.getenv('AWS_SECRET_ACCESS_KEY')})
    def 'should call target manifest on amazon' () {
        given:
        def IMAGE = 'wave/kaniko'
        def REG = '195996028523.dkr.ecr.eu-west-1.amazonaws.com'
        def registry = lookupService.lookup(REG)
        def creds = credentialsProvider.getCredentials(REG)
        and:
        def proxy = new ProxyClient()
                .withImage(IMAGE)
                .withRegistry(registry)
                .withLoginService(loginService)
                .withCredentials(creds)

        when:
        def resp = proxy.getString("/v2/$IMAGE/manifests/0.1.0")
        then:
        resp.statusCode() == 200
    }
    
}
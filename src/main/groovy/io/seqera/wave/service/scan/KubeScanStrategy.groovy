package io.seqera.wave.service.scan

import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import javax.annotation.Nullable

import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.models.V1Job
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.seqera.wave.configuration.ScanConfig
import io.seqera.wave.exception.BadRequestException
import io.seqera.wave.service.k8s.K8sService
import jakarta.inject.Singleton
import static io.seqera.wave.util.K8sHelper.getSelectorLabel
import static java.nio.file.StandardOpenOption.CREATE
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import static java.nio.file.StandardOpenOption.WRITE
/**
 * Implements ScanStrategy for Kubernetes
 *
 * @author Munish Chouhan <munish.chouhan@seqera.io>
 */
@Slf4j
@Primary
@Requires(property = 'wave.build.k8s')
@Singleton
@CompileStatic
class KubeScanStrategy extends ScanStrategy {

    @Property(name='wave.build.k8s.node-selector')
    @Nullable
    private Map<String, String> nodeSelectorMap

    private final K8sService k8sService

    private final ScanConfig scanConfig

    KubeScanStrategy(K8sService k8sService, ScanConfig scanConfig) {
        this.k8sService = k8sService
        this.scanConfig = scanConfig
    }

    @Override
    ScanResult scanContainer(ScanRequest req) {
        log.info("Launching container scan for buildId: ${req.id}")
        final startTime = Instant.now()

        final podName = "scan-${req.id}"
        try{
            // create the scan dir
            try {
                Files.createDirectory(req.workDir)
            }
            catch (FileAlreadyExistsException e) {
                log.warn("Container scan directory already exists: $e")
            }

            // save the config file with docker auth credentials
            Path configFile = null
            if( req.configJson ) {
                configFile = req.workDir.resolve('config.json')
                Files.write(configFile, JsonOutput.prettyPrint(req.configJson).bytes, CREATE, WRITE, TRUNCATE_EXISTING)
            }

            final reportFile = req.workDir.resolve(Trivy.OUTPUT_FILE_NAME)

            V1Job job
            final trivyCommand = scanCommand(req.targetImage, reportFile, scanConfig)
            final selector= getSelectorLabel(req.platform, nodeSelectorMap)
            final pod = k8sService.scanContainer(podName, scanConfig.scanImage, trivyCommand, req.workDir, configFile, scanConfig, selector)
            final terminated = k8sService.waitPod(pod, scanConfig.timeout.toMillis())
            if( terminated ) {
                log.info("Container scan completed for buildId: ${req.id}")
                return ScanResult.success(req, startTime, TrivyResultProcessor.process(reportFile.text))
            }
            else{
                log.info("Container scan failed for buildId: ${req.id}")
                return ScanResult.failure(req, startTime, null)
            }
        }
        catch (ApiException e) {
            throw new BadRequestException("Unexpected scan failure: ${e.responseBody}", e)
        }
        catch (Exception e){
            log.warn("Error creating scan pod: ${e.getMessage()}", e)
            return ScanResult.failure(req, startTime, null)
        }
        finally {
            cleanup(podName)
        }
    }

    void cleanup(String podName) {
        try {
            k8sService.deletePod(podName)
        }
        catch (Exception e) {
            log.warn ("Unable to delete pod=$podName - cause: ${e.message ?: e}", e)
        }
    }
}
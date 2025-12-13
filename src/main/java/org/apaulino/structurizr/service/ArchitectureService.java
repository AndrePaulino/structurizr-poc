package org.apaulino.structurizr.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apaulino.structurizr.dto.ServiceInfo;
import org.apaulino.structurizr.service.provider.ArchitectureServiceProvider;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ArchitectureService {

    private static final String AGGREGATOR_INDEX_PATH = "/META-INF/resources/aggregator/index.html";

    private final StaticSiteGeneratorService staticSiteGeneratorService;
    private final ServiceRegistry serviceRegistry;

    public ArchitectureService(StaticSiteGeneratorService staticSiteGeneratorService,
            ServiceRegistry serviceRegistry) {
        this.staticSiteGeneratorService = staticSiteGeneratorService;
        this.serviceRegistry = serviceRegistry;
    }

    public String getAggregatorHtml() throws IOException {
        Log.info("Loading aggregator architecture HTML");
        return loadAndFixHtml();
    }

    public String getServiceHtml(String serviceName) throws IOException {
        Log.info("Loading architecture HTML for service: " + serviceName);
        String html = loadAndFixHtml();

        return html
                .replace("\"./workspace.js\"", "\"/api/architecture/services/" + serviceName + "/workspace.js\"")
                .replace("\"workspace.js\"", "\"/api/architecture/services/" + serviceName + "/workspace.js\"")
                .replace("\"/aggregator/workspace.js\"",
                        "\"/api/architecture/services/" + serviceName + "/workspace.js\"");
    }

    public String generateServiceWorkspaceJs(String serviceName) {
        Log.info("Generating workspace.js for service: " + serviceName);
        String dslFragment = getDslFragmentOrThrow(serviceName);
        String displayName = getDisplayName(serviceName);
        return staticSiteGeneratorService.generateWorkspaceJsFromFragment(dslFragment, displayName);
    }

    public String getServiceDsl(String serviceName) {
        Log.info("Getting DSL for service: " + serviceName);
        return getDslFragmentOrThrow(serviceName);
    }

    public List<ServiceInfo> listServices() {
        Log.info("Listing all registered services");
        return serviceRegistry.getServiceNames().stream()
                .map(serviceName -> serviceRegistry.getServiceInfo(serviceName)
                        .map(ServiceInfo::from)
                        .orElseGet(() -> ServiceInfo.fromServiceName(serviceName)))
                .toList();
    }

    private String loadAndFixHtml() throws IOException {
        try (InputStream is = getClass().getResourceAsStream(AGGREGATOR_INDEX_PATH)) {
            if (is == null) throw new IOException("index.html not found at " + AGGREGATOR_INDEX_PATH);
            return fixStaticPaths(new String(is.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    private String fixStaticPaths(String html) {
        return html
                .replace("\"./js/", "\"/aggregator/js/")
                .replace("\"./css/", "\"/aggregator/css/")
                .replace("\"./img/", "\"/aggregator/img/")
                .replace("\"./workspace.js\"", "\"/aggregator/workspace.js\"");
    }

    private String getDslFragmentOrThrow(String serviceName) {
        return serviceRegistry.getDslFragment(serviceName)
                .orElseThrow(() -> new ServiceNotFoundException(serviceName));
    }

    private String getDisplayName(String serviceName) {
        return serviceRegistry.getServiceInfo(serviceName)
                .map(ArchitectureServiceProvider::getDisplayName)
                .orElse(serviceName);
    }

    public static class ServiceNotFoundException extends RuntimeException {
        private final String serviceName;

        public ServiceNotFoundException(String serviceName) {
            super("Service not found: " + serviceName);
            this.serviceName = serviceName;
        }

        public String getServiceName() {
            return serviceName;
        }
    }
}

package org.apaulino.structurizr.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class GeneratorService {

    private final StaticFileWriterService staticFileWriterService;
    private final ServiceRegistry serviceRegistry;

    public GeneratorService(StaticFileWriterService staticFileWriterService,
            ServiceRegistry serviceRegistry) {
        this.staticFileWriterService = staticFileWriterService;
        this.serviceRegistry = serviceRegistry;
    }

    public Map<String, Object> generateAllServices() {
        Log.info("Starting static file generation for all services");

        int count = staticFileWriterService.generateAllServices();
        List<String> services = serviceRegistry.getServiceNames();

        Log.info("Successfully generated static files for " + count + " services");

        return buildSuccessResponse(
                "Generated static files for " + count + " services",
                services);
    }

    public Map<String, Object> generateService(String serviceName) throws IOException {
        Log.info("Starting static file generation for service: " + serviceName);

        if (!serviceRegistry.getAvailableServices().contains(serviceName)) {
            throw new IllegalArgumentException("Service not found: " + serviceName);
        }

        staticFileWriterService.generateServiceFiles(serviceName);

        Log.info("Successfully generated static files for " + serviceName);

        return buildSuccessResponse(
                "Generated static files for " + serviceName,
                List.of(serviceName));
    }

    public List<String> getServiceNames() {
        return serviceRegistry.getServiceNames();
    }

    private Map<String, Object> buildSuccessResponse(String message, List<String> services) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        response.put("services", services);
        response.put("outputDir", staticFileWriterService.getOutputDirectory());
        response.put("accessUrls", services.stream()
                .map(s -> "/services/" + s + "/index.html")
                .toList());
        return response;
    }
}

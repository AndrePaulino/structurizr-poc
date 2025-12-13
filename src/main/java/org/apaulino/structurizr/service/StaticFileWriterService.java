package org.apaulino.structurizr.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apaulino.structurizr.service.provider.ArchitectureServiceProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StaticFileWriterService {

    @ConfigProperty(name = "structurizr.static.output-dir", defaultValue = "target/classes/META-INF/resources/services")
    String outputDir;

    private final ServiceRegistry serviceRegistry;
    private final StaticSiteGeneratorService staticSiteGeneratorService;

    public StaticFileWriterService(ServiceRegistry serviceRegistry,
            StaticSiteGeneratorService staticSiteGeneratorService) {
        this.serviceRegistry = serviceRegistry;
        this.staticSiteGeneratorService = staticSiteGeneratorService;
    }

    public int generateAllServices() {
        Log.info("Generating static files for all services");

        int success = 0;
        for (String serviceName : serviceRegistry.getServiceNames()) {
            try {
                generateServiceFiles(serviceName);
                success++;
            } catch (Exception e) {
                Log.error("Failed to generate files for " + serviceName + ": " + e.getMessage(), e);
            }
        }

        Log.info("Generated static files for " + success + " services");
        return success;
    }

    public void generateServiceFiles(String serviceName) throws IOException {
        Log.info("Generating static files for service: " + serviceName);

        Optional<String> dslFragment = serviceRegistry.getDslFragment(serviceName);
        if (dslFragment.isEmpty()) {
            throw new IllegalArgumentException("Service not found or DSL unavailable: " + serviceName);
        }

        String displayName = serviceRegistry.getServiceInfo(serviceName)
                .map(ArchitectureServiceProvider::getDisplayName)
                .orElse(serviceName);

        String workspaceJs = staticSiteGeneratorService.generateWorkspaceJsFromFragment(
                dslFragment.get(),
                displayName);

        Path serviceDir = Path.of(outputDir, serviceName);
        Files.createDirectories(serviceDir);

        Path workspaceJsPath = serviceDir.resolve("workspace.js");
        Files.writeString(workspaceJsPath, workspaceJs, StandardCharsets.UTF_8);
        Log.info("Written: " + workspaceJsPath);

        String indexHtml = generateIndexHtml(serviceName, displayName);
        Path indexHtmlPath = serviceDir.resolve("index.html");
        Files.writeString(indexHtmlPath, indexHtml, StandardCharsets.UTF_8);
        Log.info("Written: " + indexHtmlPath);
        Log.info("Successfully generated static files for: " + serviceName);
    }

    private String generateIndexHtml(String serviceName, String displayName) throws IOException {
        String template = loadIndexTemplate();

        String html = template
                .replace("./js/", "../../js/")
                .replace("./css/", "../../css/")
                .replace("./img/", "../../img/");

        html = html.replace("<title>", "<title>" + displayName + " - ");

        return html;
    }

    private String loadIndexTemplate() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/META-INF/resources/index-template.html")) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        throw new IOException("No index.html template found in META-INF/resources");
    }

    public String getOutputDirectory() {
        return outputDir;
    }
}

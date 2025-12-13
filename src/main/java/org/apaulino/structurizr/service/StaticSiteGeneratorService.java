package org.apaulino.structurizr.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apaulino.structurizr.dto.ContainerInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.structurizr.Workspace;
import com.structurizr.autolayout.graphviz.GraphvizAutomaticLayout;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;
import com.structurizr.util.WorkspaceUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StaticSiteGeneratorService {

    @ConfigProperty(name = "structurizr.static.base-style.file", defaultValue = "/architecture/styles-base.dsl")
    String baseDslStyles;

    private static final Pattern SOFTWARE_SYSTEM_PATTERN = Pattern
            .compile("(\\w+)\\s*=\\s*softwareSystem\\s+\"([^\"]+)\"", Pattern.MULTILINE);
    private static final Pattern CONTAINER_PATTERN = Pattern.compile(
            "(\\w+)\\s*=\\s*container\\s+\"([^\"]+)\"[^{]*\\{([^}]*component[^}]*)}",
            Pattern.MULTILINE | Pattern.DOTALL);

    public String generateWorkspaceJsFromFragment(String dslFragment, String serviceName) {
        Log.info("Generating workspace.js for service: " + serviceName);

        String completeDsl = wrapFragmentInWorkspace(dslFragment, serviceName);
        return generateWorkspaceJsFromDsl(completeDsl);
    }

    private String generateWorkspaceJsFromDsl(String dsl) {
        Log.info("Generating workspace.js from DSL");
        try {
            Workspace workspace = parseDsl(dsl);
            applyAutolayout(workspace);

            String json = WorkspaceUtils.toJson(workspace, false);
            String base64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            String workspaceJs = "const jsonAsString = '" + base64 + "';";

            Log.info("Successfully generated workspace.js (" + workspaceJs.length() + " bytes)");
            return workspaceJs;
        } catch (Exception e) {
            Log.error("Failed to generate workspace.js: " + e.getMessage(), e);
            throw new RuntimeException("Failed to generate workspace.js", e);
        }
    }

    private String wrapFragmentInWorkspace(String fragment, String serviceName) {
        String systemVarName = extractSystemVarName(fragment);
        String dynamicViews = generateDynamicViews(fragment, systemVarName);
        String baseStyles = loadBaseStyles();

        return """
                workspace "%s" "Documentação de Arquitetura - %s" {
                    !identifiers hierarchical
                    model {
                        %s
                    }
                    views {
                        systemLandscape "Landscape" "Visão geral do sistema" {
                            include *
                            autolayout lr
                        }
                        %s
                        %s
                    }
                }
                """.formatted(serviceName, serviceName, fragment, dynamicViews, baseStyles);
    }

    private String extractSystemVarName(String fragment) {
        Matcher matcher = SOFTWARE_SYSTEM_PATTERN.matcher(fragment);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "system";
    }

    private String generateDynamicViews(String fragment, String systemVarName) {
        StringBuilder views = new StringBuilder();

        views.append(String.format("""
                        container %s "Containers" "Visão de containers" {
                            include *
                            autolayout lr
                        }
                """, systemVarName));

        List<ContainerInfo> containers = extractContainersWithComponents(fragment, systemVarName);
        Log.debug("Found " + containers.size() + " containers with components in fragment");
        
        for (ContainerInfo container : containers) {
            views.append(String.format("""
                            component %s "%sComponents" "Componentes de %s" {
                                include *
                                autolayout lr
                            }
                    """, container.fullRef(), container.name(), container.name()));
        }
        return views.toString();
    }

    private List<ContainerInfo> extractContainersWithComponents(String fragment, String systemVarName) {
        List<ContainerInfo> result = new ArrayList<>();

        if (systemVarName == null || systemVarName.isEmpty()) {
            Log.warn("systemVarName is null or empty, skipping container extraction");
            return result;
        }

        Matcher matcher = CONTAINER_PATTERN.matcher(fragment);
        while (matcher.find()) {
            if (matcher.groupCount() < 3) {
                Log.warn("Regex match has unexpected number of groups: " + matcher.groupCount());
                continue;
            }
            
            String containerVarName = matcher.group(1);
            String containerContent = matcher.group(3);

            if (containerVarName != null && !containerVarName.isEmpty() && 
                containerContent != null && containerContent.contains("component ")) {
                result.add(new ContainerInfo(systemVarName, containerVarName));
                Log.debug("Added container: " + containerVarName);
            }
        }

        return result;
    }

    private Workspace parseDsl(String dsl) throws StructurizrDslParserException {
        Log.debug("Parsing DSL content");

        StructurizrDslParser parser = new StructurizrDslParser();
        parser.parse(dsl);

        Workspace workspace = parser.getWorkspace();
        Log.info("Parsed workspace: " + workspace.getName() +
                " with " + workspace.getModel().getElements().size() + " elements");

        return workspace;
    }

    private void applyAutolayout(Workspace workspace) {
        Log.debug("Applying Graphviz autolayout");

        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            GraphvizAutomaticLayout graphviz = new GraphvizAutomaticLayout(tempDir);
            graphviz.apply(workspace);
            Log.info("Applied autolayout to " + workspace.getViews().getViews().size() + " views");
        } catch (Exception e) {
            Log.warn("Graphviz autolayout failed, views may not be positioned: " + e.getMessage());
        }
    }

    private String loadBaseStyles() {
        try (InputStream inputStream = StaticSiteGeneratorService.class.getResourceAsStream(baseDslStyles)) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ex) {
            Log.error("Failed to load base styles: using empty base styles" + ex.getMessage(), ex);
        }
        return "";
    }
}

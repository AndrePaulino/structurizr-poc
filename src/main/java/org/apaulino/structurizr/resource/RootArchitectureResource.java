package org.apaulino.structurizr.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.apaulino.structurizr.service.ServiceRegistry;
import org.apaulino.structurizr.service.provider.ArchitectureServiceProvider;

import io.quarkus.logging.Log;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

import java.util.List;
import java.util.Optional;

@Path("/")
public class RootArchitectureResource {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance landingPage(List<ArchitectureServiceProvider> services);
    }

    private final ServiceRegistry serviceRegistry;

    public RootArchitectureResource(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getLandingPage() {
        Log.info("Serving landing page at ROOT");

        List<ArchitectureServiceProvider> services = serviceRegistry.getServiceNames().stream()
                .map(name -> serviceRegistry.getServiceInfo(name))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return Templates.landingPage(services);
    }
}

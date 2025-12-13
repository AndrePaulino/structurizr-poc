package org.apaulino.structurizr.resource;

import java.io.IOException;

import org.apaulino.structurizr.service.ArchitectureService;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/architecture")
public class ArchitectureResource {

    private final ArchitectureService architectureService;

    public ArchitectureResource(ArchitectureService architectureService) {
        this.architectureService = architectureService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getAggregatorArchitecture() throws IOException {
        Log.info("Serving aggregator architecture at /api/architecture");
        return Response.ok(architectureService.getAggregatorHtml()).build();
    }

    @GET
    @Path("/services")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listServices() {
        return Response.ok(architectureService.listServices()).build();
    }

    @GET
    @Path("/services/{serviceName}")
    @Produces(MediaType.TEXT_HTML)
    public Response getServicePage(@PathParam("serviceName") String serviceName) throws IOException {
        Log.info("Serving architecture page for service: " + serviceName);
        return Response.ok(architectureService.getServiceHtml(serviceName)).build();
    }

    @GET
    @Path("/services/{serviceName}/workspace.js")
    @Produces("application/javascript")
    public Response getServiceWorkspaceJs(@PathParam("serviceName") String serviceName) {
        Log.info("Generating workspace.js for service: " + serviceName);
        return Response.ok(architectureService.generateServiceWorkspaceJs(serviceName)).build();
    }

    @GET
    @Path("/services/{serviceName}/dsl")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServiceDsl(@PathParam("serviceName") String serviceName) {
        Log.info("Returning DSL for service: " + serviceName);
        return Response.ok(architectureService.getServiceDsl(serviceName)).build();
    }
}
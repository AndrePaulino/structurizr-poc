package org.apaulino.structurizr.resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apaulino.structurizr.service.GeneratorService;
import org.jboss.resteasy.reactive.RestResponse;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/generate")
@Produces(MediaType.APPLICATION_JSON)
public class GeneratorResource {

    private final GeneratorService generatorService;

    public GeneratorResource(GeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    @POST
    public RestResponse<Map<String, Object>> generateAll() {
        Log.info("Request received: Generate all services");
        return RestResponse.ok(generatorService.generateAllServices());
    }

    @POST
    @Path("/{serviceName}")
    public RestResponse<Map<String, Object>> generateService(@PathParam("serviceName") String serviceName)
            throws IOException {
        Log.info("Request received: Generate service " + serviceName);
        return RestResponse.ok(generatorService.generateService(serviceName));
    }

    @GET
    @Path("/services")
    public RestResponse<Map<String, Object>> listAvailableServices() {
        Log.info("Request received: List available services");
        List<String> services = generatorService.getServiceNames();
        return RestResponse.ok(Map.of(
                "status", "success",
                "services", services,
                "count", services.size()));
    }
}

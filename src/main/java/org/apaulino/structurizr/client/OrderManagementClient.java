package org.apaulino.structurizr.client;

import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/architecture")
@RegisterRestClient(configKey = "order-management")
@Singleton
public interface OrderManagementClient {

    @GET
    @Path("/dsl")
    @Produces(MediaType.TEXT_PLAIN)
    String getDslFragment();
}

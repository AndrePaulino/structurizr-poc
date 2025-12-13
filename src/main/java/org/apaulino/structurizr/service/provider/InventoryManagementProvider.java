package org.apaulino.structurizr.service.provider;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.apaulino.structurizr.client.InventoryManagementClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class InventoryManagementProvider implements ArchitectureServiceProvider {

    private final InventoryManagementClient client;

    public InventoryManagementProvider(@RestClient InventoryManagementClient client) {
        this.client = client;
    }

    @Override
    public String getServiceId() {
        return "service-b";
    }

    @Override
    public String getDisplayName() {
        return "Inventory Management";
    }

    @Override
    public String getDescription() {
        return "Sistema de controle de estoque";
    }

    @Override
    public String getDslFragment() {
        Log.info("Fetching DSL from Inventory Management Service");
        return client.getDslFragment();
    }
}

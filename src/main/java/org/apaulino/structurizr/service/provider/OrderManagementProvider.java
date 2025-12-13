package org.apaulino.structurizr.service.provider;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.apaulino.structurizr.client.OrderManagementClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class OrderManagementProvider implements ArchitectureServiceProvider {

    private final OrderManagementClient client;

    public OrderManagementProvider(@RestClient OrderManagementClient client) {
        this.client = client;
    }

    @Override
    public String getServiceId() {
        return "order-management";
    }

    @Override
    public String getDisplayName() {
        return "Order Management";
    }

    @Override
    public String getDescription() {
        return "Sistema de gerenciamento de pedidos";
    }

    @Override
    public String getDslFragment() {
        Log.info("Fetching DSL from Order Management Service");
        return client.getDslFragment();
    }
}

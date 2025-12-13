package org.apaulino.structurizr.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apaulino.structurizr.service.provider.ArchitectureServiceProvider;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;

@ApplicationScoped
public class ServiceRegistry {

    private final Map<String, ArchitectureServiceProvider> providers;

    public ServiceRegistry(Instance<ArchitectureServiceProvider> providerInstances) {
        this.providers = providerInstances.stream()
                .collect(Collectors.toMap(
                        ArchitectureServiceProvider::getServiceId,
                        provider -> provider));
        Log.info("ServiceRegistry initialized with " + providers.size() + " providers: " + providers.keySet());
    }

    public Optional<String> getDslFragment(String serviceName) {
        Optional<ArchitectureServiceProvider> provider = getServiceInfo(serviceName);
        if (!provider.isPresent()) {
            Log.warn("Service not found: " + serviceName);
            return Optional.empty();
        }

        try {
            Log.info("Fetching DSL from service: " + serviceName);
            String dsl = provider.get().getDslFragment();
            return Optional.ofNullable(dsl);
        } catch (Exception e) {
            Log.error("Failed to fetch DSL from " + serviceName + ": " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    public List<String> getServiceNames() {
        return getAvailableServices().stream().toList();
    }
    
    public Set<String> getAvailableServices() {
        return providers.keySet();
    }

    public Optional<ArchitectureServiceProvider> getServiceInfo(String serviceName) {
        return Optional.ofNullable(providers.get(serviceName.toLowerCase()));
    }
}

package org.apaulino.structurizr.dto;

import org.apaulino.structurizr.service.provider.ArchitectureServiceProvider;

public record ServiceInfo(String id, String displayName, String description, String url) {

    public static ServiceInfo from(ArchitectureServiceProvider provider) {
        return new ServiceInfo(
                provider.getServiceId(),
                provider.getDisplayName(),
                provider.getDescription(),
                "/api/architecture/services/" + provider.getServiceId());
    }

    public static ServiceInfo fromServiceName(String serviceName) {
        return new ServiceInfo(
                serviceName,
                serviceName,
                "",
                "/api/architecture/services/" + serviceName);
    }
}

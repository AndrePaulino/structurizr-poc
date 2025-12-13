package org.apaulino.structurizr.service.provider;

public interface ArchitectureServiceProvider {
    String getServiceId();

    String getDisplayName();

    String getDescription();

    String getDslFragment();
}

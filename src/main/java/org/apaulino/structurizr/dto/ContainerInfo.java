package org.apaulino.structurizr.dto;

public record ContainerInfo(String systemVarName, String containerVarName) {
    public String fullRef() {
        return systemVarName + "." + containerVarName;
    }

    public String name() {
        return containerVarName;
    }
}

package org.csanchez.jenkins.plugins.kubernetes.builders;

import io.fabric8.kubernetes.api.model.Pod;

public interface Builder {
    void createInstance();
    void setJobSpec(Pod pod);
    void setEnvs(Pod pod);
    void setLabels(Pod pod);
    void setCodeSource(Pod pod);
    void setMountConfig(Pod pod);
    void setResources(Pod pod);
    void setSideCarContainers(Pod pod);
}

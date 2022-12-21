package org.csanchez.jenkins.plugins.kubernetes.model;

import java.util.Map;

public class RuleConfiguration {
    public Project defaultProject;

    public Map<String,Project>  projectMap;
}

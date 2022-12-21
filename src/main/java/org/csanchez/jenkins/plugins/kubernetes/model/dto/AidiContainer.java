package org.csanchez.jenkins.plugins.kubernetes.model.dto;

import java.util.ArrayList;
import java.util.List;

public class AidiContainer {
    public String name;
    public String image;
    public String command;
    public List<String> args;
    public Resource resource;
    public List<Env> envs;

    public AidiContainer(){
        this.resource=new Resource();
        this.envs=new ArrayList<Env>();
    }
}
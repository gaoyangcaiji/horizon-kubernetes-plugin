package org.csanchez.jenkins.plugins.kubernetes.model.dto;

public class Env {
    public String name;
    public String value;

    public  Env(String name,String value){
        this.name=name;
        this.value=value;
    }
}
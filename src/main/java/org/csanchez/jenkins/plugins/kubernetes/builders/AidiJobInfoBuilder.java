package org.csanchez.jenkins.plugins.kubernetes.builders;
import io.fabric8.kubernetes.api.model.*;
import org.csanchez.jenkins.plugins.kubernetes.model.dto.AidiContainer;
import org.csanchez.jenkins.plugins.kubernetes.model.dto.Env;
import org.csanchez.jenkins.plugins.kubernetes.model.dto.AidiJobInfo;
//import org.csanchez.jenkins.plugins.kubernetes.model.dto.Container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AidiJobInfoBuilder implements Builder{
    private AidiJobInfo aidiJobInfo;

    @Override
    public void createInstance(){
        this.aidiJobInfo=new AidiJobInfo();
    }

    @Override
    public void setJobSpec(Pod pod) {
        //build metadata
        aidiJobInfo.jobName=pod.getMetadata().getName();
        Container jnlpContainer= pod.getSpec().getContainers().stream().filter(u -> u.getName().contains("jnlp")).findFirst().get();
        //aidiJobInfo.command= jnlpContainer.getCommand().toString();
        aidiJobInfo.command= "/usr/local/bin/jenkins-agent";
        aidiJobInfo.jobSpec.image=jnlpContainer.getImage();
        //set default workspace path
        aidiJobInfo.jobSpec.extraPodSpec.sharedVolumeMountPaths.add("/home/jenkins/agent");
    }

    @Override
    public void setEnvs(Pod pod) {
        Container jnlpContainer= pod.getSpec().getContainers().stream().filter(u -> u.getName().contains("jnlp")).findFirst().get();
        List<EnvVar> envList=jnlpContainer.getEnv();
        for (EnvVar item : envList){
            Env envItem= new Env(item.getName(),item.getValue());
            aidiJobInfo.envs.add(envItem);
        }
    }

    @Override
    public void setLabels(Pod pod) {
        Map<String,String> labels= pod.getMetadata().getLabels();
        labels.forEach((key, value) -> {
            HashMap<String, String > labelItem = new HashMap<String, String>(){{
                put("name",key);
                put("value",value);
            }};
            aidiJobInfo.jobSpec.extraPodSpec.podLabels.add(labelItem);
        });
    }

    @Override
    public void setCodeSource(Pod pod) {
        //TODO
    }

    @Override
    public void setMountConfig(Pod pod) {
        //TODO
    }

    @Override
    public void setResources(Pod pod) {
        Container jnlpContainer= pod.getSpec().getContainers().stream().filter(u -> u.getName().contains("jnlp")).findFirst().get();
        Map<String, Quantity> resources=jnlpContainer.getResources().getRequests();
        aidiJobInfo.jobSpec.resource.cpu=Integer.parseInt(resources.get("cpu").getAmount());
        aidiJobInfo.jobSpec.resource.memory=Integer.parseInt(resources.get("memory").getAmount());
    }

    @Override
    public void setSideCarContainers(Pod pod) {
        List<Container> containers=pod.getSpec().getContainers();
        for (Container item : containers) {
            AidiContainer containerItem=new AidiContainer();
            //jnlp作为主容器
            if (item.getName()=="jnlp"){
                continue;
            }

            //set job spec
            containerItem.name=item.getName();
            containerItem.image=item.getImage();
            containerItem.command="sleep 3000";

            //set job resource
            ResourceRequirements resourceRequirements= item.getResources();
            if (resourceRequirements!=null){
                Map<String, Quantity> resources=resourceRequirements.getRequests();
                containerItem.resource.cpu=Integer.parseInt(resources.get("cpu").getAmount());
                containerItem.resource.memory=Integer.parseInt(resources.get("memory").getAmount());
            }

            //set job envs
            List<EnvVar> envList=item.getEnv();
            for (EnvVar envItem : envList){
                Env jobEnvItem= new Env(envItem.getName(),envItem.getValue());
                containerItem.envs.add(jobEnvItem);
            }

            aidiJobInfo.jobSpec.extraPodSpec.sideCarContainers.add(containerItem);
        }
    }


    public AidiJobInfo getResult() {
        return aidiJobInfo;
    }
}

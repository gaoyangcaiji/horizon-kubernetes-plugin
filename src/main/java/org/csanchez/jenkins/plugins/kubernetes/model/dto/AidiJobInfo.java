package org.csanchez.jenkins.plugins.kubernetes.model.dto;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AidiJobInfo {
    public String jobId;
    public String jobName;
    public int jobMaxRunningTimeMinutes=1;
    public int priority=5;
    public String queue="jm-test-dev";
    public String projectId="rds-gpu";
    public String command;
    public List<Env> envs;
    public CodeSource codeSource;
    public MountConfig mountConfig;
    public JobSpec jobSpec;
    public JobStatus jobStatus;

    public AidiJobInfo(){
        this.envs=new ArrayList<Env>();
        this.mountConfig=new MountConfig();
        this.codeSource=new CodeSource();
        this.jobStatus=new JobStatus();
        this.jobSpec=new JobSpec();
    }

    public class CodeSource {
        public List<Git> gits;
    }
    public class MountConfig {
        public List<Bucket> buckets;
    }

    public class Bucket {
        public String name;
        public boolean readonly;
    }

    public class Git {
        public String repo;
        public String branch;
        public String commit;
        public String tag;
    }

    public class JobSpec {
        public int podCount;
        public Resource resource;
        public String image;
        public ExtraPodSpec extraPodSpec;

        public JobSpec(){
            this.podCount=1;
            this.extraPodSpec=new ExtraPodSpec();
            this.resource=new Resource();
        }
    }

    public class ExtraPodSpec {
        public List<Map<String,String>> podLabels;
        public List<String> sharedVolumeMountPaths;
        public List<AidiContainer> sideCarContainers;

        public ExtraPodSpec(){
            this.podLabels=new ArrayList<Map<String,String>>();
            this.sharedVolumeMountPaths=new ArrayList<String>();
            this.sideCarContainers=new ArrayList<AidiContainer>();
        }
    }

    public class JobStatus{
        public  String reason;
        public  String message;
        public  String phase;
        public  int rank;
        public Date createTime;
        public Date submittedTime;
        public Date runningTime;
        public Date finishTime;
        public int runningDuration;
    }
}

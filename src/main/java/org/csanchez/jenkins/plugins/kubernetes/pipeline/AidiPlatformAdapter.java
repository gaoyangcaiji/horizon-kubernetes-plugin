package org.csanchez.jenkins.plugins.kubernetes.pipeline;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Container;
import okhttp3.HttpUrl;
import org.csanchez.jenkins.plugins.kubernetes.builders.AidiJobInfoBuilder;
import org.csanchez.jenkins.plugins.kubernetes.exceptions.BusinessException;
import org.csanchez.jenkins.plugins.kubernetes.model.SCMConfiguration;
import org.csanchez.jenkins.plugins.kubernetes.model.dto.APIResult;
import org.csanchez.jenkins.plugins.kubernetes.model.dto.AidiJobInfo;
import org.csanchez.jenkins.plugins.kubernetes.util.OkHttpUtil;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class AidiPlatformAdapter {
    public static final SCMConfiguration scmConfiguration=initSCMConfiguration();
    public static final Gson GSON = new Gson();
    public static final String AIDI_CREATEJOB_URL="infra/api/v1alpha/job_manager/create_job";
    public static final String AIDI_GETJOB_URL="infra/api/v1alpha/job_manager/get_job?jobId=%s";
    public static final String AIDI_STOPJOB_URL="infra/api/v1alpha/job_manager/stop_job";
    public static final String AIDIJOB_RULE_CONFIGURATION_URL = "https://gitlab.hobot.cc/sdp_public/cicd_pod_config/-/raw/feat-AIDI-12Sp01/SCMConfiguration.json";



    public  static SCMConfiguration initSCMConfiguration(){
        String url = AIDIJOB_RULE_CONFIGURATION_URL;
        Map<String, String> header = new HashMap<>();
        SCMConfiguration result=OkHttpUtil.get(url,header,new TypeToken<SCMConfiguration>(){
        }.getType());
        return  result;
    }


    public static AidiJobInfo transferPodToAidiJob(Pod pod){
        //build aidi job;
        AidiJobInfoBuilder builder=new AidiJobInfoBuilder();
        builder.createInstance();
        builder.setJobSpec(pod);
        builder.setCodeSource(pod);
        builder.setEnvs(pod);
        builder.setLabels(pod);
        builder.setMountConfig(pod);
        builder.setResources(pod);
        builder.setSideCarContainers(pod);
        return builder.getResult();
    }

    public static APIResult<AidiJobInfo> sendJobToAidiPlatform(Pod pod) throws BusinessException {
        AidiJobInfo aidiJobInfo=transferPodToAidiJob(pod);
        String aidiJobInfoJson= GSON.toJson(aidiJobInfo);
        String createJobURL= HttpUrl.parse(scmConfiguration.aidiPlatformURl+AIDI_CREATEJOB_URL).url().toString();
        APIResult<AidiJobInfo> result= OkHttpUtil.post(createJobURL, new HashMap<>(), aidiJobInfoJson, new TypeToken<APIResult<AidiJobInfo>>(){
        }.getType());

        if (result.code!=0){
            throw new BusinessException(String.format("Create aidi job failed for reason: %s",result.msg));
        }

        return  result;
    }

    public static void waitUntilJobReady(String jobId) throws BusinessException, InterruptedException {
        APIResult<AidiJobInfo> aidiJobInfoAPIResult= getAidiJob(jobId);
        String phase=aidiJobInfoAPIResult.data.jobStatus.phase;
        while( !phase.equalsIgnoreCase("running") ) {
            TimeUnit.SECONDS.sleep(3);//秒
            aidiJobInfoAPIResult= getAidiJob(jobId);
            phase=aidiJobInfoAPIResult.data.jobStatus.phase;
        }
    }

    public static APIResult<AidiJobInfo> getAidiJob(String jobId) throws BusinessException {
        String createJobURL= HttpUrl.parse(scmConfiguration.aidiPlatformURl+String.format(AIDI_GETJOB_URL,jobId)).url().toString();
        APIResult<AidiJobInfo> result= OkHttpUtil.get(createJobURL, new HashMap<>(), new TypeToken<APIResult<AidiJobInfo>>(){
        }.getType());

        if (result.code!=0){
            throw new BusinessException(String.format("Get aidi job failed for reason: %s",result.msg));
        }
        return  result;
    }

    public static APIResult<AidiJobInfo> stopAidiJob(String jobId) throws BusinessException {

        String stopJobURL= HttpUrl.parse(scmConfiguration.aidiPlatformURl+AIDI_STOPJOB_URL).url().toString();
        Map<String,String> payload = new HashMap<String,String>();
        payload.put("jobId",jobId);
        APIResult<AidiJobInfo> result= OkHttpUtil.post(stopJobURL, new HashMap<>(), GSON.toJson(payload), new TypeToken<APIResult<AidiJobInfo>>(){
        }.getType());

        if (result==null || result.code!=0){
            throw new BusinessException(String.format("Stop aidi job failed for reason: %s",result.msg));
        }

        return  result;
    }

}

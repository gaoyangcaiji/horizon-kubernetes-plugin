package org.csanchez.jenkins.plugins.kubernetes.exceptions;

public class BusinessException extends Exception{

    public BusinessException(){
        super();
    }


    public BusinessException(String message){
        super(message);
    }


    public BusinessException(String message, Throwable cause){
        super(message,cause);
    }


    public BusinessException(Throwable cause) {
        super(cause);
    }
}

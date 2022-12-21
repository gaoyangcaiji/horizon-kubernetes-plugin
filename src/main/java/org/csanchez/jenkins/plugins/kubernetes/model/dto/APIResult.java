package org.csanchez.jenkins.plugins.kubernetes.model.dto;

import java.io.Serializable;

public class APIResult<T> implements Serializable {

    private static final long serialVersionUID = -8727135259678182645L;

    public int code;

    public String msg;

    public T data;
}
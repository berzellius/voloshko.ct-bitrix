package com.voloshko.ctbitrix.service;

import com.voloshko.ctbitrix.exception.APIAuthException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by berz on 12.03.2016.
 */
public interface APIService {



    public RestTemplate getRestTemplate();

    public <T, TR> T request(String url, HttpMethod method, HttpEntity<TR> request, Class<T> cl)  throws APIAuthException;

    public <T, TR, TE> T request(String url, HttpMethod method, HttpEntity<TR> request, Class<T> cl, Class<TE> exceptionType) throws APIAuthException;

    public  <T, TR, TE> T request(String url, HttpMethod method, HttpEntity<TR> request, RestTemplate rt, Class<T> cl, Class<TE> exceptionType) throws APIAuthException;

    void logIn() throws APIAuthException;

    void reLogin() throws APIAuthException;

}

package com.voloshko.ctbitrix.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voloshko.ctbitrix.dto.api.ErrorHandlers.APIRequestErrorException;
import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIRequest;
import com.voloshko.ctbitrix.exception.APIAuthException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by berz on 12.03.2016.
 */
public abstract class APIServiceRequestsImpl implements APIService {

    protected int reLogins = 0;
    protected int reLoginsMax = 3;

    protected List<String> cookies;

    protected ResponseErrorHandler errorHandler;

    public abstract RestTemplate getRestTemplate();

    @Override
    public  <T, TR> T request(String url, HttpMethod method, HttpEntity<TR> request, Class<T> cl) throws APIAuthException {
        return request(url, method, request, cl, APIRequestErrorException.class);
    }

    @Override
    public  <T, TR, TE> T request(String url, HttpMethod method, HttpEntity<TR> request, Class<T> cl, Class<TE> exceptionType) throws APIAuthException {
        RestTemplate rt;
        switch (method){
            case GET:
                rt = new RestTemplate();
                rt.setErrorHandler(this.errorHandler);
                break;
            default:
                rt = this.getRestTemplate();
                break;
        }

        return request(url, method, request, rt, cl, exceptionType);
    }

    @Override
    public  <T, TR, TE> T request(String url, HttpMethod method, HttpEntity<TR> request, RestTemplate rt, Class<T> cl, Class<TE> exceptionType) throws APIAuthException {
        //ObjectMapper objectMapper = new ObjectMapper();


            //System.out.println("request: " + objectMapper.writeValueAsString(request.getBody()));
            HttpEntity<T> response;
            switch (method){
                case GET:
                    UriComponentsBuilder uriComponentsBuilder = this.uriComponentsBuilderByParams(
                            request.getBody(), url
                    );
                    System.out.println(uriComponentsBuilder.build().encode().toUri());
                    response = rt.exchange(uriComponentsBuilder.build().encode().toUri(), method, this.plainHttpEntity(), cl);
                    break;
                default:
                    response = rt.exchange(
                            url, method, request, cl
                    );
                    break;
            }

            if (response.getHeaders().containsKey("Set-Cookie")) {
                this.cookies = response.getHeaders().get("Set-Cookie");
            }

          //  System.out.println("response: " + objectMapper.writeValueAsString(response.getBody()));

            return (T) response.getBody();
    }

    @Override
    public void reLogin() throws APIAuthException {
        if(this.reLogins >= this.reLoginsMax){
            throw new APIAuthException("Cant authentificate after " + this.reLogins + " times");
        }

        this.reLogins++;

        this.logIn();
    }

    protected UriComponentsBuilder uriComponentsBuilderByParams(Object obj, String url) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);

        if(obj instanceof MultiValueMap){
            for(Object key : ((MultiValueMap) obj).keySet()) {
                uriComponentsBuilder.queryParam(key.toString(), ((MultiValueMap) obj).getFirst(key));
            }
        }
        else {
            List<Field> fields = this.getFields(obj.getClass());
            for (Field f : fields) {
                try {
                    f.setAccessible(true);
                    if (f.get(obj) != null) {
                        uriComponentsBuilder
                                .queryParam(f.getName(), f.get(obj).toString());
                        //params.add(f.getName(), f.get(amoCRMRequest).toString());
                    }
                } catch (IllegalAccessException e) {
                    //e.printStackTrace();
                }
            }
        }

        return uriComponentsBuilder;
    }

    protected HttpEntity<MultiValueMap<String, String>> plainHttpEntity() throws APIAuthException {
        HttpHeaders requestHeaders = new HttpHeaders();
        if(this.cookies != null)
            requestHeaders.add("Cookie", String.join(";", this.cookies));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, requestHeaders);
        return request;
    }

    protected  <T> HttpEntity<T> requestByParamsAbstract(T params){
        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.add("Content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        if(this.cookies != null) {
            requestHeaders.add("Cookie", String.join(";", this.cookies));
        }

        HttpEntity<T> request = new HttpEntity<>(params, requestHeaders);
        return  request;
    }

    protected List<Field> getFields(Class<? extends Object> cl) {
        List<Field> f = new ArrayList<Field>();
        f.addAll(Arrays.asList(cl.getDeclaredFields()));

        Class s = cl.getSuperclass();
        if (s != null) {
            List<Field> sf = getFields(s);
            f.addAll(sf);
        }

        return f;
    }

}

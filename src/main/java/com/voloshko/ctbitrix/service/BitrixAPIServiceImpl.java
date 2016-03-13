package com.voloshko.ctbitrix.service;

import com.voloshko.ctbitrix.dmodel.BitrixRefreshAccess;
import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixRefreshAccessRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixRefreshAccessResponse;
import com.voloshko.ctbitrix.exception.APIAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by berz on 12.03.2016.
 */
@Service
@Transactional
public class BitrixAPIServiceImpl extends  APIServiceRequestsImpl implements BitrixAPIService {

    private String initialRefreshToken;
    private String clientId;
    private String clientSecret;
    private String redirectURI;
    private String refreshGrantType;
    private String defaultScope;

    private String loginUrl;

    private HttpMethod loginMethod;


    @Autowired
    private BitrixRefreshAccessService bitrixRefreshAccessService;

    @Override
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(this.errorHandler);

        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        FormHttpMessageConverter httpMessageConverter = new FormHttpMessageConverter();

        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);

        List<MediaType> formsMediaTypes = new ArrayList<MediaType>();
        formsMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();

        jsonHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        httpMessageConverter.setSupportedMediaTypes(formsMediaTypes);

        messageConverters.add(jsonHttpMessageConverter);
        messageConverters.add(httpMessageConverter);
        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }

    private HttpEntity<BitrixRefreshAccessRequest> requestByParams(BitrixRefreshAccessRequest params){
        return requestByParamsAbstract(params);
    }

    private HttpEntity<MultiValueMap<String, String>> requestByParams(MultiValueMap<String, String> params){
        return requestByParamsAbstract(params);
    }

    @Override
    public void logIn() throws APIAuthException {
        BitrixRefreshAccess bitrixRefreshAccess = bitrixRefreshAccessService.getLastRefreshAccess();

        String refreshToken = (bitrixRefreshAccess != null)? bitrixRefreshAccess.getRefreshToken() : this.initialRefreshToken;

        BitrixRefreshAccessRequest bitrixRefreshAccessRequest = new BitrixRefreshAccessRequest();
        bitrixRefreshAccessRequest.setRefresh_token(refreshToken);
        bitrixRefreshAccessRequest.setClient_id(this.getClientId());
        bitrixRefreshAccessRequest.setClient_secret(this.getClientSecret());
        bitrixRefreshAccessRequest.setGrant_type(this.getRefreshGrantType());
        bitrixRefreshAccessRequest.setScope(this.getDefaultScope());
        bitrixRefreshAccessRequest.setRedirect_uri(this.getRedirectURI());



        HttpEntity<BitrixRefreshAccessRequest> httpEntity = this.requestByParams(bitrixRefreshAccessRequest);

        BitrixRefreshAccessResponse bitrixRefreshAccessResponse = this.request(
                this.getLoginUrl(), this.getLoginMethod(), httpEntity, BitrixRefreshAccessResponse.class
        );

        if(
                bitrixRefreshAccessResponse == null ||
                        (
                                bitrixRefreshAccessResponse.getError() != null &&
                            !bitrixRefreshAccessResponse.getError().equals("")
                        )
                ){
            this.reLogin();
        }
        else{
            // success
            BitrixRefreshAccess bitrixRefreshAccessNew = new BitrixRefreshAccess();
            bitrixRefreshAccessNew.setScope(bitrixRefreshAccessResponse.getScope());
            bitrixRefreshAccessNew.setAccessToken(bitrixRefreshAccessResponse.getAccess_token());
            bitrixRefreshAccessNew.setMemberId(bitrixRefreshAccessResponse.getMember_id());
            bitrixRefreshAccessNew.setDomain(bitrixRefreshAccessResponse.getDomain());
            bitrixRefreshAccessNew.setRefreshToken(bitrixRefreshAccessResponse.getRefresh_token());

            bitrixRefreshAccessService.newRefreshAccess(bitrixRefreshAccessNew);
        }
    }

    public String getInitialRefreshToken() {
        return initialRefreshToken;
    }

    @Override
    public void setInitialRefreshToken(String initialRefreshToken) {
        this.initialRefreshToken = initialRefreshToken;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectURI() {
        return redirectURI;
    }

    @Override
    public void setRedirectURI(String redirectURI) {
        this.redirectURI = redirectURI;
    }

    public String getRefreshGrantType() {
        return refreshGrantType;
    }

    @Override
    public void setRefreshGrantType(String refreshGrantType) {
        this.refreshGrantType = refreshGrantType;
    }

    public String getDefaultScope() {
        return defaultScope;
    }

    @Override
    public void setDefaultScope(String defaultScope) {
        this.defaultScope = defaultScope;
    }

    public ResponseErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public HttpMethod getLoginMethod() {
        return loginMethod;
    }

    @Override
    public void setLoginMethod(HttpMethod loginMethod) {
        this.loginMethod = loginMethod;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    @Override
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }
}

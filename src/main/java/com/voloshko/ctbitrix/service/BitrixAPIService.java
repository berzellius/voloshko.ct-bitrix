package com.voloshko.ctbitrix.service;

import com.voloshko.ctbitrix.exception.APIAuthException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * Created by berz on 12.03.2016.
 */
@Service
public interface BitrixAPIService {

    public void logIn() throws APIAuthException;

    void setInitialRefreshToken(String initialRefreshToken);

    void setClientId(String clientId);

    void setClientSecret(String clienSecret);

    void setRedirectURI(String redirectURI);

    void setRefreshGrantType(String grantType);

    void setDefaultScope(String defaultScope);

    void setErrorHandler(ResponseErrorHandler errorHandler);

    void setLoginMethod(HttpMethod loginMethod);

    void setLoginUrl(String loginUrl);
}

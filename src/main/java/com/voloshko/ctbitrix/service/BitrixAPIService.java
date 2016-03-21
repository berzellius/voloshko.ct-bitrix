package com.voloshko.ctbitrix.service;

import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMEntity;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMLiveFeedMessage;
import com.voloshko.ctbitrix.dto.api.bitrix.functions.BitrixAPIFunction;
import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIFindByCommunicationRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIFindByCommunicationResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIResponse;
import com.voloshko.ctbitrix.exception.APIAuthException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * Created by berz on 12.03.2016.
 */
@Service
public interface BitrixAPIService {


    <TResp extends BitrixAPIResponse, TFuncResp> TFuncResp callCrmFunction(BitrixAPIFunction bitrixAPIFunction, Class<TResp> responseClass, Class<TFuncResp> funcRespClass) throws APIAuthException;

    BitrixAPIFindByCommunicationResponse.Result findByCommunication(BitrixAPIFindByCommunicationRequest request) throws APIAuthException;

    void testCrmFunction2() throws APIAuthException;

    Long postMessageInLiveFeed(String title, String message, BitrixCRMLiveFeedMessage.EntityType entityType, Long id) throws APIAuthException;

    void testCrmFunction1() throws APIAuthException;

    Long createBitrixCRMEntity(BitrixCRMEntity bitrixCRMEntity) throws APIAuthException;

    void testCrmFunction() throws APIAuthException;

    String getAuth() throws APIAuthException;

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

    void setFunctionsUrl(String functionsUrl);
}

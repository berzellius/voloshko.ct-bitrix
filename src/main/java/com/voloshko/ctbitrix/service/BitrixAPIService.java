package com.voloshko.ctbitrix.service;

import com.voloshko.ctbitrix.dto.api.bitrix.entity.*;
import com.voloshko.ctbitrix.dto.api.bitrix.functions.BitrixAPIFunction;
import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIFindByCommunicationRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIListRequest;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIFindByCommunicationResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIResponse;
import com.voloshko.ctbitrix.exception.APIAuthException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;

import java.util.ArrayList;

/**
 * Created by berz on 12.03.2016.
 */
@Service
public interface BitrixAPIService {


    void attach(BitrixCRMEntityWithID bitrixCRMEntityWithID);

    void flush() throws APIAuthException;

    <TResp extends BitrixAPIResponse, TFuncResp> TFuncResp callCrmFunction(BitrixAPIFunction bitrixAPIFunction, Class<TResp> responseClass, Class<TFuncResp> funcRespClass) throws APIAuthException;

    BitrixAPIFindByCommunicationResponse.Result findByCommunication(BitrixAPIFindByCommunicationRequest request) throws APIAuthException;

    Long postMessageInLiveFeed(String title, String message, BitrixCRMLiveFeedMessage.EntityType entityType, Long id) throws APIAuthException;

    void updateBitrixCRMEntity(BitrixCRMEntityWithID bitrixCRMEntity) throws APIAuthException;

    Long createBitrixCRMEntity(BitrixCRMEntity bitrixCRMEntity) throws APIAuthException;

    void testCrmFunction() throws APIAuthException;

    BitrixCRMLead getLeadByID(Long id) throws APIAuthException;

    BitrixCRMContact getContactByID(Long id) throws APIAuthException;

    BitrixCRMDeal getDealByID(Long id) throws APIAuthException;

    ArrayList<BitrixCRMDeal> getDealsByRequest(BitrixAPIListRequest request) throws APIAuthException;

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

    void updateInitialTokens(String refreshCode, String accessCode, String pass);
}

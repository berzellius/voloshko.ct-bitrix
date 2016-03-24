package com.voloshko.ctbitrix.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voloshko.ctbitrix.dmodel.BitrixRefreshAccess;
import com.voloshko.ctbitrix.dto.api.ErrorHandlers.APIRequestErrorException;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMEntity;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMError;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMLead;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.BitrixCRMLiveFeedMessage;
import com.voloshko.ctbitrix.dto.api.bitrix.functions.*;
import com.voloshko.ctbitrix.dto.api.bitrix.params.*;
import com.voloshko.ctbitrix.dto.api.bitrix.request.*;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIFindByCommunicationResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixRefreshAccessResponse;
import com.voloshko.ctbitrix.exception.APIAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

/**
 * Created by berz on 12.03.2016.
 */
@Service
@Transactional
public class BitrixAPIServiceImpl extends APIServiceRequestsImpl implements BitrixAPIService {

    private String initialRefreshToken;
    private String clientId;
    private String clientSecret;
    private String redirectURI;
    private String refreshGrantType;
    private String defaultScope;

    private String loginUrl;
    private String functionsUrl;

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
    public <TResp extends BitrixAPIResponse, TFuncResp> TFuncResp callCrmFunction(BitrixAPIFunction bitrixAPIFunction, Class<TResp> responseClass, Class<TFuncResp> funcRespClass) throws APIAuthException {
        if(bitrixAPIFunction.getRequest() == null){
            throw new IllegalArgumentException("function " + bitrixAPIFunction.getName() + " has empty 'request' field");
        }

        bitrixAPIFunction.getRequest().setAuth(this.getAuth());

        if(!bitrixAPIFunction.correct()){
            String msg = "Incorrect function state. ";
            if(bitrixAPIFunction.getRequest().getIncorrectMessage() != null){
                msg.concat(bitrixAPIFunction.getRequest().getIncorrectMessage());
            }
            throw new IllegalStateException(msg);
        }

        String url = this.getFunctionsUrl().concat(bitrixAPIFunction.getName());
        MultiValueMap<String, String> map = bitrixAPIFunction.getRequest().entityFieldsToMultiValueMap();
        map.add("auth", bitrixAPIFunction.getRequest().getAuth());

        HttpEntity<MultiValueMap<String, String>> httpEntity = this.requestByParams(map);
        try {
            TResp bitrixAPIResponse = (TResp) this.request(
                    url,
                    HttpMethod.GET,
                    httpEntity,
                    responseClass
            );

            if(bitrixAPIResponse.getError() != null){
                String errorMsg = bitrixAPIResponse.getError();
                if(bitrixAPIResponse.getError_description() != null){
                    errorMsg.concat(" ").concat(bitrixAPIResponse.getError_description());
                }

                throw new RuntimeException(errorMsg);
            }

            if(bitrixAPIResponse.getResult() == null){
                throw new RuntimeException("Bitrix API Function '" + bitrixAPIFunction.getName() + "' , called with parameters: " + bitrixAPIFunction.getRequest() + " returned nothing");
            }

            if(funcRespClass.isAssignableFrom(bitrixAPIResponse.getResult().getClass())){
                return (TFuncResp) bitrixAPIResponse.getResult();
            }
            else{
                throw new RuntimeException("Bitrix API Function '" + bitrixAPIFunction.getName() + "' , returned object (" + bitrixAPIResponse.getResult().getClass() + ") class instead of (" + funcRespClass.getName() + ") class");
            }
        }
        catch(APIRequestErrorException e){
            if(
                    e.getParams().containsKey("code") &&
                            e.getParams().get("code").equals("401") &&
                            e.getParams().containsKey("body")
                    ){
                try {
                    ObjectMapper om = new ObjectMapper();
                    BitrixCRMError error = om.readValue((String) e.getParams().get("body"), BitrixCRMError.class);
                    System.out.println("seems to be login error");
                    if(error.getError().equals("expired_token")) {
                        System.out.println("Okay, it is login error");
                        this.logIn();
                        return this.callCrmFunction(bitrixAPIFunction, responseClass, funcRespClass);
                    }
                    else{
                        System.out.println("No, it is not login error!");
                    }
                } catch (IOException e1) {
                    // nothing to do
                }
            }

            throw e;
        }
    }

    @Override
    public BitrixAPIFindByCommunicationResponse.Result findByCommunication(BitrixAPIFindByCommunicationRequest request) throws APIAuthException {
        BitrixAPICRMFindByCommunicationFunction bitrixAPICRMFindByCommunicationFunction = new BitrixAPICRMFindByCommunicationFunction();
        bitrixAPICRMFindByCommunicationFunction.setRequest(request);

        BitrixAPIFindByCommunicationResponse.Result result =
                this.callCrmFunction(
                        bitrixAPICRMFindByCommunicationFunction,
                        bitrixAPICRMFindByCommunicationFunction.getResponseClass(),
                        BitrixAPIFindByCommunicationResponse.Result.class
                );

        return result;
    }

    public void testCrmFunction2() throws APIAuthException {
        BitrixAPIFindByCommunicationResponse.Result result = this.findByCommunication(
                BitrixAPIFindByCommunicationRequest.getInstance()
                .entityType(BitrixAPIFindByCommunicationRequest.EntityType.LEAD)
                .type(BitrixAPIFindByCommunicationRequest.Type.PHONE)
                .values("89111234567", "89007654321")
        );

        if(result.getLead() != null && result.getLead().size() > 0){
            for(Long leadId : result.getLead()){
                this.postMessageInLiveFeed("Появился новый лид(из API)", "Сообщение о появлении лида", BitrixCRMLiveFeedMessage.EntityType.LEAD, leadId);
            }
        }

        System.out.println("leads" + result.getLead());
        System.out.println("companies " + result.getCompany());
        System.out.println("contacts " + result.getContact());
    }

    @Override
    public Long postMessageInLiveFeed(String title, String message, BitrixCRMLiveFeedMessage.EntityType entityType, Long id) throws APIAuthException {
        BitrixAPICRMPostMessageInLiveFeedFunction bitrixAPICRMPostMessageInLiveFeedFunction = new BitrixAPICRMPostMessageInLiveFeedFunction();
        BitrixAPIPostMessageInLiveFeedFunctionRequest bitrixAPIPostMessageInLiveFeedFunctionRequest = new BitrixAPIPostMessageInLiveFeedFunctionRequest();
        bitrixAPIPostMessageInLiveFeedFunctionRequest.setFields(new BitrixCRMLiveFeedMessage(title, message, entityType, id));
        bitrixAPICRMPostMessageInLiveFeedFunction.setRequest(bitrixAPIPostMessageInLiveFeedFunctionRequest);

        Long msgId = this.callCrmFunction(bitrixAPICRMPostMessageInLiveFeedFunction, bitrixAPICRMPostMessageInLiveFeedFunction.getResponseClass(), Long.class);
        return msgId;
    }

    @Override
    public void testCrmFunction1() throws APIAuthException {
        /*BitrixAPICRMAddEntityFunction bitrixAPICRMAddEntityFunction = new BitrixAPICRMAddEntityFunction();
        BitrixAPIAddEntityRequest bitrixAPIAddEntityRequest = new BitrixAPIAddEntityRequest();

        BitrixCRMLead bitrixCRMLead = new BitrixCRMLead();
        bitrixCRMLead.setTitle("New lead generated from API#2");
        bitrixCRMLead.setSource_id("2");
        bitrixCRMLead.setAssigned_by_id(1l);
        //bitrixCRMLead.setPhone(MultiValueEntityField.arrayWithOneInstance(null, "WORK", "89111234567", null));
        bitrixCRMLead.setPhone(
                MultiValueEntityField.arrayList(
                        MultiValueEntityField.newInstance(null, "WORK", "89111234567", null),
                        MultiValueEntityField.newInstance(null, "WORK", "89007654321", null)
                ));

        bitrixAPIAddEntityRequest.setFields(bitrixCRMLead);
        bitrixAPICRMAddEntityFunction.setRequest(bitrixAPIAddEntityRequest);
        bitrixAPICRMAddEntityFunction.setName("crm.lead.add");

        Long idCreated = this.callCrmFunction(bitrixAPICRMAddEntityFunction, bitrixAPICRMAddEntityFunction.getResponseClass(), Long.class);
        System.out.println("Created entity: ".concat(idCreated.toString()));*/
        Long id = this.createBitrixCRMEntity(
                BitrixCRMLead.newInstance()
                        .title("Test lead created by API#1")
                        .assignedByID(1l)
                                // == Интернет-реклама
                        .sourceID("84996537185")
                        .phones(
                                MultiValueEntityField.arrayList(
                                        MultiValueEntityField.newInstance(null, "WORK", "89111234567", null),
                                        MultiValueEntityField.newInstance(null, "WORK", "89007654321", null)
                                )
                        )
                        .marketingChannel("Яндекс.SEO")
        );
        System.out.println("Created entity: ".concat(id.toString()));
    }

    @Override
    public Long createBitrixCRMEntity(BitrixCRMEntity bitrixCRMEntity) throws APIAuthException {
        BitrixAPICRMAddEntityFunction bitrixAPICRMAddEntityFunction = new BitrixAPICRMAddEntityFunction();
        BitrixAPIAddEntityRequest bitrixAPIAddEntityRequest = new BitrixAPIAddEntityRequest();

        String functionName = BitrixAPICRMAddEntityFunction.getFunctionNameByEntity(bitrixCRMEntity);
        if(functionName == null){
            throw new IllegalArgumentException("i cant work with bitrix enitity of type [".concat(bitrixCRMEntity.getClass().getName()).concat("]"));
        }

        bitrixAPIAddEntityRequest.setFields(bitrixCRMEntity);
        bitrixAPICRMAddEntityFunction.setName(functionName);
        bitrixAPICRMAddEntityFunction.setRequest(bitrixAPIAddEntityRequest);
        Long idCreated = this.callCrmFunction(bitrixAPICRMAddEntityFunction, bitrixAPICRMAddEntityFunction.getResponseClass(), Long.class);
        return idCreated;
    }

    @Override
    public void testCrmFunction() throws APIAuthException {
        BitrixAPICRMLeadListFunction bitrixAPICRMLeadListFunction = new BitrixAPICRMLeadListFunction();
        BitrixAPIListRequest bitrixAPIListRequest = new BitrixAPIListRequest();
        bitrixAPIListRequest
                //.filterOne("ID", 19l)
                //.filterFrom("ID", 11l, RangeEntityField.BoundType.STRICT)
                //.filterTo("ID", 37l, RangeEntityField.BoundType.STRICT)
                .range("ID", 6910l, 6920l, RangeEntityField.BoundType.UNSTRICT, RangeEntityField.BoundType.UNSTRICT)
                .select("ID", "TITLE", "SOURCE_ID", "PHONE")
                .sort("ID", SortEntityField.Direction.DESC);


        bitrixAPICRMLeadListFunction.setRequest(bitrixAPIListRequest);
        ArrayList<BitrixCRMLead> response = this.callCrmFunction(bitrixAPICRMLeadListFunction, bitrixAPICRMLeadListFunction.getResponseClass(), ArrayList.class);

        for(BitrixCRMLead bitrixCRMLead : response){
            System.out.println("lead#" + bitrixCRMLead.getId() + " :: " + bitrixCRMLead);
        }
    }

    @Override
    public String getAuth() throws APIAuthException {
        BitrixRefreshAccess bitrixRefreshAccess = bitrixRefreshAccessService.getLastRefreshAccess();
        if(bitrixRefreshAccess == null){
            logIn();
            return getAuth();
        }

        return bitrixRefreshAccess.getAccessToken();
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

    public String getFunctionsUrl() {
        return functionsUrl;
    }

    @Override
    public void setFunctionsUrl(String functionsUrl) {
        this.functionsUrl = functionsUrl;
    }
}

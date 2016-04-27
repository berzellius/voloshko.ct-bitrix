package com.voloshko.ctbitrix.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voloshko.ctbitrix.dmodel.BitrixRefreshAccess;
import com.voloshko.ctbitrix.dto.api.ErrorHandlers.APIRequestErrorException;
import com.voloshko.ctbitrix.dto.api.bitrix.annotations.RequireByDefault;
import com.voloshko.ctbitrix.dto.api.bitrix.entity.*;
import com.voloshko.ctbitrix.dto.api.bitrix.functions.*;
import com.voloshko.ctbitrix.dto.api.bitrix.params.*;
import com.voloshko.ctbitrix.dto.api.bitrix.request.*;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIFindByCommunicationResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixAPIResponse;
import com.voloshko.ctbitrix.dto.api.bitrix.response.BitrixRefreshAccessResponse;
import com.voloshko.ctbitrix.exception.APIAuthException;
import com.voloshko.ctbitrix.utils.ClassUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;



import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by berz on 12.03.2016.
 */
@Service
@PropertySource("classpath:bitrix.properties")
public class BitrixAPIServiceImpl extends APIServiceRequestsImpl implements BitrixAPIService {

    @Value("${bitrix.initial_refresh_key}")
    private String initialRefreshToken;
    @Value("${bitrix.initial_refresh_key_upd_pass}")
    private String updateTokenPass;
    private String clientId;
    private String clientSecret;
    private String redirectURI;
    private String refreshGrantType;
    private String defaultScope;

    private String loginUrl;
    private String functionsUrl;

    private HttpMethod loginMethod;

    private HashSet<BitrixCRMEntityWithID> attachedEntities = new HashSet<>();

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BitrixAPIService.class);

    @Autowired
    private BitrixRefreshAccessService bitrixRefreshAccessService;

    @Override
    public void attach(BitrixCRMEntityWithID bitrixCRMEntityWithID){
        if(bitrixCRMEntityWithID.getId() != null && !this.attachedEntities.contains(bitrixCRMEntityWithID)){
            log.info("Attaching entity of class `"
                    .concat(bitrixCRMEntityWithID.getClass().getName())
                    .concat("` #")
                    .concat(bitrixCRMEntityWithID.getId().toString())
            );
            this.attachedEntities.add(bitrixCRMEntityWithID);
        }
    }

    @Override
    public void flush() throws APIAuthException {
        log.info("flushing entities from BitrixAPIService");
        for(BitrixCRMEntityWithID bitrixCRMEntityWithID : this.attachedEntities){
            log.info(
                    "flushing "
                            .concat(bitrixCRMEntityWithID.getClass().getName())
                            .concat("#")
                            .concat(bitrixCRMEntityWithID.getId().toString())
            );
            this.updateBitrixCRMEntity(bitrixCRMEntityWithID);
        }

        this.attachedEntities = new HashSet<>();
    }

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

        if(BitrixAPIListRequest.class.isAssignableFrom(bitrixAPIFunction.getRequest().getClass())){
            // class of bitrixAPIFunction.getRequest() instance of BitrixAPIListRequest
            BitrixAPIListRequest req = (BitrixAPIListRequest) bitrixAPIFunction.getRequest();

            if(req.emptySelect()){
                Field[] fields = ClassUtil.getAnnotatedDeclaredFields(
                        ((BitrixAPIListRequest) bitrixAPIFunction.getRequest()).getEntityType(),
                        RequireByDefault.class,
                        true
                );

                List<String> select = new ArrayList<>();
                for(Field f : fields){
                    select.add(f.getName().toUpperCase());
                }
                if(select.size() > 0) {
                    String[] s = (String[]) select.toArray(new String[select.size()]);
                    ((BitrixAPIListRequest) bitrixAPIFunction.getRequest()).select(s);
                }
            }
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
                    if(error.getError().equals("expired_token")) {
                        this.logIn();
                        return this.callCrmFunction(bitrixAPIFunction, responseClass, funcRespClass);
                    }
                    else{
                        System.out.println("Error in request!");
                        e.printStackTrace();
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


    @Override
    public Long postMessageInLiveFeed(String title, String message, BitrixCRMLiveFeedMessage.EntityType entityType, Long id) throws APIAuthException {
        BitrixAPICRMPostMessageInLiveFeedFunction bitrixAPICRMPostMessageInLiveFeedFunction = new BitrixAPICRMPostMessageInLiveFeedFunction();
        BitrixAPIPostMessageInLiveFeedFunctionRequest bitrixAPIPostMessageInLiveFeedFunctionRequest = new BitrixAPIPostMessageInLiveFeedFunctionRequest();
        bitrixAPIPostMessageInLiveFeedFunctionRequest.setFields(new BitrixCRMLiveFeedMessage(title, message, entityType, id));
        bitrixAPICRMPostMessageInLiveFeedFunction.setRequest(bitrixAPIPostMessageInLiveFeedFunctionRequest);

        Long msgId = this.callCrmFunction(bitrixAPICRMPostMessageInLiveFeedFunction, bitrixAPICRMPostMessageInLiveFeedFunction.getResponseClass(), Long.class);
        return msgId;
    }

    /*
    public void testCrmFunction1() throws APIAuthException {
        Long id = this.createBitrixCRMEntity(
                BitrixCRMLead.newInstance()
                        .marketingChannel("Яндекс.SEO")
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
        );
        System.out.println("Created entity: ".concat(id.toString()));
    }*/

    @Override
    public void updateBitrixCRMEntity(BitrixCRMEntityWithID bitrixCRMEntity) throws APIAuthException {
        BitrixAPICRMUpdateEntityFunction bitrixAPICRMUpdateEntityFunction = new BitrixAPICRMUpdateEntityFunction();
        BitrixAPIUpdateEntityRequest bitrixAPIUpdateEntityRequest = new BitrixAPIUpdateEntityRequest();

        SimpleEntityField id = new SimpleEntityField();
        id.setValue(bitrixCRMEntity.getId());
        bitrixAPIUpdateEntityRequest.setId(id);

        String functionName = BitrixAPICRMUpdateEntityFunction.getFunctionNameByEntity(bitrixCRMEntity);
        if(functionName == null){
            throw new IllegalArgumentException("i cant work with bitrix enitity of type [".concat(bitrixCRMEntity.getClass().getName()).concat("]"));
        }
        bitrixCRMEntity.setId(null);
        bitrixAPIUpdateEntityRequest.setFields(bitrixCRMEntity);
        bitrixAPICRMUpdateEntityFunction.setName(functionName);
        bitrixAPICRMUpdateEntityFunction.setRequest(bitrixAPIUpdateEntityRequest);

        this.callCrmFunction(bitrixAPICRMUpdateEntityFunction, bitrixAPICRMUpdateEntityFunction.getResponseClass(), Boolean.class);
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
        BitrixAPIListRequest bitrixAPIListRequest = new BitrixAPIListRequest(BitrixCRMLead.class);
        bitrixAPIListRequest
                //.filterOne("ID", 19l)
                //.filterFrom("ID", 11l, RangeEntityField.BoundType.STRICT)
                //.filterTo("ID", 37l, RangeEntityField.BoundType.STRICT)
                .range("ID", 6910l, 6920l, RangeEntityField.BoundType.UNSTRICT, RangeEntityField.BoundType.UNSTRICT)
                //.select("ID", "TITLE", "SOURCE_ID", "PHONE")
                .sort("ID", SortEntityField.Direction.DESC);


        bitrixAPICRMLeadListFunction.setRequest(bitrixAPIListRequest);
        ArrayList<BitrixCRMLead> response = this.callCrmFunction(bitrixAPICRMLeadListFunction, bitrixAPICRMLeadListFunction.getResponseClass(), ArrayList.class);

        for(BitrixCRMLead bitrixCRMLead : response){
            System.out.println("lead#" + bitrixCRMLead.getId() + " :: " + bitrixCRMLead);
        }
    }

    @Override
    public BitrixCRMLead getLeadByID(Long id) throws APIAuthException {
        BitrixAPICRMLeadListFunction bitrixAPICRMLeadListFunction = new BitrixAPICRMLeadListFunction();
        BitrixAPIListRequest bitrixAPIListRequest = new BitrixAPIListRequest(BitrixCRMLead.class);
        bitrixAPIListRequest.filterOne("ID", id);
        bitrixAPICRMLeadListFunction.setRequest(bitrixAPIListRequest);

        ArrayList<BitrixCRMLead> response = this.callCrmFunction(bitrixAPICRMLeadListFunction, bitrixAPICRMLeadListFunction.getResponseClass(), ArrayList.class);
        if(response == null || response.size() == 0){
            return null;
        }

        this.attach(response.get(0));

        return response.get(0);
    }

    @Override
    public BitrixCRMContact getContactByID(Long id) throws APIAuthException {
        BitrixAPICRMContactListFunction bitrixAPICRMContactListFunction = new BitrixAPICRMContactListFunction();
        BitrixAPIListRequest bitrixAPIListRequest = new BitrixAPIListRequest(BitrixCRMContact.class);
        bitrixAPIListRequest.filterOne("ID", id);
        bitrixAPICRMContactListFunction.setRequest(bitrixAPIListRequest);

        ArrayList<BitrixCRMContact> response = this.callCrmFunction(bitrixAPICRMContactListFunction, bitrixAPICRMContactListFunction.getResponseClass(), ArrayList.class);
        if(response == null || response.size() == 0){
            return null;
        }

        this.attach(response.get(0));

        return response.get(0);
    }

    @Override
    public BitrixCRMDeal getDealByID(Long id) throws APIAuthException {
        List<BitrixCRMDeal> deals = this.getDealsByRequest(BitrixAPIListRequest.newInstance(BitrixCRMDeal.class).filterOne("ID", id));
        if(deals != null && deals.size() > 0){
            this.attach(deals.get(0));
            return deals.get(0);
        }
        else{
            return null;
        }
    }

    @Override
    public ArrayList<BitrixCRMDeal> getDealsByRequest(BitrixAPIListRequest request) throws APIAuthException {
        BitrixAPICRMDealListFunction bitrixAPICRMDealListFunction = new BitrixAPICRMDealListFunction();
        bitrixAPICRMDealListFunction.setRequest(request);

        ArrayList<BitrixCRMDeal> response = this.callCrmFunction(bitrixAPICRMDealListFunction, bitrixAPICRMDealListFunction.getResponseClass(), ArrayList.class);
        if(response == null || response.size() == 0){
            return null;
        }

        for(BitrixCRMDeal deal : response){
            this.attach(deal);
        }

        return response;
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
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = RuntimeException.class)
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

    @Override
    @Transactional
    public void updateInitialTokens(String refreshCode, String accessCode, String pass) {
        if(pass.equals(this.getUpdateTokenPass())){
            this.setInitialRefreshToken(refreshCode);
            bitrixRefreshAccessService.addTokens(accessCode, refreshCode);
        }
        else{
            throw new IllegalArgumentException("wrong pass");
        }
    }

    public String getUpdateTokenPass() {
        return updateTokenPass;
    }

    public void setUpdateTokenPass(String updateTokenPass) {
        this.updateTokenPass = updateTokenPass;
    }
}

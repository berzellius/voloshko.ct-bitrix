package com.voloshko.ctbitrix.web;

import com.voloshko.ctbitrix.exception.APIAuthException;
import com.voloshko.ctbitrix.repository.CallRepository;
import com.voloshko.ctbitrix.service.BitrixAPIService;
import com.voloshko.ctbitrix.service.CallTrackingAPIService;
import com.voloshko.ctbitrix.service.CallTrackingSourceConditionService;
import com.voloshko.ctbitrix.service.CtBitrixBusinessLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;

/**
 * Created by berz on 20.09.2015.
 */
@Controller
@RequestMapping(value = "/")
public class IndexController extends BaseController {

    @Autowired
    CallTrackingAPIService callTrackingAPIService;

    @Autowired
    CallRepository callRepository;

    @Autowired
    CallTrackingSourceConditionService callTrackingSourceConditionService;

    @Autowired
    BitrixAPIService bitrixAPIService;

    @Autowired
    CtBitrixBusinessLogicService ctBitrixBusinessLogicService;

    @RequestMapping(value = "update_conditions")
    public String updCond(){
        try {
            callTrackingAPIService.updateMarketingChannelsFromCalltracking();
        } catch (APIAuthException e) {
            e.printStackTrace();
        }
        return "index";
    }

    @RequestMapping
    public String indexPage(
            Model model
            //,String code,
            //String domain
    ) throws ParseException {
            //model.addAttribute("code", code);
            //model.addAttribute("domain", domain);

        /*Call call = callRepository.findOne(29l);

        if(call != null && call.getState().equals(Call.State.NEW)){
            try {
                ctBitrixBusinessLogicService.processCall(call);
            } catch (APIAuthException e) {
                e.printStackTrace();
            }
        }*/
        try {
            bitrixAPIService.testCrmFunction();
        } catch (APIAuthException e) {
            e.printStackTrace();
        }

        return "index";
    }

    @RequestMapping(value="bitrix_update_initial")
    public String updBitrixInit(
            Model model,
            @RequestParam("refresh")
            String refresh_token,
            @RequestParam("access")
            String access_token,
            @RequestParam("pass")
            String pass
    ){
        try {
            bitrixAPIService.updateInitialTokens(refresh_token, access_token, pass);
            model.addAttribute("msg", "success");
        }
        catch(IllegalArgumentException e){
            model.addAttribute("msg", e.getMessage());
        }
        return "upd";
    }


}

package com.voloshko.ctbitrix.web;

import com.voloshko.ctbitrix.dto.api.bitrix.request.BitrixAPIRequest;
import com.voloshko.ctbitrix.exception.APIAuthException;
import com.voloshko.ctbitrix.repository.CallRepository;
import com.voloshko.ctbitrix.service.BitrixAPIService;
import com.voloshko.ctbitrix.service.CallTrackingAPIService;
import com.voloshko.ctbitrix.service.CallTrackingSourceConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping
    public String indexPage(
            Model model
            //,String code,
            //String domain
    ) throws ParseException {
            //model.addAttribute("code", code);
            //model.addAttribute("domain", domain);

        try {
            bitrixAPIService.logIn();
        } catch (APIAuthException e) {
            System.out.println("login error");
            e.printStackTrace();
        }

        return "index";
    }

}

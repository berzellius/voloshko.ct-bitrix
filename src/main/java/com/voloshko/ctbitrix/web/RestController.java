package com.voloshko.ctbitrix.web;

import com.voloshko.ctbitrix.dto.site.evrika.Lead;
import com.voloshko.ctbitrix.dto.site.evrika.LeadRequest;
import com.voloshko.ctbitrix.dto.site.evrika.Result;
import com.voloshko.ctbitrix.service.CallsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by berz on 27.03.2016.
 */
@org.springframework.web.bind.annotation.RestController
@RequestMapping("/rest/")
public class RestController extends BaseController {

    @Autowired
    CallsService callsService;

    @RequestMapping(
            value = "lead_from_site",
            method = RequestMethod.POST,
            consumes="application/json",
            produces="application/json"
    )
    @ResponseBody
    public Result newLeadFromSite(
            @RequestBody
            LeadRequest leadRequest
    ){
        return callsService.newLeadFromSite(leadRequest.getLeads(), leadRequest.getOrigin(), leadRequest.getPassword());
    }
}

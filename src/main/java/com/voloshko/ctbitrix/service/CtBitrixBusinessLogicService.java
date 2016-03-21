package com.voloshko.ctbitrix.service;

import com.voloshko.ctbitrix.dmodel.Call;
import com.voloshko.ctbitrix.exception.APIAuthException;
import org.springframework.stereotype.Service;

/**
 * Created by berz on 21.03.2016.
 */
@Service
public interface CtBitrixBusinessLogicService {
    void processCall(Call call) throws APIAuthException;
}

package com.voloshko.ctbitrix.config;


import com.voloshko.ctbitrix.dto.api.ErrorHandlers.CalltrackingAPIRequestErrorHandler;
import com.voloshko.ctbitrix.interceptors.AddTemplatesDataInterceptor;
import com.voloshko.ctbitrix.service.*;
import com.voloshko.ctbitrix.settings.LocalProjectSettings;
import com.voloshko.ctbitrix.settings.ProjectSettings;
import com.voloshko.ctbitrix.settings.RemoteProjectSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by berz on 20.10.14.
 */
@Configuration
public class ServiceBeanConfiguration {


    @Bean
    AddTemplatesDataInterceptor addTemplatesDataInterceptor(){
        return new AddTemplatesDataInterceptor();
    }


    @Bean
    public ReloadableResourceBundleMessageSource messageSource(){
        ReloadableResourceBundleMessageSource messageSource=new ReloadableResourceBundleMessageSource();
        String[] resources = {"classpath:/labels","classpath:/message"};
        messageSource.setBasenames(resources);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor(){
        LocaleChangeInterceptor localeChangeInterceptor=new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("locale");
        return localeChangeInterceptor;
    }

    @Bean
    public SessionLocaleResolver sessionLocaleResolver(){
        SessionLocaleResolver localeResolver=new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("ru","RU"));
        return localeResolver;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("UTF-8");
        commonsMultipartResolver.setMaxUploadSize(10 * 1024 * 1024);

        return commonsMultipartResolver;
    }

    @Bean
    public ProjectSettings projectSettings(){
        // Локальный сервер
        return new LocalProjectSettings();
        // Боевой сервер
        //return new RemoteProjectSettings();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsServiceImpl();
    }

    @Bean
    public CallTrackingAPIService callTrackingAPIService(){
        CallTrackingAPIService callTrackingAPIService = new CallTrackingAPIServiceImpl();

        callTrackingAPIService.setApiMethod(HttpMethod.POST);
        callTrackingAPIService.setApiURL("https://calltracking.ru/api/get_data.php");

        callTrackingAPIService.setLoginURL("https://calltracking.ru/api/login.php");
        callTrackingAPIService.setLoginMethod(HttpMethod.POST);

        callTrackingAPIService.setLogin("info@home-motion.ru");
        callTrackingAPIService.setPassword("");
        callTrackingAPIService.setWebSiteLogin("info@home-motion.ru");
        callTrackingAPIService.setWebSitePassword("");
        callTrackingAPIService.setWebSiteLoginUrl("https://calltracking.ru/admin/login");
        Integer[] projects = {};
        callTrackingAPIService.setProjects(projects);

        CalltrackingAPIRequestErrorHandler errorHandler = new CalltrackingAPIRequestErrorHandler();
        callTrackingAPIService.setErrorHandler(errorHandler);

        return callTrackingAPIService;
    }

    @Bean
    public CallsService callsService(){
        return new CallsServiceImpl();
    }





    @Bean
    public CallTrackingSourceConditionService callTrackingSourceConditionService(){
        return new CallTrackingSourceConditionServiceImpl();
    }


}
package com.voloshko.ctbitrix.batch;

import com.voloshko.ctbitrix.dmodel.Call;
import com.voloshko.ctbitrix.service.CallTrackingAPIService;
import com.voloshko.ctbitrix.service.CtBitrixBusinessLogicService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by berz on 22.03.2016.
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
@PropertySource("classpath:batch.properties")
public class NewCallsToCRMBatchConfiguration {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CtBitrixBusinessLogicService ctBitrixBusinessLogicService;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    CallTrackingAPIService callTrackingAPIService;

    @Bean
    public ItemReader<Call> callReader(){
        JpaPagingItemReader<Call> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManager.getEntityManagerFactory());
        reader.setQueryString("select c from Call c where state = :st");
        HashMap<String, Object> params = new LinkedHashMap<>();
        params.put("st", Call.State.NEW);
        reader.setParameterValues(params);

        return reader;
    }

    @Bean
    public ItemProcessor<Call, Call> callProcessor(){
        return new ItemProcessor<Call, Call>() {
            @Override
            public Call process(Call call) throws Exception {
                ctBitrixBusinessLogicService.processCall(call);
                return call;
            }
        };
    }

    @Bean
    public Step callAddToCRMStep(
            StepBuilderFactory stepBuilderFactory,
            ItemReader<Call> callItemReader,
            ItemProcessor<Call, Call> callProcessor

    ){
        return stepBuilderFactory.get("callAddToCRMStep")
                .<Call, Call>chunk(1)
                .reader(callItemReader)
                .processor(callProcessor)
                .faultTolerant()
                .skip(RuntimeException.class)
                .skipLimit(2000)
                .build();
    }

    @Bean
    public Job newCallsToCRMJob(
            Step callAddToCRMStep
    ){
        RunIdIncrementer runIdIncrementer = new RunIdIncrementer();

        return jobBuilderFactory.get("newCallsToCRMJob")
                .incrementer(runIdIncrementer)
                .flow(callAddToCRMStep)
                .end()
                .build();
    }
}

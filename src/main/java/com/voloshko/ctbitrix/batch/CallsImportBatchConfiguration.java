package com.voloshko.ctbitrix.batch;

import com.voloshko.ctbitrix.dmodel.Call;
import com.voloshko.ctbitrix.reader.CallTrackingCallsReader;
import com.voloshko.ctbitrix.repository.CallRepository;
import com.voloshko.ctbitrix.service.CallsService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.springframework.batch.item.ItemProcessor;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 20.09.2015.
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
@PropertySource("classpath:batch.properties")
public class CallsImportBatchConfiguration {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private CallRepository callRepository;

    @Bean
    public ItemReader<List<Call>> callsReader() throws ParseException {

        // Внимание: один шаг чтения должен сопровождаться одним шагом записи в БД. иначе будут засасываться повторяющиеся данные.
        CallTrackingCallsReader reader = new CallTrackingCallsReader(new Date(), new Date(), 100, CallTrackingCallsReader.DateMode.UPDATE_EACH_READ);
        /*Calendar calFrom = Calendar.getInstance();
        calFrom.set(2016, Calendar.FEBRUARY, 1);
        Date dtFrom = calFrom.getTime();

        Calendar calTo = Calendar.getInstance();
        calTo.set(2016, Calendar.APRIL, 10);
        Date dtTo = calTo.getTime();*/

        //CallTrackingCallsReader reader = new CallTrackingCallsReader(dtFrom, dtTo, 100, CallTrackingCallsReader.DateMode.NO_UPDATE);
        return reader;
    }

    @Bean
    public ItemProcessor<List<Call>, List<Call>> itemProcessor(){
        return new ItemProcessor<List<Call>, List<Call>>() {
            @Override
            public List<Call> process(List<Call> calls) throws Exception {

                System.out.println("process calls: " + calls);

                for(Call call : calls){
                    call.setDtmCreate(new Date());
                }

                return calls;
            }
        };
    }

    @Bean
    public ItemWriter<List<Call>> writer(){

        return new ItemWriter<List<Call>>() {
            @Override
            public void write(List<? extends List<Call>> callsPortions) throws Exception {
                for(List<Call> calls : callsPortions) {
                    System.out.println("write calls to base: " + calls);
                    callRepository.save(calls);
                }
            }
        };
    }

    @Bean
    public Step callsImportStep(
            StepBuilderFactory stepBuilderFactory,
            ItemReader<List<Call>> callsReader,
            ItemProcessor<List<Call>, List<Call>> itemProcessor,
            ItemWriter<List<Call>> writer
    ){
       return stepBuilderFactory.get("callsImportStep")
               // представляется верным, что chunk size - это эквивалент commit interval
               // поэтому делаем chunk size = 1
                .<List<Call>, List<Call>>chunk(1)
                .reader(callsReader)
                .processor(itemProcessor)
                .writer(writer)
                /*.faultTolerant()
                .skip(RuntimeException.class)
                .skipLimit(2000)*/
                .build();
    }

    @Bean
    public Job callsImportJob(Step callsImportStep){
        RunIdIncrementer runIdIncrementer = new RunIdIncrementer();

        return jobBuilderFactory.get("callsImportJob")
                .incrementer(runIdIncrementer)
                .flow(callsImportStep)
                .end()
                .build();
    }

}

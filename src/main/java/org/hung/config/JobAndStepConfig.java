package org.hung.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Configuration
public class JobAndStepConfig {

	@Autowired
	private JobBuilderFactory jobFactory;
	
	@Autowired
	private StepBuilderFactory stepFactory;
	
	Tasklet dummyTasklet() {
		return (contribution,chunkContext) -> {
			log.info("here");
			return null;
		};
	}
	
	@Bean
	Step echoStep() {
		return stepFactory.get("dummy")
				.tasklet(dummyTasklet())
				.build();
	}
	
	@Bean
	Job dummyJob() {
		return jobFactory.get("dummy")
				.incrementer(new RunIdIncrementer())
				.start(echoStep())
				.build();
	}
	
}

package org.hung;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBatchMultiDatasrcApplication {

	public static void main(String[] args) {
		ApplicationContext appCtx = SpringApplication.run(SpringBatchMultiDatasrcApplication.class, args);
		
		JobLauncher joblauncher = appCtx.getBean(JobLauncher.class);
		
		Job job = appCtx.getBean("runBalanceJob",Job.class);
		
		
		JobParameters jobParameters = new JobParametersBuilder()
				.addDate("runTime", new Date())
				.toJobParameters();
		
		try {
			joblauncher.run(job, jobParameters);
		} catch (JobExecutionAlreadyRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobRestartException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

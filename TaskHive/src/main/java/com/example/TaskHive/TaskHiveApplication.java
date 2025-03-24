package com.example.TaskHive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskHiveApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(TaskHiveApplication.class, args);
	}

}

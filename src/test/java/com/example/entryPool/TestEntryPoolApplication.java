package com.example.entryPool;

import org.springframework.boot.SpringApplication;

public class TestEntryPoolApplication {

	public static void main(String[] args) {
		SpringApplication.from(EntryPoolApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

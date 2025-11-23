package com.gb02.syumsvc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@SpringBootTest
class SyumsvcApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Test
	void contextLoads() {
	}

	@AfterAll
	static void cleanupTestData(@Autowired DataSource dataSource) {
		try (Connection connection = dataSource.getConnection();
		     Statement statement = connection.createStatement()) {
			
			// Delete test users and their related data
			statement.execute("""
				DELETE FROM usuarios
				WHERE nick LIKE 'tses_%' 
				   OR nick LIKE 'tfav_%' 
				   OR nick LIKE 'tusr_%' 
				   OR nick LIKE 'otsr_%'
				   OR nick LIKE 'ot_del_%'
				""");
			
			System.out.println("Test data cleanup completed successfully.");
		} catch (Exception e) {
			System.err.println("Error during test data cleanup: " + e.getMessage());
		}
	}

}

package com.utn.tpfinal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = TpfinalApplication.DotenvInitializer.class)
class TpfinalApplicationTests {

	@Test
	void contextLoads() {
	}

}
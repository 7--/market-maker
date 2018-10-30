package com.phil.rego;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.phil.rego.model.FloatingOrder;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FloatingOrderApplicationTests {

	@Test
	public void contextLoads() {
	}
	@Test 
	public void FloatingOrderService() throws Exception {
		FloatingOrderService fos = new FloatingOrderService();
		FloatingOrder fo = new FloatingOrder(OrderType.BID, new BigDecimal("0.71"),new BigDecimal("0.72"), new BigDecimal("4"),  new CurrencyPair("ZRX", "USDT"));
		assertTrue(fos.submitFloatingOrder(fo));
	}
}

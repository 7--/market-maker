package com.phil.rego.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;

@Entity
public class FloatingOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private OrderType orderType;
	private BigDecimal base;
	private BigDecimal most;
	private BigDecimal amount;
	private CurrencyPair cp;

	public FloatingOrder(OrderType orderType, BigDecimal base, BigDecimal most, BigDecimal amount, CurrencyPair cp) throws Exception {
		super();
		this.setOrderType(orderType);
		this.base = base;
		this.most = most;
		this.amount = amount;
		this.cp = cp;
		checkOrder();
	}

	private void checkOrder() throws Exception {
		if (orderType == OrderType.BID && base.compareTo(most) > 0) {
			// base of a bid should be less than most
			throw new Exception();
		}
		if (orderType == OrderType.ASK && base.compareTo(most) < 0) {
			// base of a ask should be greater than most
			throw new Exception();
		}		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getBase() {
		return base;
	}

	public void setBase(BigDecimal low) throws Exception {
		this.base = low;
		checkOrder();

	}

	public BigDecimal getMost() {
		return most;
	}

	public void setMost(BigDecimal high) throws Exception {
		this.most = high;
		checkOrder();

	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public CurrencyPair getCp() {
		return cp;
	}

	public void setCp(CurrencyPair cp) {
		this.cp = cp;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

}

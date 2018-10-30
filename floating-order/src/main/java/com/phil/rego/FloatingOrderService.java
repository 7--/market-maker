package com.phil.rego;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParamCurrencyPair;
import org.springframework.stereotype.Service;

import com.phil.rego.model.FloatingOrder;

@Service
public class FloatingOrderService {
	
	public boolean submitFloatingOrder(FloatingOrder fo) throws Exception {
		Exchange exchange = getExchange();
		TradeService tradeService = exchange.getTradeService();
		MarketDataService dataService = exchange.getMarketDataService();
	    OrderBook ob = dataService.getOrderBook(fo.getCp());
	
		//Won't market buy to start. 
		if(isMarketBuy(exchange, fo, ob)) {
			System.out.println("Don't market buy");
			return false;
		}
		
		if(fo.getOrderType()==OrderType.BID) {
			List<LimitOrder> bids = ob.getBids();
			BigDecimal bestBid = bids.get(0).getLimitPrice();
			//if best bid is less than base. Place order at base.
			if(fo.getBase().compareTo(bestBid)>0 ) {
				LimitOrder order = new LimitOrder.Builder(OrderType.BID, fo.getCp()).originalAmount(fo.getAmount())
						.limitPrice(fo.getBase()).build();
				String orderId = tradeService.placeLimitOrder(order);
				System.out.println("Placed order #" + orderId);
			}
			// if most ill pay is less than best bid. then put one tick above order below most.
			else if(fo.getMost().compareTo(bestBid)<0) {
				int bestBidUnderMost = Collections.binarySearch(bids, new LimitOrder.Builder(OrderType.BID, fo.getCp()).originalAmount(fo.getAmount())
						.limitPrice(bestBid).build());//.getLimitPrice();
				System.out.println("bestBidUnderMost: "+bestBidUnderMost );
			}
		

			//?if most ill pay is less than best bid. then put above best bid 1 tick
			
			else if(fo.getMost().compareTo(bestBid)<0) {
				LimitOrder order = new LimitOrder.Builder(OrderType.BID, fo.getCp()).originalAmount(fo.getAmount())
						.limitPrice(bestBid.add(new BigDecimal("0.00000001"))).build();
				String orderId = tradeService.placeLimitOrder(order);
				System.out.println("Placed order #" + orderId);
			}
			//bestBid is greater than you want to pay. put order at base
			else {
				LimitOrder order = new LimitOrder.Builder(OrderType.BID, fo.getCp()).originalAmount(fo.getAmount())
						.limitPrice(fo.getBase()).build();
			}
		}
		if(fo.getOrderType()==OrderType.ASK) {
			
		}
		//if base price is better than all order 
			//place order at base
		//else if base price is less than a order
			//if best order in book is less than 'most'
				//place tick above best in book
			//else best order is more than most
				//do nothing, wait until price goes below 'most'
		
//		LimitOrder order = new LimitOrder.Builder(OrderType.BID, fo.getCp()).originalAmount(fo.getAmount())
//				.limitPrice(fo.getBase()).build();

		

		return true;
	}

	private boolean isMarketBuy(Exchange exchange, FloatingOrder fo, OrderBook ob) throws IOException {    
	    //base should be lower than ask
	    if(fo.getOrderType()==OrderType.BID) {
		    List<LimitOrder> asks = ob.getAsks();
		    if(fo.getMost().compareTo(asks.get(0).getLimitPrice())<0) {
		    	return false;
		    }
	    }
	    if(fo.getOrderType()==OrderType.ASK) {
		    List<LimitOrder> bids = ob.getBids();
		    //ask should be lower than bid
		    if(fo.getBase().compareTo(bids.get(0).getLimitPrice())<0) {
		    	return false;
		    }
	    }
		return true;
	}

	public static Exchange getExchange() throws FileNotFoundException, IOException {

		Properties apiKeys = new Properties();
		apiKeys.load(new FileInputStream("src/main/resources/apikeys.properties"));

		ExchangeSpecification exSpec = new ExchangeSpecification(PoloniexExchange.class);
		System.out.println("Key:" + apiKeys.getProperty("poloniex_key"));
		System.out.println("Key:" + apiKeys.getProperty("poloniex_secret"));

		exSpec.setApiKey(apiKeys.getProperty("poloniex_key").trim());
		exSpec.setSecretKey(apiKeys.getProperty("poloniex_secret").trim());
		return ExchangeFactory.INSTANCE.createExchange(exSpec);
	}

	private static void printOpenOrders(TradeService tradeService) throws Exception {
		TimeUnit.SECONDS.sleep(2);

		final OpenOrdersParamCurrencyPair params = (OpenOrdersParamCurrencyPair) tradeService.createOpenOrdersParams();
		OpenOrders openOrders = tradeService.getOpenOrders(params);
		System.out.printf("All open Orders: %s%n", openOrders);

	}

}

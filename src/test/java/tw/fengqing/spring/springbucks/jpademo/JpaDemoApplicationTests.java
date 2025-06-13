package tw.fengqing.spring.springbucks.jpademo;

import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.fengqing.spring.springbucks.jpademo.model.Coffee;
import tw.fengqing.spring.springbucks.jpademo.model.CoffeeOrder;
import tw.fengqing.spring.springbucks.jpademo.repository.CoffeeOrderRepository;
import tw.fengqing.spring.springbucks.jpademo.repository.CoffeeRepository;

import java.util.Arrays;

@SpringBootTest
@Slf4j
class JpaDemoApplicationTests {

	@Autowired
	private CoffeeRepository coffeeRepository;
	
	@Autowired
	private CoffeeOrderRepository orderRepository;

	@Test
	void contextLoads() {
	}

	@Test
	public void testJpaRepository() {
		// 創建咖啡品項
		Coffee espresso = Coffee.builder()
				.name("espresso")
				.price(Money.of(CurrencyUnit.of("TWD"), 150))
				.build();
		coffeeRepository.save(espresso);
		log.info("Coffee: {}", espresso);

		Coffee latte = Coffee.builder()
				.name("latte")
				.price(Money.of(CurrencyUnit.of("TWD"), 180))
				.build();
		coffeeRepository.save(latte);
		log.info("Coffee: {}", latte);

		// 創建訂單
		CoffeeOrder order = CoffeeOrder.builder()
				.customer("FengQing")
				.items(Arrays.asList(espresso, latte))
				.state(0)
				.build();
		orderRepository.save(order);
		log.info("Order: {}", order);

		// 查詢所有咖啡品項
		coffeeRepository.findAll().forEach(c -> log.info("Loading {}", c));

		// 查詢所有訂單
		orderRepository.findAll().forEach(o -> log.info("Loading {}", o));
	}
}

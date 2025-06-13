package tw.fengqing.spring.springbucks.jpademo;

import tw.fengqing.spring.springbucks.jpademo.model.Coffee;
import tw.fengqing.spring.springbucks.jpademo.model.CoffeeOrder;
import tw.fengqing.spring.springbucks.jpademo.repository.CoffeeOrderRepository;
import tw.fengqing.spring.springbucks.jpademo.repository.CoffeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Feingqing
 */
@SpringBootApplication 
@EnableJpaRepositories 
@Slf4j 
public class JpaDemoApplication implements ApplicationRunner {
	
	@Autowired
	private CoffeeRepository coffeeRepository;

	@Autowired
	private CoffeeOrderRepository orderRepository;

	public static void main(String[] args) {
		SpringApplication.run(JpaDemoApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		initOrders();
	}

	/**
	 * 初始化訂單資料
	 * 創建咖啡和訂單記錄，並保存到資料庫
	 * 包含以下操作：
	 * 1. 創建並保存 espresso 咖啡
	 * 2. 創建並保存 latte 咖啡
	 * 3. 創建並保存包含單一咖啡的訂單
	 * 4. 創建並保存包含多個咖啡的訂單
	 */
	private void initOrders() {
		// 創建並保存 espresso 咖啡
		Coffee espresso = Coffee.builder()
				.name("espresso")
				.price(Money.of(CurrencyUnit.of("TWD"), 100.0))
				.build();
		coffeeRepository.save(espresso);
		log.info("Coffee: {}", espresso);

		// 創建並保存 latte 咖啡
		Coffee latte = Coffee.builder()
				.name("latte")
				.price(Money.of(CurrencyUnit.of("TWD"), 150.0))
				.build();
		coffeeRepository.save(latte);
		log.info("Coffee: {}", latte);

		// 創建並保存第一個訂單（只包含 espresso）
		CoffeeOrder order = CoffeeOrder.builder()
				.customer("Li Lei")
				.items(Collections.singletonList(espresso))
				.state(0)
				.build();
		orderRepository.save(order);
		log.info("Order: {}", order);

		// 創建並保存第二個訂單（包含 espresso 和 latte）
		order = CoffeeOrder.builder()
				.customer("Li Lei")
				.items(Arrays.asList(espresso, latte))
				.state(0)
				.build();
		orderRepository.save(order);
		log.info("Order: {}", order);
	}
}


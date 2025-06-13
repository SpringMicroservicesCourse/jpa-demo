package tw.fengqing.spring.springbucks.jpademo.repository;

import tw.fengqing.spring.springbucks.jpademo.model.CoffeeOrder;
import org.springframework.data.repository.CrudRepository;

public interface CoffeeOrderRepository extends CrudRepository<CoffeeOrder, Long> {
}

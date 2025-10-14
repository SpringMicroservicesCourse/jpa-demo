package tw.fengqing.spring.springbucks.jpademo.repository;

import tw.fengqing.spring.springbucks.jpademo.model.Coffee;
import org.springframework.data.repository.CrudRepository;

/**
 * 繼承 CrudRepository 以提供基本的 CRUD 操作
 */
public interface CoffeeRepository extends CrudRepository<Coffee, Long> {
}

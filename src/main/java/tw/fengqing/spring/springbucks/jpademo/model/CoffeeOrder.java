package tw.fengqing.spring.springbucks.jpademo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.joda.money.Money;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 咖啡訂單實體類
 * 使用 JPA 註解映射到資料庫表 t_order
 * 
 * @author tw.fengqing.spring.springbucks.jpademo
 */
@Entity
@Table(name = "T_ORDER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeeOrder implements Serializable {
    /**
     * 訂單 ID
     * 使用 @Id 標記為主鍵
     * 使用 @GeneratedValue 配置主鍵生成策略為自增
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 訂單中的咖啡列表
     * 使用 @ManyToMany 標記多對多關係
     * 使用 @JoinTable 配置關聯表
     */
    @ManyToMany
    @JoinTable(name = "T_ORDER_COFFEE")
    private List<Coffee> items;

    /**
     * 訂單狀態
     * 使用 @Enumerated 標記枚舉類型
     */
    @Column(nullable = false)
    private Integer state;

    /**
     * 客戶名稱
     */
    private String customer;

    /**
     * 訂單創建時間
     * 使用 @CreationTimestamp 自動設置創建時間
     */
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createTime;

    /**
     * 訂單更新時間
     * 使用 @UpdateTimestamp 自動更新修改時間
     */
    @UpdateTimestamp
    private LocalDateTime updateTime;
}


# Spring Boot JPA 示範專案

## 專案說明
這是一個使用 Spring Boot 和 JPA 實作的咖啡訂單管理系統示範專案。本專案展示了如何使用 Spring Data JPA 進行物件關聯對應（ORM）的實作。

## 技術架構
- Spring Boot 3.4.5
- Spring Data JPA
- Java 21
- H2 資料庫
- Lombok
- Joda Money

## 專案結構
```
src/main/java/tw/fengqing/spring/springbucks/jpademo/
├── JpaDemoApplication.java        # 應用程式主類別
├── model/                         # 資料模型
│   ├── Coffee.java               # 咖啡實體類別
│   ├── CoffeeOrder.java          # 訂單實體類別
│   └── MoneyConverter.java       # 金額轉換器
└── repository/                    # 資料存取層
    ├── CoffeeRepository.java     # 咖啡資料存取介面
    └── CoffeeOrderRepository.java # 訂單資料存取介面
```

## 核心程式碼說明

### 1. 咖啡實體類別 (Coffee.java)
```java
@Entity
@Table(name = "t_menu")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coffee {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Convert(converter = MoneyConverter.class)
    @Column(nullable = false)
    private Money price;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;
}
```
**程式碼說明**：
- `@Entity`：標記為 JPA 實體類別
- `@Table`：指定對應的資料表名稱為 t_menu
- `@Data`：Lombok 註解，自動生成 getter/setter 等方法
- `@Builder`：Lombok 註解，提供建構器模式
- `@Convert`：使用自定義的 MoneyConverter 處理金額轉換
- `@CreationTimestamp`：自動記錄建立時間
- `@UpdateTimestamp`：自動更新修改時間

### 2. 訂單實體類別 (CoffeeOrder.java)
```java
@Entity
@Table(name = "t_order")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeOrder {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany
    @JoinTable(name = "t_order_coffee")
    private List<Coffee> items;

    @Enumerated
    @Column(nullable = false)
    private OrderState state;

    private String customer;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;
}
```
**程式碼說明**：
- `@ManyToMany`：建立與咖啡的多對多關聯
- `@JoinTable`：指定關聯表名稱為 t_order_coffee
- `@Enumerated`：將訂單狀態以列舉型別儲存
- `@Column(nullable = false)`：設定欄位不可為空

### 3. 金額轉換器 (MoneyConverter.java)
```java
@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, Long> {
    @Override
    public Long convertToDatabaseColumn(Money attribute) {
        return attribute == null ? null : attribute.getAmountMinorLong();
    }

    @Override
    public Money convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : Money.ofMinor(CurrencyUnit.of("TWD"), dbData);
    }
}
```
**程式碼說明**：
- `@Converter`：標記為 JPA 屬性轉換器
- `autoApply = true`：自動應用於所有 Money 型別欄位
- `convertToDatabaseColumn`：將 Money 物件轉換為資料庫中的長整數值
- `convertToEntityAttribute`：將資料庫中的長整數值轉換為 Money 物件

### 4. 應用程式主類別 (JpaDemoApplication.java)
```java
@SpringBootApplication
@EnableJpaRepositories
@Slf4j
public class JpaDemoApplication implements ApplicationRunner {
    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private CoffeeOrderRepository orderRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initOrders();
    }

    private void initOrders() {
        // 建立並儲存 espresso 咖啡
        Coffee espresso = Coffee.builder()
                .name("espresso")
                .price(Money.of(CurrencyUnit.of("TWD"), 100.0))
                .build();
        coffeeRepository.save(espresso);
        log.info("Coffee: {}", espresso);

        // 建立並儲存 latte 咖啡
        Coffee latte = Coffee.builder()
                .name("latte")
                .price(Money.of(CurrencyUnit.of("TWD"), 150.0))
                .build();
        coffeeRepository.save(latte);
        log.info("Coffee: {}", latte);

        // 建立並儲存訂單
        CoffeeOrder order = CoffeeOrder.builder()
                .customer("Li Lei")
                .items(Arrays.asList(espresso, latte))
                .state(0)
                .build();
        orderRepository.save(order);
        log.info("Order: {}", order);
    }
}
```
**程式碼說明**：
- `@SpringBootApplication`：標記為 Spring Boot 應用程式
- `@EnableJpaRepositories`：啟用 JPA 倉儲功能
- `@Slf4j`：啟用 Lombok 日誌功能
- `ApplicationRunner`：實作此介面以在應用程式啟動時執行初始化操作
- `initOrders`：初始化測試資料，包含咖啡和訂單

## 建置與執行

### 前置需求
- JDK 21 或以上版本
- Maven 3.6 或以上版本

### 建置步驟
1. 複製專案
```bash
git clone https://github.com/SpringMicroservicesCourse/jpa-demo
```

2. 進入專案目錄
```bash
cd jpa-demo
```

3. 使用 Maven 建置專案
```bash
mvn clean package
```

4. 執行應用程式
```bash
mvn spring-boot:run
```

## 資料庫設定
- 使用 H2 記憶體資料庫
- 資料庫連線資訊：
  - URL: `jdbc:h2:mem:testdb`
  - 使用者名稱: `sa`
  - 密碼: 無

## 測試資料
應用程式啟動時會自動建立以下測試資料：
- 咖啡品項：
  - Espresso（NT$20）
  - Latte（NT$30）
- 訂單：
  - 單一咖啡訂單
  - 多品項咖啡訂單

## 注意事項
1. 本專案使用 H2 記憶體資料庫，重啟應用程式後資料會重置
2. 所有金額計算使用 Joda Money 函式庫，確保精確性
3. 時間戳記使用 `LocalDateTime` 型別，符合 Java 8 時間 API 規範

## 作者
- 作者：Fengqing
- 專案網址：[SpringMicroservicesCourse/jpa-demo · GitHub](https://github.com/SpringMicroservicesCourse/jpa-demo)

## 授權條款
本專案採用 MIT 授權條款，詳見 [LICENSE](LICENSE) 檔案。

## 關於我們

我們主要專注在敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。喜歡把先進技術和實務經驗結合，打造好用又靈活的軟體解決方案。近來也積極結合 AI 技術，推動自動化工作流，讓開發與運維更有效率、更智慧。持續學習與分享，希望能一起推動軟體開發的創新和進步。

## 聯繫我們

若有任何問題、合作或想了解更多，歡迎透過以下管道與我們聯繫：

- FB 粉絲頁：[風清雲談 | Facebook](https://www.facebook.com/profile.php?id=61576838896062)
- LinkedIn：[linkedin.com/in/chu-kuo-lung](https://www.linkedin.com/in/chu-kuo-lung)
- YouTube 頻道：[雲談風清 - YouTube](https://www.youtube.com/channel/UCXDqLTdCMiCJ1j8xGRfwEig)
- 風清雲談 部落格：[風清雲談](https://blog.fengqing.tw/)
- 電子郵件：[fengqing.tw@gmail.com](mailto:fengqing.tw@gmail.com)
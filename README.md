# jpa-demo

> SpringBucks coffee shop demo with JPA entity relationships and Joda Money integration

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![JPA](https://img.shields.io/badge/Spring%20Data%20JPA-3.4.5-blue.svg)](https://spring.io/projects/spring-data-jpa)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive demonstration of Spring Data JPA featuring entity relationship mapping, custom attribute converters, and professional money handling for a coffee shop management system.

## Features

- JPA entity design and relationship mapping
- Many-to-many relationship (Order ↔ Coffee)
- Custom attribute converter (MoneyConverter)
- Professional money handling with Joda Money
- Automatic timestamp management
- H2 in-memory database with console
- Repository pattern with Spring Data JPA
- Builder pattern with Lombok
- Test data initialization

## Tech Stack

- Spring Boot 3.4.5
- Spring Data JPA
- Java 21
- H2 Database (in-memory)
- Joda Money 2.0.2
- Lombok
- Maven 3.8+

## Getting Started

### Prerequisites

- JDK 21 or higher
- Maven 3.8+ (or use included Maven Wrapper)

### Installation & Run

```bash
# Clone the repository
git clone https://github.com/SpringMicroservicesCourse/jpa-demo
cd jpa-demo

# Run the application
./mvnw spring-boot:run
```

### Alternative: Run as JAR

```bash
# Build
./mvnw clean package

# Run
java -jar target/jpa-demo-0.0.1-SNAPSHOT.jar
```

## Configuration

### Application Properties

```properties
# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Database configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
```

### Configuration Highlights

| Property | Value | Description |
|----------|-------|-------------|
| `ddl-auto` | create-drop | Recreate tables on each startup |
| `show_sql` | true | Display executed SQL statements |
| `format_sql` | true | Format SQL output for readability |
| `h2.console.enabled` | true | Enable H2 web console |

### H2 Console Access

Access the H2 database console at: `http://localhost:8080/h2-console`

**Connection Settings:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## Database Schema

### Tables Created by JPA

**T_MENU** (Coffee table)
- `ID` - Primary key (auto-generated)
- `NAME` - Coffee name
- `PRICE` - Price in minor units (cents)
- `CREATE_TIME` - Creation timestamp
- `UPDATE_TIME` - Last update timestamp

**T_ORDER** (Order table)
- `ID` - Primary key (auto-generated)
- `STATE` - Order state (NOT NULL)
- `CUSTOMER` - Customer name
- `CREATE_TIME` - Creation timestamp
- `UPDATE_TIME` - Last update timestamp

**T_ORDER_COFFEE** (Join table for many-to-many)
- `ORDER_ID` - Foreign key to T_ORDER
- `ITEMS_ID` - Foreign key to T_MENU

## Usage

### Application Execution Flow

```
1. Application starts
   ↓
2. JPA auto-creates tables (T_MENU, T_ORDER, T_ORDER_COFFEE)
   ↓
3. Initializes test data:
   - Coffee: espresso (TWD 100.00)
   - Coffee: latte (TWD 150.00)
   - Order 1: Ray Chu orders 1x espresso
   - Order 2: Ray Chu orders 1x espresso + 1x latte
   ↓
4. Logs output to console
   ↓
5. H2 console available for database inspection
```

### Sample Log Output

```
Coffee: Coffee(id=1, name=espresso, price=TWD 100.00, createTime=..., updateTime=...)
Coffee: Coffee(id=2, name=latte, price=TWD 150.00, createTime=..., updateTime=...)
Order: CoffeeOrder(id=1, items=[Coffee(id=1, name=espresso, ...)], state=0, customer=Ray Chu, ...)
Order: CoffeeOrder(id=2, items=[Coffee(id=1, ...), Coffee(id=2, ...)], state=0, customer=Ray Chu, ...)
```

### Query Examples via H2 Console

```sql
-- View all coffee items
SELECT * FROM T_MENU;

-- View all orders
SELECT * FROM T_ORDER;

-- View order details with coffee items
SELECT o.id, o.customer, c.name, c.price 
FROM T_ORDER o 
JOIN T_ORDER_COFFEE oc ON o.id = oc.order_id 
JOIN T_MENU c ON oc.items_id = c.id;
```

## Key Components

### 1. Coffee Entity

**File:** `Coffee.java`

```java
@Entity
@Table(name = "T_MENU")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coffee implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    
    @Column
    @Convert(converter = MoneyConverter.class)
    private Money price;
    
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    private LocalDateTime updateTime;
}
```

**Key Features:**
- `@Entity` - Marks as JPA entity
- `@Convert` - Uses MoneyConverter for price field
- `@CreationTimestamp` - Auto-records creation time (immutable)
- `@UpdateTimestamp` - Auto-updates modification time

### 2. CoffeeOrder Entity

**File:** `CoffeeOrder.java`

```java
@Entity
@Table(name = "T_ORDER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeeOrder implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToMany
    @JoinTable(name = "T_ORDER_COFFEE")
    private List<Coffee> items;
    
    @Column(nullable = false)
    private Integer state;
    
    private String customer;
    
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    private LocalDateTime updateTime;
}
```

**Key Features:**
- `@ManyToMany` - Many-to-many relationship with Coffee
- `@JoinTable` - Specifies join table name
- `@Column(nullable = false)` - State cannot be null

### 3. MoneyConverter (Custom AttributeConverter)

**File:** `MoneyConverter.java`

```java
@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, Long> {
    
    @Override
    public Long convertToDatabaseColumn(Money attribute) {
        // Convert Money to minor units (cents)
        return attribute == null ? null : attribute.getAmountMinorLong();
    }

    @Override
    public Money convertToEntityAttribute(Long dbData) {
        // Convert minor units (cents) to Money
        return dbData == null ? null : Money.ofMinor(CurrencyUnit.of("TWD"), dbData);
    }
}
```

**Why This Matters:**
- ✅ **Precision**: Stores money as cents (Long) to avoid floating-point errors
- ✅ **Auto-apply**: Automatically applied to all Money fields
- ✅ **Currency**: Hardcoded to TWD (Taiwan Dollar) - can be made configurable

### 4. Repositories

**CoffeeRepository.java:**
```java
public interface CoffeeRepository extends CrudRepository<Coffee, Long> {
}
```

**CoffeeOrderRepository.java:**
```java
public interface CoffeeOrderRepository extends CrudRepository<CoffeeOrder, Long> {
}
```

**Benefits:**
- No implementation code needed
- Automatic CRUD methods
- Type-safe operations

## Why Joda Money?

### The Floating-Point Problem

```java
// ❌ WRONG: Using double for money
double price = 0.1 + 0.2;
System.out.println(price);  // Output: 0.30000000000000004

// ✅ CORRECT: Using Joda Money
Money price = Money.of(CurrencyUnit.of("TWD"), 0.1)
                  .plus(Money.of(CurrencyUnit.of("TWD"), 0.2));
System.out.println(price);  // Output: TWD 0.30
```

### Binary Representation Limitation

- Floating-point numbers are stored in binary format
- Some decimal values cannot be precisely represented in binary (e.g., 0.1)
- This causes rounding and truncation errors

### Joda Money Solution

```java
// Create Money object
Money price = Money.of(CurrencyUnit.of("TWD"), 100.0);

// Store in database as minor units (cents)
Long amountInCents = price.getAmountMinorLong();  // 10000 cents

// Retrieve from database
Money retrieved = Money.ofMinor(CurrencyUnit.of("TWD"), 10000);  // TWD 100.00
```

## Entity Relationship Diagram

```
┌─────────────┐         ┌──────────────────┐         ┌──────────────┐
│   Coffee    │         │ T_ORDER_COFFEE   │         │ CoffeeOrder  │
│  (T_MENU)   │────────<│  (Join Table)    │>────────│  (T_ORDER)   │
└─────────────┘         └──────────────────┘         └──────────────┘
│ ID          │         │ ORDER_ID (FK)    │         │ ID           │
│ NAME        │         │ ITEMS_ID (FK)    │         │ CUSTOMER     │
│ PRICE       │         └──────────────────┘         │ STATE        │
│ CREATE_TIME │                                      │ CREATE_TIME  │
│ UPDATE_TIME │                                      │ UPDATE_TIME  │
└─────────────┘                                      └──────────────┘
```

## Best Practices Demonstrated

1. **Money Handling**: Use Joda Money instead of double/float
2. **Entity Design**: Separate concerns with proper entity modeling
3. **Relationship Mapping**: Correct use of `@ManyToMany` and `@JoinTable`
4. **Timestamp Management**: Automatic creation/update timestamps
5. **Builder Pattern**: Fluent object creation with Lombok
6. **Repository Pattern**: Spring Data JPA for clean data access
7. **Custom Converters**: Type-safe conversion between Java and database types

## Development vs Production

### Development (Current Configuration)

```properties
# Auto-create tables, show SQL
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Production (Recommended)

```properties
# Validate only, hide SQL
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.show_sql=false
spring.jpa.properties.hibernate.format_sql=false

# Use production database (not H2)
spring.datasource.url=jdbc:mysql://localhost:3306/springbucks
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

## Testing

```bash
./mvnw test
```

## Important Notes

⚠️ **Critical Points:**

1. **H2 In-Memory Database** - Data resets on every application restart
2. **Money Precision** - Always use Joda Money for financial calculations
3. **Timestamp Annotations** - `@CreationTimestamp` makes field immutable
4. **Many-to-Many** - Creates separate join table automatically
5. **DDL Auto** - Use `validate` in production (never `create-drop`)

⚠️ **Common Pitfalls:**

- ❌ Using `double` or `float` for money
- ❌ Forgetting `@NoArgsConstructor` for JPA entities
- ❌ Using `create-drop` in production
- ❌ Missing `@EnableJpaRepositories` annotation
- ❌ Not implementing `Serializable` for entities

## Extended Practice

**Suggested Enhancements:**

1. Add more coffee attributes (description, category)
2. Implement order state management
3. Add order total calculation
4. Create Customer entity and relationship
5. Implement additional custom converters
6. Add query methods to repositories
7. Implement soft delete functionality

## References

- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Joda Money Documentation](https://www.joda.org/joda-money/)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [JPA 2.1 Specification](https://jcp.org/en/jsr/detail?id=338)

## License

MIT License - see [LICENSE](LICENSE) file for details.

## About Us

我們主要專注在敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。喜歡把先進技術和實務經驗結合，打造好用又靈活的軟體解決方案。近來也積極結合 AI 技術，推動自動化工作流，讓開發與運維更有效率、更智慧。持續學習與分享，希望能一起推動軟體開發的創新和進步。

## Contact

**風清雲談** - 專注於敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。

- 🌐 官方網站：[風清雲談部落格](https://blog.fengqing.tw/)
- 📘 Facebook：[風清雲談粉絲頁](https://www.facebook.com/profile.php?id=61576838896062)
- 💼 LinkedIn：[Chu Kuo-Lung](https://www.linkedin.com/in/chu-kuo-lung)
- 📺 YouTube：[雲談風清頻道](https://www.youtube.com/channel/UCXDqLTdCMiCJ1j8xGRfwEig)
- 📧 Email：[fengqing.tw@gmail.com](mailto:fengqing.tw@gmail.com)

---

**⭐ 如果這個專案對您有幫助，歡迎給個 Star！**

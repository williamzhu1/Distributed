# 本地数据储存逻辑
Spring通过组件扫描（component scanning）自动发现应用中的Bean。当你启动Spring应用时，Spring容器会扫描所有带有@Component、@Service、@Repository等注解的类，并将它们注册为Bean。对于MealsRepository，通常会用@Repository注解标记，或者由于它继承了Spring Data JPA的接口，Spring会隐式地将其视为一个Bean。
注册与创建Bean：一旦MealsRepository被Spring识别并注册为Bean，它就会在需要时被自动装配到其他Bean中。在你的MealsRestRpcStyleController中，Spring看到构造器参数上有@Autowired注解，就会查找匹配类型的Bean（在这个案例中是MealsRepository和OrdersRepository），并将其注入到MealsRestRpcStyleController的新实例中。

# API Endpoints
### GET /supplier/goods

Description: Retrieves a collection of all available goods.  
Returns: Collection<Good>

### GET /supplier/order

Description: Adds a new order. The status of the new order should initially be set to DECLINED. If the orderId already exists, it suggests a new orderId and sets the status to ALREADYEXIST.
Request Body: Order object.
Behavior:
Checks if the orderId already exists.
If it does, updates the orderId and sets the status to ALREADYEXIST.
If the orderId does not exist, it checks the inventory of the specified goods.
If sufficient inventory is available, the status is updated to PROCESSING.
Returns: Order

### GET /supplier/declineOrder

Description: Declines an existing order. The status of the order is updated to DECLINED.
Request Body: Order object.
Behavior:
Looks for the order by orderId.
If found, updates the order's status to DECLINED and returns the updated order.
If not found, sets the status of the passed Order to NOTFOUND.
Returns: Order

### GET /supplier/checkOrderStatus

Description: Checks the status of an order.  
Request Body: Order object.  
Behavior:  
Looks for the order by orderId.  
If found, returns the order.  
If not found, sets the status of the passed Order to NOTFOUND.  
Returns: Order  

# Supplier Class Documentation

## Package
`be.kuleuven.foodrestservice.domain`

## Class Definition
`Supplier`

## Fields

- `int id` - Unique identifier for the supplier.
- `String name` - Name of the supplier.
- `String logoUrl` - URL to the supplier's logo image.

## Constructors

### Supplier(int id, String name)
Initializes a new instance of the Supplier class with the specified id and name.

### Supplier(int id, String name, String logoUrl)
Initializes a new instance of the Supplier class with the specified id, name, and logoUrl.

## Methods

### Getters

- `int getId()` - Returns the supplier's id.
- `String getName()` - Returns the supplier's name.
- `String getLogoUrl()` - Returns the URL to the supplier's logo.

### Setters

- `void setId(int id)` - Sets the supplier's id.
- `void setName(String name)` - Sets the supplier's name.
- `void setLogoUrl(String logoUrl)` - Sets the URL to the supplier's logo.
# Good Class Documentation

## Package
`be.kuleuven.foodrestservice.domain`

## Class Definition
`Good`

## Fields

- `int id` - Unique identifier for the good.
- `String name` - Name of the good.
- `String imageUrl` - URL for the image of the good.
- `float price` - Price of the good.
- `String sex` - Category of the good, potentially indicating its targeted gender or other demographic.
- `List<Inventory> inventories` - List of inventory items associated with this good.

## Constructors

### Good(int id, String name, String imageUrl, float price, String sex, List<Inventory> inventories)
Initializes a new instance of the Good class with specified details and a list of inventories.

### Good()
Default constructor that initializes a new instance with default values and an empty list for inventories.

## Methods

### Getters

- `int getId()` - Returns the good's id.
- `String getName()` - Returns the good's name.
- `String getImageUrl()` - Returns the URL to the good's image.
- `float getPrice()` - Returns the good's price.
- `String getSex()` - Returns the good's sex.
- `List<Inventory> getInventories()` - Returns a copy of the list of inventory items.

### Setters

- `void setId(int id)` - Sets the good's id.
- `void setName(String name)` - Sets the good's name.
- `void setImageUrl(String imageUrl)` - Sets the URL to the good's image.
- `void setPrice(float price)` - Sets the good's price.
- `void setSex(String sex)` - Sets the good's sex.
- `void setInventories(List<Inventory> inventories)` - Sets the list of inventories with a new list.

### Inventory Management Methods

- `void addInventory(Inventory inventory)` - Adds an inventory item to the list.
- `void removeInventory(Inventory inventory)` - Removes an inventory item from the list.


# Inventory Class Documentation

## Package
`be.kuleuven.foodrestservice.domain`

## Class Definition
`Inventory`

## Fields

- `int id` - Unique identifier for the inventory item.
- `int shoesSize` - Size of the shoes in inventory.
- `int availableQuantity` - Number of items currently available for reservation.
- `int onholdQuantity` - Number of items reserved but not yet sold.
- `int soldQuantity` - Number of items sold.

## Constructors

### Inventory(int id, int shoesSize, int availableQuantity, int onholdQuantity, int soldQuantity)
Initializes a new instance of the Inventory class with specified details.

### Inventory()
Default constructor which initializes a new instance of the Inventory class with default values.

## Methods

### Getters

- `int getId()`
- `int getShoesSize()`
- `int getAvailableQuantity()`
- `int getOnholdQuantity()`
- `int getSoldQuantity()`

### Setters

- `void setId(int id)`
- `void setShoesSize(int shoesSize)`
- `void setAvailableQuantity(int availableQuantity)`
- `void setOnholdQuantity(int onholdQuantity)`
- `void setSoldQuantity(int soldQuantity)`

### Inventory Management Methods

- `void addAvailable(int quantity)` - Adds specified quantity to available stock.
- `void reserveStock(int quantity)` - Reserves specified quantity of stock if available.
- `void sellStock(int quantity)` - Sells specified quantity of stock if on hold.


# Order Class Documentation

## Package
`be.kuleuven.foodrestservice.domain`

## Class Definition
`Order`

## Fields

- `String orderId` - Unique identifier for the order, automatically generated.
- `List<String> mealIds` - List of meal identifiers included in the order.
- `String address` - Delivery address for the order.
- `Status status` - Current status of the order, using the Status enum.

## Constructors

### Order(String address)
Initializes a new instance of the Order class with the specified delivery address and sets the initial status to PENDING. A unique orderId is automatically generated.

## Methods

### Getters

- `String getOrderId()` - Returns the order's unique identifier.
- `List<String> getMealIds()` - Returns a copy of the list of meal IDs.
- `String getAddress()` - Returns the delivery address of the order.
- `Status getStatus()` - Returns the current status of the order.

### Setters

- `void setAddress(String address)` - Sets the delivery address of the order.
- `void setStatus(Status status)` - Sets the current status of the order.

### Order Manipulation Methods

- `void addMeal(String mealId)` - Adds a meal to the order.
- `void removeMeal(String mealId)` - Removes a meal from the order.


# OrderItem Class Documentation

## Package
`be.kuleuven.foodrestservice.domain`

## Class Definition
`OrderItem`

## Fields

- `int id` - Unique identifier for the order item.
- `int goodId` - Identifier for the good associated with this order item.
- `int inventoryId` - Identifier for the inventory from which the good is sourced.
- `int quantity` - Quantity of the good ordered.
- `Status status` - Current status of the order item, using the Status enum.

## Constructors

### OrderItem(int id, int goodId, int inventoryId, int quantity, Status status)
Initializes a new instance of the OrderItem class with the specified details. Sets the order item's properties including id, good id, inventory id, quantity, and status.

### OrderItem()
Default constructor that initializes a new instance of the OrderItem class with default values.

## Methods

### Getters

- `int getId()` - Returns the order item's id.
- `int getGoodId()` - Returns the good's id associated with the order item.
- `int getInventoryId()` - Returns the inventory id from which the good is sourced.
- `int getQuantity()` - Returns the quantity of the good ordered.
- `Status getStatus()` - Returns the current status of the order item.

### Setters

- `void setId(int id)` - Sets the order item's id.
- `void setGoodId(int goodId)` - Sets the good's id associated with the order item.
- `void setInventoryId(int inventoryId)` - Sets the inventory id from which the good is sourced.
- `void setQuantity(int quantity)` - Sets the quantity of the good ordered.
- `void setStatus(Status status)` - Sets the current status of the order item.

# dapp2024
How to upload to the server(tar file) <br />
scp 'filepath' dapp@dsgtwilliamzhu.japaneast.cloudapp.azure.com: <br />
Unzip the tar file with and run inside the target folder  <br />
java -jar food-rest-service-0.0.1-SNAPSHOT.jar <br />

## Apikey
'H' "ApiKey: {apikey}"

## Endpoints

All API responses are wrapped as Api response class to standardize the response <br />
private boolean success; <br />
private T data; <br />
private String message; <br />
#### Item Structure

```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "price": "number",
  "category": "string",
  "manufacturer": "string",
  "stock": "integer"
} 
```
#### Order

```json
{
  "id": "string",
  "masterId": "string",
  "address": "string",
  "items": {
    "item": "quantity"
  },
  "orderStatus": "string"
}
```
Order status is an enum 

  PENDING("pending"),
  CONFIRMED("confirmed"),
  DELIVERED("delivered"),
  CANCELLED("cancelled"),
  ROOTSTOCK("rootstock");

### Items

#### Get Item by ID

**Endpoint:** `/items/{id}`  
**Method:** `GET`  
**Description:** Retrieves an item by its ID.  
**Response:**
```json
{
  "success": true,
  "data": {
    "id": "123",
    "name": "Item Name",
    "description": "Description of the item",
    "price": 9.99,
    "category": "Category",
    "manufacturer": "Manufacturer",
    "stock": 10
  },
  "message": "Item retrieved successfully."
}
```

#### Update Item by ID

**Endpoint:** `/items/{id}`  
**Method:** `PUT`  
**Description:** Updates an item by its ID.  
**Request body:**
```json
{
  "name": "Updated Item",
  "description": "Updated description",
  "price": 14.99,
  "category": "Updated Category",
  "manufacturer": "Updated Manufacturer",
  "stock": 15
}
```
**Response:**
```json
{
  "success": true,
  "data": {
  "name": "Updated Item",
  "description": "Updated description",
  "price": 14.99,
  "category": "Updated Category",
  "manufacturer": "Updated Manufacturer",
  "stock": 15
  },
  "message": "Item updated successfully."
}
```

#### Delete Item by ID

**Endpoint:** `/items/{id}`  
**Method:** `DELETE`  
**Description:** Deletes an item by its ID.
**Response:**
```json
{
  "success": true,
  "data": null,
  "message": "Item deleted successfully."
}
```

#### Get All Items

**Endpoint:** `/items`  
**Method:** `GET`  
**Description:** Retrieves all items.  
**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "123",
      "name": "Item Name",
      "description": "Description of the item",
      "price": 9.99,
      "category": "Category",
      "manufacturer": "Manufacturer",
      "stock": 10
    },
    {
      "id": "124",
      "name": "Another Item",
      "description": "Description of another item",
      "price": 19.99,
      "category": "Another Category",
      "manufacturer": "Another Manufacturer",
      "stock": 5
    }
  ],
  "message": "Items retrieved successfully."
}
```
#### Add Item

**Endpoint:** `/items`  
**Method:** `POST`  
**Description:** Add an item.  
**Request body:**
```json
{
  "id": "125",
  "name": "New Item",
  "description": "Description of the new item",
  "price": 29.99,
  "category": "New Category",
  "manufacturer": "New Manufacturer",
  "stock": 20
}
```
**Response:**
```json
{
  "success": true,
  "data": {
    "id": "125",
    "name": "New Item",
    "description": "Description of the new item",
    "price": 29.99,
    "category": "New Category",
    "manufacturer": "New Manufacturer",
    "stock": 20
  },
  "message": "Item added successfully."
}
```
### Orders

#### Get Order by ID

**Endpoint:** `/orders/{id}`  
**Method:** `GET`  
**Description:** Retrieves an order by its ID.  
**Response:**
```json
{
  "success": true,
  "data": {
    "id": "123",
    "masterId": "master123",
    "address": "123 Main St, Anytown, USA",
    "items": {
      "Item{id='item1', name='Widget', price=9.99}": 2,
      "Item{id='item2', name='Gizmo', price=19.99}": 1
    },
    "orderStatus": "PENDING"
  },
  "message": "Order retrieved successfully."
}
```

#### Get All Orders

**Endpoint:** `/orders`  
**Method:** `GET`  
**Description:** Retrieves all orders.  
**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "123",
      "masterId": "master123",
      "address": "123 Main St, Anytown, USA",
      "items": {
        "Item{id='item1', name='Widget', price=9.99}": 2,
        "Item{id='item2', name='Gizmo', price=19.99}": 1
      },
      "orderStatus": "PENDING"
    }
  ],
  "message": "Orders retrieved successfully."
}
```
#### Add Order

**Endpoint:** `/orders`  
**Method:** `POST`  
**Description:** Add an order.  
**Request body:**
```json
{
  "masterId": "master124",
  "address": "456 Elm St, Anothertown, USA",
  "items": {
    "Item{id='item1', name='Widget', price=9.99}": 3,
    "Item{id='item3', name='Gadget', price=29.99}": 2
  },
  "orderStatus": "PENDING"
}
```
**Response:**
```json
{
  "success": true,
  "data": {
    "id": "124",
    "masterId": "master124",
    "address": "456 Elm St, Anothertown, USA",
    "items": {
      "Item{id='item1', name='Widget', price=9.99}": 3,
      "Item{id='item3', name='Gadget', price=29.99}": 2
    },
    "orderStatus": "PENDING"
  },
  "message": "Order added successfully."
}
```
#### Update Order by ID

**Endpoint:** `/order/{id}`  
**Method:** `PUT`  
**Description:** Updates an order by its ID.  
**Request body:**
```json
{
  "masterId": "1",
  "address": "New Address",
  "status": "COMPLETED",
  "items": {
    "5268203c-de76-4921-a3e3-439db69c462a": 1,
    "cfd1601f-29a0-485d-8d21-7607ec0340c8": 4
  }
}
```
**Response:**
```json
{
  "success": true,
  "data": {
    "id": "cfd1601f-29a0-485d-8d21-7607ec0340c1",
    "masterId": "1",
    "address": "New Address",
    "status": "COMPLETED",
    "items": {
      "5268203c-de76-4921-a3e3-439db69c462a": 1,
      "cfd1601f-29a0-485d-8d21-7607ec0340c8": 4
    }
  },
  "message": "Order updated successfully."
}
```

#### Delete Order by ID

**Endpoint:** `/order/{id}`  
**Method:** `DELETE`  
**Description:** Deletes an order by its ID.
**Response:**
```json
{
  "success": true,
  "message": "Order deleted successfully."
} 
```






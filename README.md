# 这是supplier后端文件
#### 测试curl代码在test_rest.sh
主要提供restful api给webserver

# api 端口
### 1.Good

| 地址              | 功能                           | 接收数据    | 返回数据                                                                                     | 
|-----------------|------------------------------|---------|------------------------------------------------------------------------------------------|
| /supplier/goods | 获取所有商品                       | *       | 所有商品的具体信息，包括inventory                                                                    |

<details>
<summary>/supplier/goods</summary>
1./supplier/goods
发送数据：空  

接受数据：获取商品信息。。。。。
[ {
  "id" : "5268203c-de76-4921-a3e3-439db69c462a",
  "name" : "AJ1 black red",
  "imageUrl" : "https://files.oaiusercontent.com/file-LRtWJrcTkbMmABUiuBdgfjse?se=2024-05-07T16%3A34%3A58Z&sp=r&sv=2021-08-06&sr=b&rscc=max-age%3D31536000%2C%20immutable&rscd=attachment%3B%20filename%3D0344fafc-d2f9-4fe5-82c9-61e8934db8d3.webp&sig=jDfysOcMXkOYEJzWepocKEj/wTyKENuguhG4LmnYFTs%3D",
  "price" : 100.0,
  "sex" : "Male",
  "inventories" : [ {
    "id" : 1,
    "shoesSize" : 41,
    "availableQuantity" : 5,
    "onholdQuantity" : 5,
    "soldQuantity" : 1
  } ]
}, {
  "id" : "6268203c-de76-4921-a3e3-439db69c462a",
  "name" : "AJ10 white",
  "imageUrl" : "",
  "price" : 200.0,
  "sex" : "female",
  "inventories" : [ {
    "id" : 2,
    "shoesSize" : 36,
    "availableQuantity" : 32,
    "onholdQuantity" : 2,
    "soldQuantity" : 1
  } ]
} ]

</details>


### 2.order
| 地址              | 功能                                            | 接收数据        | 返回数据                                                                                          | 
|-----------------|-----------------------------------------------|-------------|-----------------------------------------------------------------------------------------------|
| /supplier/order | broker 对supplier下订单，只能包含单个商品                  | Order class | Order class,如果order成功，则status为Processing，如果order失败，则status为Declined，broker需要重新将该订单所有商品decline |
|/supplier/declineOrder| broker对已存在的订单修改状态，将其状态改为decliened | Order class | 新的Order class，新order 的状态会变为declined, 如果supplier找不到这个order，则返回同样的order，order的状态改为 NotFound     |
|/supplier/checkOrderStatus|查看某个订单当前的状态|Order class| 查询到的order class，如果order不存在，返回的orderclass的状态为NotFound|
<details>
<summary>/supplier/order</summary>
发送数据:
curl -X GET "http://localhost:8081/supplier/order" \
    -H "Content-Type: application/json" \
    -d '{
        "orderId": "some-unique-id",
        "goodsId": "5268203c-de76-4921-a3e3-439db69c462a",
        "inventoryId": 1,
        "quantity": 5
    }'

接收数据：
添加order
{
"success" : true,
"orderId" : "some-unique-id",
"goodsId" : "5268203c-de76-4921-a3e3-439db69c462a",
"address" : null,
"status" : "PROCESSING",
"inventoryId" : 1,
"quantity" : 5
}
添加order,orderID已经存在（重复）
{
"success" : false,
"orderId" : "0a5fdc4a-5c2f-4ce2-b0b7-4ab52a67e233",
"goodsId" : "5268203c-de76-4921-a3e3-439db69c462a",
"address" : null,
"status" : "ALREADYEXIST",
"inventoryId" : 1,
"quantity" : 5
}
</details>

<details>
<summary> /supplier/declineOrder </summary>
发送数据：
curl -X GET "http://localhost:8081/supplier/declineOrder" \
    -H "Content-Type: application/json" \
    -d '{
        "orderId": "some-unique-id"
    }'

接收数据：
declien order，order存在
{
"success" : true,
"orderId" : "some-unique-id",
"goodsId" : "5268203c-de76-4921-a3e3-439db69c462a",
"address" : null,
"status" : "DECLINED",
"inventoryId" : 1,
"quantity" : 5
}declien order，order不存在
{
"success" : false,
"orderId" : "order-id-to-decline",
"goodsId" : null,
"address" : null,
"status" : "NOTFOUND",
"inventoryId" : 0,
"quantity" : 0
}
</details>


<details>
<summary>/supplier/checkOrderStatus</summary>
发送数据：
curl -X GET "http://localhost:8081/supplier/checkOrderStatus" \
    -H "Content-Type: application/json" \
    -d '{
        "orderId": "some-unique-id"
    }'
接收数据：
查询order， order存在
{
  "success" : true,
  "orderId" : "some-unique-id",
  "goodsId" : "5268203c-de76-4921-a3e3-439db69c462a",
  "address" : null,
  "status" : "DECLINED",
  "inventoryId" : 1,
  "quantity" : 5
}\n 查询order，order不存在
{
  "success" : false,
  "orderId" : "order-id-to-check",
  "goodsId" : null,
  "address" : null,
  "status" : "NOTFOUND",
  "inventoryId" : 0,
  "quantity" : 0
}
</details>

# 数据格式
### 详细的数据格式在domain中的readme.md文件中表示
### 供应商 Supplier
    id
    Name
    logoUrl

### 商品种类 Good
    id
    name
    imageUrl
    price
    sex
    List-Inventory

### 库存信息 Inventory
    id    (id需要unique)
    size
    availableQuantity
    onholdQuantity
    soldQuantity

### 订单 Order
    id
    address
    status
    list - OrderItem

### 订单物品 OrderItem
    id
    goodId
    inventoryId
    quantity
    status

### 状态 status    enum
    PENDING
    PROCESSING
    DELIVERED
    CANCELLED



# 安全性
与supplier通信的密钥问题  
API_KEY 出存在环境变量中

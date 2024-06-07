#!/bin/bash



echo "Testing MealsRestRpcStyleController..."


echo "获取商品信息。。。。。"
curl -X GET "http://localhost:8081/supplier/goods" \
    -H "Content-Type: application/json"

echo "添加order。。。。"
curl -X GET "http://localhost:8081/supplier/order" \
    -H "Content-Type: application/json" \
    -d '{
        "orderId": "some-unique-id",
        "goodsId": "5268203c-de76-4921-a3e3-439db69c462a",
        "inventoryId": "a",
        "quantity": 5
    }'

echo "添加order,orderID已经存在（重复）"
curl -X GET "http://localhost:8081/supplier/order" \
    -H "Content-Type: application/json" \
    -d '{
        "orderId": "some-unique-id",
        "goodsId": "5268203c-de76-4921-a3e3-439db69c462a",
        "inventoryId": "a",
        "quantity": 5
    }'

echo "declien order，order存在"
curl -X GET "http://localhost:8081/supplier/declineOrder" \
    -H "Content-Type: application/json" \
    -d '{
        "orderId": "some-unique-id"
    }'

echo "declien order，order不存在"
curl -X GET "http://localhost:8081/supplier/declineOrder" \
    -H "Content-Type: application/json" \
    -d '{
        "orderId": "order-id-to-decline"
    }'

echo "查询order， order存在"
curl -X GET "http://localhost:8081/supplier/checkOrderStatus" \
    -H "Content-Type: application/json" \
    -d '{
        "orderId": "some-unique-id"
    }'


echo "\n 查询order，order不存在"
curl -X GET "http://localhost:8081/supplier/checkOrderStatus" \
    -H "Content-Type: application/json" \
    -d '{
        "orderId": "order-id-to-check"
    }'


echo "Testing completed."

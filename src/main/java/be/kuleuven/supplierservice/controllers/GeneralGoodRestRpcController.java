package be.kuleuven.supplierservice.controllers;

import be.kuleuven.supplierservice.domain.Good;
import be.kuleuven.supplierservice.domain.GoodsRepository;
import be.kuleuven.supplierservice.domain.Order;
import be.kuleuven.supplierservice.domain.OrdersRepository;
import be.kuleuven.supplierservice.domain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
public class GeneralGoodRestRpcController {
    private final GoodsRepository goodsRepository;
    private final OrdersRepository ordersRepository;

    @Autowired
    GeneralGoodRestRpcController(GoodsRepository goodsRepository, OrdersRepository ordersRepository) {
        this.goodsRepository = goodsRepository;
        this.ordersRepository = ordersRepository;
    }

    @GetMapping("/supplier/goods")
    Collection<Good> getGoods(){return goodsRepository.getAllGood();}

    // new coming order status should be PENDING
    @GetMapping("/supplier/order")
    Order addOrder(@RequestBody Order newOrder){
        String orderId = newOrder.getOrderId();
        newOrder.setStatus(Status.DECLINED);
        newOrder.setSuccess(false);
        //check if orderid already exist recommend a new orderid
        this.ordersRepository.findOrderById(orderId).ifPresent(order -> {
            newOrder.setStatus(Status.ALREADYEXIST);
            newOrder.setOrderId(this.ordersRepository.generateOrderId());
        });
        if (newOrder.getStatus() == Status.ALREADYEXIST){
            return newOrder;
        }

        String goodsId = newOrder.getGoodsId();
        String inventoryId = newOrder.getInventoryId();
        int quantity  = newOrder.getQuantity();
        this.goodsRepository.findGood(goodsId).ifPresent(good -> {
            good.findInventory(inventoryId).ifPresent(inventory -> {
                if(inventory.reserveStock(quantity)){
                    newOrder.setStatus(Status.PROCESSING);
                    newOrder.setSuccess(true);
                }
            });
        });
        this.ordersRepository.addOrder(newOrder);
        return newOrder;
    }

    // from broker side decline order.
    @GetMapping("/supplier/declineOrder")
    Order declineOrder(@RequestBody Order declinedOrder){
        String orderId = declinedOrder.getOrderId();
        return this.ordersRepository.findOrderById(orderId).map(order -> {
            // 如果订单的状态已经是DECLINED，直接返回
            if (order.getStatus() == Status.DECLINED) {
                return order;
            }
            order.setStatus(Status.DECLINED); // Update the status of the retrieved order
            this.goodsRepository.findGood(order.getGoodsId()).ifPresent(
                    good -> good.findInventory(order.getInventoryId()).ifPresent(
                            inventory ->
                                    inventory.reverseBook(order.getQuantity())
                    )
                    );
            return order;
        }).orElseGet(() -> {
            declinedOrder.setStatus(Status.NOTFOUND); // Set status to NOTFOUND if order isn't found
            return declinedOrder; // Return the passed-in order with updated status
        });
    }

    //check the new orderstatus
    @GetMapping("/supplier/checkOrderStatus")
    Order checkOrder(@RequestBody Order checkedOrder){
        String orderId = checkedOrder.getOrderId();
        // Return the updated order directly
        return this.ordersRepository.findOrderById(orderId).orElseGet(() -> {
            checkedOrder.setStatus(Status.NOTFOUND); // Set status to NOTFOUND if order isn't found
            return checkedOrder; // Return the passed-in order with updated status
        });
    }
}

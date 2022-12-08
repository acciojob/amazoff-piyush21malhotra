package com.driver;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {


    private String id;
    HashMap<String, Order> orderHashmap;
    HashMap<String, DeliveryPartner> partnerHashMap;
    HashMap<String, List<String>> orderPartnerPair;
    HashSet<String> unassignedOrderMap;

    public OrderRepository() {
        this.id = "1";
        this.orderHashmap = new HashMap<>();
        this.partnerHashMap = new HashMap<>();
        this.orderPartnerPair = new HashMap<>();
        this.unassignedOrderMap = new HashSet<>();
    }

    public void addOrder(Order order) {

        order.setId(id);
        this.id = Integer.toString(Integer.parseInt(id) + 1);
        orderHashmap.put(id, order);
        unassignedOrderMap.add(order.getId());
    }

    public void addPartner(String partnerId) {
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        partnerHashMap.put(partnerId, deliveryPartner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        if(orderHashmap.containsKey(orderId) && partnerHashMap.containsKey(partnerId) && unassignedOrderMap.contains(orderId)) {
            List<String> listOfOrders = new ArrayList<>();

            if(orderPartnerPair.containsKey(partnerId)) {
                listOfOrders = orderPartnerPair.get(partnerId);
            }
            listOfOrders.add(orderId);
            orderPartnerPair.put(partnerId, listOfOrders);
            unassignedOrderMap.remove(orderId);
        }
    }

    public Order getOrderById(String orderId) {
        return orderHashmap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partnerHashMap.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        return partnerHashMap.get(partnerId).getNumberOfOrders();
    }

    public List<Order> getOrdersByPartnerId(String partnerId) {
        List<Order> listOfOrder = new ArrayList<>();

        List<String> listOfOrderId = orderPartnerPair.get(partnerId);

        for(String orderId : listOfOrderId) {
            listOfOrder.add(orderHashmap.get(orderId));
        }
        return listOfOrder;
    }

    public List<Order> getAllOrders() {
        List<Order> listOfOrder = new ArrayList<>();

        for(String orderId : orderHashmap.keySet()) {
            listOfOrder.add(orderHashmap.get(orderId));
        }
        return listOfOrder;
    }

    public Integer getCountOfUnassignedOrders() {
        return unassignedOrderMap.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {

        Integer orderCount = 0;
        List<String> listOfOrders = orderPartnerPair.get(partnerId);

        for(String orderId : listOfOrders) {
            if(orderHashmap.get(orderId).getDeliveryTime() > Integer.parseInt(time)) {
                orderCount++;
            }
        }
        return orderCount;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        List<String> listOfOrders = orderPartnerPair.get(partnerId);

        int lastDeliveryTime = Integer.MIN_VALUE;
        for(String orderId : listOfOrders) {
            if(orderHashmap.get(orderId).getDeliveryTime() > lastDeliveryTime) {
                lastDeliveryTime = orderHashmap.get(orderId).getDeliveryTime();
            }
        }
        return Integer.toString(lastDeliveryTime);
    }

    public void deletePartnerById(String partnerId) {
        List<String> listOfOrders = orderPartnerPair.get(partnerId);

        for(String orderId : listOfOrders) {
            unassignedOrderMap.add(orderHashmap.get(orderId).getId());
        }
        orderPartnerPair.remove(partnerId);
        partnerHashMap.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {
        orderHashmap.remove(orderId);

        for(List<String> orderIds : orderPartnerPair.values()) {

            for(String order : orderIds) {
                if(order.equals(orderId)) {
                    orderIds.remove(orderId);
                    return;
                }
            }
        }
    }

}

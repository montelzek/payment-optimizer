package com.montelzek.paymentoptimizer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.montelzek.paymentoptimizer.model.Order;
import com.montelzek.paymentoptimizer.model.PaymentMethod;
import com.montelzek.paymentoptimizer.service.PaymentOptimizer;
import com.montelzek.paymentoptimizer.util.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {


        List<Order> orders = JsonMapper.mapToPojo(new File(args[0]), new TypeReference<>() {});
        List<PaymentMethod> paymentMethods = JsonMapper.mapToPojo(new File(args[1]), new TypeReference<>() {});

        Map<String, BigDecimal> result = PaymentOptimizer.processOrders(orders, paymentMethods);

        result.forEach((method, amount) -> {
            System.out.println(method + " " + amount.setScale(2, RoundingMode.HALF_UP));
        });
    }
}

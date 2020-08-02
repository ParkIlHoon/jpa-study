package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController
{
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    /**
     * JSON 변환 과정에서 무한루프, Exception 엄청 발생!!
     * @return
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1()
    {
        List<Order> allByCriteria = orderRepository.findAllByCriteria(new OrderSearch());

        for (Order order : allByCriteria)
        {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }

        return allByCriteria;
    }

    /**
     * DTO 를 사용한 방식. Entity 필드에 @JsonIgnore 어노테이션을 붙이지 않아도 된다.
     * BUT, Lazy Loading 으로 인해 쿼리가 너무 많이 호출되는 문제가 있음.
     * @return
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2()
    {
        List<Order> allByCriteria = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> collect = allByCriteria.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        // 총 9회 쿼리 수행됨
        return collect;
    }

    @Data
    static class OrderDto
    {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order o)
        {
            this.orderId = o.getId();
            this.name = o.getMember().getName();
            this.orderDate = o.getOrderDate();
            this.orderStatus = o.getStatus();
            this.address = o.getDelivery().getAddress();
            this.orderItems = o.getOrderItems().stream()
                                                .map(oi -> new OrderItemDto(oi))
                                                .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto
    {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem)
        {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}

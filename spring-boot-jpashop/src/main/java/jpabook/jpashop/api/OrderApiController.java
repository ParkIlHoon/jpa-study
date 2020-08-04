package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 권장 순서
 * 1. 엔티티 조회 방식으로 우선 접근
 *  1-1. 페치 조인으로 쿼리 수를 최적화
 *  1-2. 컬렉션 최적화
 *      1-2-1. 페이징 필요 : hibernate.default_batch_fetch_size 로 최적화
 *      1-2-2. 페이징 불필요 : 페치 조인 사용
 * 2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용
 * 3. DTO 조회 방식으로 해결이 안되면 NativeSQL 또는 스프링 JDBC Template 사용
 */
@RestController
@RequiredArgsConstructor
public class OrderApiController
{
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

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

    /**
     * 패치 조인으로 쿼리 한 번에 조회하도록 구현
     * 단점 : 페이징 처리 불가능
     * @return
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3()
    {
        List<Order> allWithItem = orderRepository.findAllWithItem();
        List<OrderDto> collect = allWithItem.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());

        return collect;
    }

    /**
     * V3에서 페이징 처리를 하기 위해서
     * - 컬렉션은 페치 조인 시 페이징 불가능
     * - xToOne 관계는 페치 조인으로 쿼리 수 최적화
     * - 컬렉션은 페치 조인 대신에 지연 로딩을 유지하고, hibernate.default_batch_fetch_size 로 최적화
     * @param offset
     * @param limit
     * @return
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit)
    {
        List<Order> allWithItem = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> collect = allWithItem.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());

        return collect;
    }

    /**
     * DTO 로 바로 조회
     * @return
     */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4()
    {
        return orderQueryRepository.findOrderQueryDtos();
    }

    /**
     * DTO 컬렉션 조회 최적화
     * 일대다 관계인 컬렉션은 in 절을 활용해서 메모리에 미리 조회해서 최적화
     * @return
     */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5()
    {
        return orderQueryRepository.findAllByDto_optimization();
    }

    /**
     * join 결과를 그대로 조회 후 애플리케이션에서 원하는 형태로 직접 변환
     * @return
     */
    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> ordersV6()
    {
        return orderQueryRepository.findAllByDto_flat();
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

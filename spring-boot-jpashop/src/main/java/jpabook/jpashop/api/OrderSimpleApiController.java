package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController
{
    private final OrderRepository orderRepository;

    /**
     * JSON 변환 과정에서 무한루프, Exception 엄청 발생!!
     * @return
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1()
    {
        List<Order> allByCriteria = orderRepository.findAllByCriteria(new OrderSearch());
        return allByCriteria;
    }

    /**
     * DTO 를 사용한 방식. Entity 필드에 @JsonIgnore 어노테이션을 붙이지 않아도 된다.
     * BUT, Lazy Loading 으로 인해 쿼리가 너무 많이 호출되는 문제가 있음.
     * @return
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2()
    {
        // Order 조회 1회 -> 결과 2
        List<Order> allByCriteria = orderRepository.findAllByCriteria(new OrderSearch());
        // 한 번의 loop마다 Member, Delivery 조회 총 2회 -> 총 4회 조회
        List<SimpleOrderDto> collect = allByCriteria.stream()
                                                    .map(o -> new SimpleOrderDto(o))
                                                    .collect(Collectors.toList());
        // 전체 select 문 수행 건수 : 5건 (1 + 2 + 2)
        return collect;
    }

    /**
     * 패치 조인을 사용한 방식. Order 조회 시, Member, Delivery 를 한 번에 가져와 SELECT 쿼리가 총 1회 수행됨.
     * @return
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3()
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> collect = orders.stream()
                                            .map(o -> new SimpleOrderDto(o))
                                            .collect(Collectors.toList());
        return collect;
    }

    /**
     * Repository 에서 데이터 조회 시 바로 DTO 형태로 바꿔서 조회해옴.
     * 이 클래스의 코드는 간단해지나, 결국 같은 역할의 코드가 Repository 로 옮겨진것 뿐이기 때문에 재사용성은 떨어짐.
     * 성능은 V3보다 미세하게 좋음
     * @return
     */
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderDto> ordersV4()
    {
        return orderRepository.findOrderDtos();
    }
}

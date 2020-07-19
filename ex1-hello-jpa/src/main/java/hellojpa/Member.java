package hellojpa;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
//@Table(name = "MBR", schema = "dbo")
@SequenceGenerator(name = "member_seq_generator", sequenceName = "member_seq", initialValue = 1, allocationSize = 50)
public class Member extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    private Long id;

    @Column(name = "name", length = 100, nullable = false, columnDefinition = "default 'EMPTY'")
    private String userName;

    @Column
    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    private LocalDate localDate;

    private LocalDateTime localDateTime;

    @Lob
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    // 즉시 로딩
    @Embedded
    private Period period;

    // 즉시 로딩
    @Embedded
    private Address address;

    // 지연 로딩
    @ElementCollection
    @CollectionTable(name = "FAVORITE_FOOD", joinColumns = {
            @JoinColumn(name = "id")
    })
    @Column(name = "FOOD_NAME")
    private Set<String> favoriteFoods = new HashSet<>();

    // 지연 로딩
    @ElementCollection
    @CollectionTable(name = "ADDRESS_HISTORY", joinColumns = {
            @JoinColumn(name = "id")
    })
    private List<Address> addressHistory = new ArrayList<>();

    /**
     * 실제 DB 컬럼과 매핑하지 않고 인메모리에서만 사용할 필드
     */
    @Transient
    private String temp;

    protected Member ()
    {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public Team getTeam() {
        return team;
    }

    // 연관관계 편의 메서드 (중요!!!)
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Set<String> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(Set<String> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

    public List<Address> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<Address> addressHistory) {
        this.addressHistory = addressHistory;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", age=" + age +
                ", roleType=" + roleType +
                ", localDate=" + localDate +
                ", localDateTime=" + localDateTime +
                ", description='" + description + '\'' +
                ", period=" + period +
                ", address=" + address +
                ", favoriteFoods=" + favoriteFoods +
                ", addressHistory=" + addressHistory +
                ", temp='" + temp + '\'' +
                '}';
    }
}

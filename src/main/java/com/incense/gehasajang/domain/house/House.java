package com.incense.gehasajang.domain.house;

import com.incense.gehasajang.domain.Address;
import com.incense.gehasajang.domain.BaseTimeEntity;
import com.incense.gehasajang.domain.Invitation;
import com.incense.gehasajang.domain.booking.Booking;
import com.incense.gehasajang.domain.room.Room;
import com.incense.gehasajang.domain.sms.SmsTemplate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class House extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_id")
    private Long id;

    private String uuid;

    private String name;

    @Embedded
    private Address address;

    private String mainImage;

    private String thumbnailImage;

    private String mainNumber;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "house")
    private List<HostHouse> hostHouses;

    @OneToMany(mappedBy = "house")
    private List<HouseExtraInfo> houseExtraInfos;

    @OneToMany(mappedBy = "house")
    private List<HouseOff> houseOffs;

    @OneToMany(mappedBy = "house")
    private List<Invitation> invitations;

    @OneToMany(mappedBy = "house")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "house")
    private List<Room> rooms;

    @OneToMany(mappedBy = "house")
    private List<SmsTemplate> smsTemplates;

    @Builder
    public House(Long id, String name, Address address, String mainImage, String thumbnailImage, String mainNumber, List<HouseExtraInfo> houseExtraInfos) {
        this.id = id;
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.mainImage = mainImage;
        this.thumbnailImage = thumbnailImage;
        this.mainNumber = mainNumber;
        this.houseExtraInfos = houseExtraInfos;
    }

    @PrePersist
    public void prePersist() {
        address = address == null ? new Address("", "", "", "") : address;
    }
}

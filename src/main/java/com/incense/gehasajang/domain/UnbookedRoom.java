package com.incense.gehasajang.domain;

import com.incense.gehasajang.domain.bed.Bed;
import com.incense.gehasajang.domain.booking.BookingRoomInfo;
import com.incense.gehasajang.domain.room.Room;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class UnbookedRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unbooked_room_id")
    private Long id;

    private LocalDateTime entry_date;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "bed_id")
    private Bed bed;

    private boolean is_down_bed;

    private boolean is_additional_bed;

    private String todayAmount;

    @OneToMany(mappedBy = "unbookedRoom")
    private List<BookingRoomInfo> bookingRoomInfos;

    @OneToOne(mappedBy = "unbookedRoom")
    private BookedRoom bookedRoom;

}
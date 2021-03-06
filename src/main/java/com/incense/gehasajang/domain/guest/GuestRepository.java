package com.incense.gehasajang.domain.guest;

import com.incense.gehasajang.domain.house.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    @Query(nativeQuery = true,
            value = "SELECT b.check_in FROM guest g JOIN booking b ON g.guest_id = b.guest_id WHERE g.guest_id = :guestId AND b.house_id = :houseId ORDER BY b.check_in DESC LIMIT 1")
    LocalDateTime findLastBookingById(@Param(value = "guestId") Long guestId, @Param(value = "houseId") Long houseId);

    // TODO: 2021-02-03 list로 변경할 것
    Set<Guest> findAllByNameAndBookings_House(@Param(value = "name") String name, @Param(value = "house")House house);

    Optional<Guest> findByIdAndBookings_House(@Param(value = "guestId") Long guestId, @Param(value = "house") House house);

}

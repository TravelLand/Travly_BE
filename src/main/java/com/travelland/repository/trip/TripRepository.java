package com.travelland.repository.trip;

import com.travelland.domain.member.Member;
import com.travelland.domain.trip.Trip;
import com.travelland.repository.trip.querydsl.CustomTripRepositoryV2;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long>, CustomTripRepositoryV2 {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trip> findByIdAndIsDeleted(Long tripId, boolean isDeleted);

    List<Trip> findAllByIsDeleted(boolean isDeleted);

    long countByMemberAndIsDeleted(Member member, boolean isDeleted);
}

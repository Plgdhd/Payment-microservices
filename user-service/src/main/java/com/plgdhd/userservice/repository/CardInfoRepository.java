package com.plgdhd.userservice.repository;

import com.plgdhd.userservice.model.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

    @Query("SELECT c FROM CardInfo c WHERE c.user.id = :userId")
    List<CardInfo> findByUserId(@Param("userId") long userId);

}

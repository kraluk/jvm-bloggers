package com.jvm_bloggers.entities.wykop;

import io.vavr.control.Option;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface WykopRepository extends JpaRepository<Wykop, Long> {

    Option<Wykop> findFirstBySentIsFalseAndPostingDateLessThan(final LocalDateTime referenceDate);

}

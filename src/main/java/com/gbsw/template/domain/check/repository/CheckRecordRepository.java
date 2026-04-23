package com.gbsw.template.domain.check.repository;

import com.gbsw.template.domain.check.entity.CheckRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CheckRecordRepository extends JpaRepository<CheckRecord, Long> {

    @Query("""
            SELECT cr FROM CheckRecord cr
            JOIN FETCH cr.student
            JOIN FETCH cr.room r
            JOIN FETCH r.floor f
            JOIN FETCH f.building
            WHERE cr.student.id = :studentId AND cr.date = :date
            """)
    Optional<CheckRecord> findByStudentIdAndDate(Long studentId, LocalDate date);

    @Query("""
            SELECT cr FROM CheckRecord cr
            JOIN FETCH cr.student
            JOIN FETCH cr.room r
            JOIN FETCH r.floor f
            JOIN FETCH f.building
            WHERE cr.student.id = :studentId
            ORDER BY cr.date DESC
            """)
    List<CheckRecord> findAllByStudentIdOrderByDateDesc(Long studentId);

    @Query("""
            SELECT cr FROM CheckRecord cr
            JOIN FETCH cr.student
            JOIN FETCH cr.room r
            JOIN FETCH r.floor f
            JOIN FETCH f.building
            WHERE cr.date = :date
            """)
    List<CheckRecord> findAllByDate(LocalDate date);

    @Query("""
            SELECT cr FROM CheckRecord cr
            JOIN FETCH cr.student
            JOIN FETCH cr.room r
            JOIN FETCH r.floor f
            JOIN FETCH f.building
            WHERE cr.date BETWEEN :startDate AND :endDate
            ORDER BY cr.date DESC
            """)
    List<CheckRecord> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    long countByDate(LocalDate date);
}

package com.gbsw.template.domain.check.entity;

import com.gbsw.template.domain.place.entity.Room;
import com.gbsw.template.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "check_record",
        uniqueConstraints = @UniqueConstraint(name = "uk_check_student_date", columnNames = {"student_id", "date"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CheckRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "checked_at", nullable = false)
    private LocalTime checkedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckStatus status;

    public void updateRoomAndCheckedAt(Room room, LocalTime checkedAt) {
        this.room = room;
        this.checkedAt = checkedAt;
        this.status = CheckStatus.CHECKED;
    }
}

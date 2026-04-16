# 야자 현황 체크 서비스 (YAJA)

경북소프트웨어마이스터고 **야자(야간자율학습) 현황 체크 서비스** 백엔드.

학생이 본인의 야자 위치를 웹에서 체크하고, 담당 교사·관리자가 대시보드에서 전체 현황을 실시간으로 확인할 수 있는 시스템.

- **기획서**: [`야자_현황체크_서비스_기획서.md`](./%EC%95%BC%EC%9E%90_%ED%98%84%ED%99%A9%EC%B2%B4%ED%81%AC_%EC%84%9C%EB%B9%84%EC%8A%A4_%EA%B8%B0%ED%9A%8D%EC%84%9C.md)
- **API 명세서**: [`yaja-api-spec.md`](./yaja-api-spec.md)
- **클라이언트 저장소**: [2026-yaja/yaja-client](https://github.com/2026-yaja/yaja-client)

---

## 배경 및 목적

기존 야자 현황 관리는 각 교실 교탁에 비치된 종이 기록표에 학생이 직접 위치를 기재하고, 야자 담당 교사가 모든 교실을 순회하며 수기로 확인하는 방식이었다. 이 방식은 교사의 순회 부담, 실시간 현황 파악의 어려움, 기록 정확성 저하 등의 문제가 있었다.

본 서비스는 학생이 웹에서 직접 본인의 위치를 체크하고, 교사·관리자가 대시보드에서 전체 현황을 실시간으로 확인할 수 있도록 하여 이러한 불편을 해소하는 것을 목적으로 한다.

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.4.3 |
| Security | Spring Security + JWT (JJWT 0.12.5) |
| ORM | Spring Data JPA |
| Database | MySQL |
| Docs | Springdoc OpenAPI (Swagger UI) |
| Build | Gradle |

---

## 사용자 역할

| 역할 | 주요 기능 |
|------|-----------|
| **학생** | 본인 야자 위치 체크/수정, 오늘 위치·이력 조회 |
| **교사 (야자 담당)** | 전체 학생 현황 조회, 미체크 학생 확인, 수동 수정, CSV 내보내기 |
| **관리자** | 학생/교사 계정 관리, 장소 목록 관리, 운영 시간 설정 |

> API 구현 상의 `role` 값은 `student` / `admin` 두 종류이며, 교사 기능은 관리자 권한에 통합되어 제공됩니다 (`yaja-api-spec.md` 기준).

---

## 주요 기능

### 학생
- 야자 시작 전/도중 본인 위치 체크 제출
- 위치 이동 시 재선택 (Upsert)
- 오늘 체크 기록 및 히스토리 조회

### 교사·관리자
- 실시간 대시보드 (전체/체크/미체크 집계)
- 장소별 학생 현황 (인원 내림차순)
- 미체크 학생 목록 조회
- 특정 학생의 위치 수동 입력·수정
- 날짜 및 기간 범위별 현황 CSV/JSON 내보내기

### 관리자 (추가)
- 학생·교사 계정 등록·수정·삭제
- 야자 장소(건물·층·방) 관리
- 야자 운영 시간 설정

---

## 위치 (장소) 구성

| 건물 | 층 | 주요 장소 |
|------|----|-----------|
| **호연관** | 1F | 늘봄도서관, 세미나실, 콘퍼런스홀, 영어실, 가사실, 서버실 |
| | 2F | 음악실, 컴퓨터교육실, 메이커교육실, SW개발부, LAB I, 동아리실 |
| | 3F | 진로상담실, 전문교육부, NCS소프트웨어프로젝트실습실 I, NCS모바일프로그래밍실습실 I, NCS임베디드플랫폼실습실 I, NCS응용프로그래밍실습실 I, LAB IV, LAB V |
| | 4F | Wee클래스, SW채움교실, NCS응용프로그래밍실습실 II, NCS게임콘텐츠제작실습실 I, LAB VI, LAB VII, 휴게테크 |
| **정심관** | - | 기숙사 |
| **기타** | - | 교외, 기타(직접 입력) |

---

## 프로젝트 구조

```
com.gbsw.template/
├── domain/
│   ├── auth/      # 인증 (로그인, 토큰)
│   ├── health/    # 헬스체크
│   └── user/      # 사용자 엔티티·리포지토리
└── global/
    ├── common/    # ApiResponse<T> 공통 응답 래퍼
    ├── exception/ # ErrorCode, CustomException, GlobalExceptionHandler
    └── security/  # JWT 필터, SecurityConfig, 인증/인가 핸들러
```

향후 도메인 추가 예정: `building`, `floor`, `room`, `check_record` (학생 출석), 관리자 기능 모듈.

---

## 응답 포맷

모든 API 응답은 공통 `ApiResponse<T>` 포맷을 따른다.

**성공**
```json
{ "success": true, "data": { ... } }
```

**실패**
```json
{ "success": false, "error": { "code": "INVALID_CREDENTIALS", "message": "이메일 또는 비밀번호가 올바르지 않습니다." } }
```

주요 에러 코드: `MISSING_FIELDS`, `INVALID_DATE_FORMAT`, `UNAUTHORIZED`, `INVALID_CREDENTIALS`, `FORBIDDEN`, `USER_NOT_FOUND`, `STUDENT_NOT_FOUND`, `ROOM_NOT_FOUND`, `INTERNAL_SERVER_ERROR`.

---

## 주요 API 엔드포인트

상세 명세는 [`yaja-api-spec.md`](./yaja-api-spec.md) 참고.

| 메서드 | 경로 | 역할 | 설명 |
|--------|------|------|------|
| `POST` | `/auth/login` | 공통 | 로그인 |
| `GET` | `/buildings` | 공통 | 건물·층·장소 계층 조회 |
| `GET` | `/students/{id}/check?date=` | student | 특정일 출석 기록 조회 |
| `POST` | `/students/{id}/check` | student | 출석 체크 제출 (Upsert) |
| `GET` | `/students/{id}/history` | student | 본인 출석 히스토리 |
| `GET` | `/admin/stats?date=` | admin | 일일 통계 |
| `GET` | `/admin/room-stats?date=` | admin | 장소별 출석 현황 |
| `GET` | `/admin/unchecked?date=` | admin | 미출석 학생 목록 |
| `GET` | `/admin/records` | admin | 출석 기록 조회 (단일일/기간) |
| `POST` | `/admin/check` | admin | 수동 출석 입력 |

---

## 데이터 모델 (기획서 기준)

| 테이블 | 설명 |
|--------|------|
| `user` | 학생/교사 계정 (type ENUM, admission_year, grade, class, name) |
| `location` | 야자 장소 목록 (floor, name) |
| `schedule` | 날짜별 학생의 장소 신청 (`date` + `user_id` UNIQUE) |
| `attendance_check` | 예약에 대한 교사 확인 여부 (`schedule_id` 1:1) |

관계: `user 1:N schedule N:1 location`, `schedule 1:1 attendance_check`

> 실제 구현은 API 명세에 맞춰 `building`/`floor`/`room` 계층 및 `check_record` 모델로 발전할 예정 (`CLAUDE.md` 참고).

---

## 빌드 및 실행

### 환경변수

```bash
DB_NAME=yaja
DB_USERNAME=...
DB_PASSWORD=...
JWT_SECRET_KEY=<Base64 인코딩된 시크릿>
JWT_ACCESS_TOKEN_EXPIRATION=1800000    # 30분
JWT_REFRESH_TOKEN_EXPIRATION=604800000 # 7일
```

### 명령어

```bash
# 빌드 (테스트 제외)
./gradlew build -x test

# 앱 실행
./gradlew bootRun

# 클린 빌드
./gradlew clean build
```

### API 문서

앱 실행 후 Swagger UI에서 확인:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Docs: `http://localhost:8080/v3/api-docs`


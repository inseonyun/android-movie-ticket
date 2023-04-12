# android-movie-ticket

## step1

### UI
- [x] 영화를 리스트에 보여준다.
  - [x] 영화 예매 버튼을 누르면 상세 정보 화면으로 이동한다.
- [x] 영화의 상세 정보를 보여준다.
  - [x] 예약할 인원을 선택할 수 있다.
  - [x] 예매 완료 버튼을 누르면 예약 완료 화면으로 이동한다.
  - [x] 뒤로가기 버튼을 누르면 영화 목록 화면으로 이동한다.
- [x] 최종 예약 정보를 보여준다.
  - [x] 영화 결제 금액을 보여준다.
  - [x] 영화 정보를 보여준다.
  - [x] 뒤로가기 버튼을 누르면 영화 목록 화면으로 이동한다.

### Domain
- [x] 구매할 티켓에 대한 가격을 계산한다.

## step2

### UI
- [ ] 영화 상영일 기간을 보여준다.
  - [ ] 영화의 상영일은 각자의 범위를 갖는다.
- [ ] 영화 상세 정보 화면에서 날짜와 시간을 선택할 수 있다.
- [ ] 최종 예약 정보를 보여준다.
  - [ ] 영화 상영 날짜와 시간을 보여준다.
  - [ ] 할인된 영화 금액을 보여준다.
- [ ] 화면이 회전되어도 입력한 정보를 유지한다.

### Domain
- [ ] 주말은 오전 9시, 평일은 오전 10시부터 자정까지 두 시간 간격으로 상영한다.
  - [x] 현재 시간 이전의 정보는 제외한다.
  - [ ] 현재 날짜 이전의 정보는 제외한다.
  - [x] 시간 기본값은 현재 시간 바로 직후이다.
  - [ ] 날짜 기본값은 금일이다.
- [ ] 할인 조건에 따라 적절한 할인 정책이 적용된다.
  - [ ] 무비데이(매월 10, 20, 30일)일 때: 10% 할인
  - [ ] 조조(11시 이전)/야간(20시 이후)일 때: 2,000원 할인
  - [ ] 두 조건은 겹칠 수 있고 무비데이 할인이 선적용되어야 한다.

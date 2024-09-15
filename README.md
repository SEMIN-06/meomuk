<div align="center">
  <h3 align="center">오늘 뭐먹지?</h3>

  <p align="center">
    학교 수행평가로 개발 한 안드로이드 어플리케이션
  </p>
</div>

<!-- ABOUT THE PROJECT -->

## 프로젝트 설명

고등학교 3학년 1학기 수행평가로 개발 한 오늘 뭐먹을지를 추천해주는 앱 입니다.
<br/>
<br/>
단순히 뭐를 먹을지 추천해 주는 기능뿐 아니라, 메뉴의 레시피 확인, 역사 확인, 리뷰 남기기와 사용자가 직접 메뉴를 추가 할 수 있으며, 마법의 소라고동으로 랜덤한 답변을 받아볼 수 있는 기능까지 포함되어 있습니다.
<br/>
<br/>
파이어베이스의 파이어스토어 데이터베이스를 사용하고 있기 때문에, 외부에서도 데이터를 접근할 수 있습니다.
<br/>
<br/>
처음 코틀린과 안드로이드 개발에 대해 배운 후 개발은 개인으로 진행 한 프로젝트 입니다.

### 개발 환경

- Mackbook Pro M1 Pro
- Android Studio Iguana

### 시연 영상 (사진 클릭 시 유튜브로 이동합니다.)

[![시연 영상](https://img.youtube.com/vi/KmBhbCywI88/0.jpg)](https://www.youtube.com/watch?v=KmBhbCywI88 '시연 영상')

### 데이터베이스 구조

### 컬렉션:

#### 1. `foods`

- **문서 ID:** 자동 생성
- **필드:**
  - `name`: String
  - `desc`: String
  - `imageUrl`: String
  - `recipeContent`: String
  - `historyContent`: String
- **서브 컬렉션:**
  - `comments`
    - `rating`: Number
    - `text`: String
    - `timestamp`: Timestamp
    - `userName`: String

### 이 프로젝트에 사용한 것들

- Kotlin
- Firebase Firestore Database
- Firebase Storage

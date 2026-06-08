# LifeTrace 設計書

## アプリ概要

毎日の定時刻に通知を送り、ユーザーにその瞬間の考えや気持ちを問いかけるアプリ。
入力内容は日記として時系列で保存・閲覧できる。

---

## コアコンセプト

- **「今何を考えている？」という問いかけ** を毎日決まった時間に受け取る
- 負担なく短文でも答えられる設計（強制ではなく、通知を見て思ったことを記録）
- 過去の記録を振り返ることで、自分の思考の変化に気づける

---

## 機能一覧

### MVP（最初に作る機能）

| 機能 | 説明 |
|------|------|
| 通知スケジュール設定 | 1日1〜3回、任意の時刻を設定できる |
| 通知受信 | 「今何を考えていますか？」という問いかけ通知 |
| 通知からの直接入力 | 通知タップでアプリが開き、すぐ入力できる |
| 記録の保存 | テキスト＋日時をローカルDBに保存 |
| タイムライン表示 | 過去の記録を日付ごとに一覧で見る |
| 記録の編集・削除 | 保存済みの記録を修正・削除できる |

### Phase 2（後で追加する機能）

| 機能 | 説明 |
|------|------|
| 問いかけのバリエーション | 「最近嬉しかったことは？」など複数の質問をランダムで出す |
| タグ付け | 記録にカテゴリタグをつけて絞り込み検索 |
| 検索 | キーワードで過去の記録を検索 |
| 気分トラッカー | 記録時に気分（5段階など）を添付 |
| 統計・カレンダー表示 | 記録した日のカレンダー、継続日数の表示 |
| バックアップ（エクスポート） | テキスト/JSONで記録をエクスポート |

---

## アーキテクチャ

```
MVVM + Clean Architecture（軽量版）

app/
├── data/
│   ├── local/
│   │   ├── EntryDatabase.kt         # Room DB定義
│   │   ├── EntryDao.kt              # DAOインターフェース
│   │   └── EntryEntity.kt          # DBエンティティ
│   └── repository/
│       └── EntryRepository.kt      # データアクセス抽象化
├── domain/
│   └── model/
│       └── Entry.kt                # ドメインモデル
├── notification/
│   ├── NotificationReceiver.kt     # BroadcastReceiver（通知発火）
│   ├── NotificationScheduler.kt   # AlarmManagerによるスケジュール管理
│   └── NotificationHelper.kt      # 通知チャンネル・表示ロジック
├── ui/
│   ├── home/
│   │   ├── HomeScreen.kt           # タイムライン画面
│   │   └── HomeViewModel.kt
│   ├── editor/
│   │   ├── EditorScreen.kt         # 入力・編集画面
│   │   └── EditorViewModel.kt
│   ├── settings/
│   │   ├── SettingsScreen.kt       # 通知時刻設定画面
│   │   └── SettingsViewModel.kt
│   └── theme/                      # 既存
├── MainActivity.kt
└── LifeTraceApp.kt                 # Application クラス
```

---

## データモデル

### Entry（記録）

```kotlin
data class Entry(
    val id: Long = 0,
    val content: String,          // 記録本文
    val prompt: String,           // 問いかけ文（通知から開いた場合に保持）
    val createdAt: LocalDateTime, // 作成日時
    val updatedAt: LocalDateTime, // 更新日時
)
```

### NotificationSchedule（通知設定）

SharedPreferences に保存（DBは不要）

```
key: "notification_times"
value: ["08:00", "12:00", "21:00"]  // 最大3件
```

---

## 通知フロー

```
AlarmManager（繰り返し設定）
    ↓ 設定時刻になる
BroadcastReceiver（NotificationReceiver）
    ↓ 通知を生成
NotificationManager（通知表示）
    ↓ ユーザーがタップ
MainActivity（Deep Link / Intent Extra で EditorScreen を開く）
    ↓ ユーザーが入力して保存
Room DB（EntryEntity として永続化）
```

### 通知の詳細

- **チャンネルID**: `lifetrace_daily_prompt`
- **チャンネル名**: 「今日の問いかけ」
- **内容**: タイトル「今何を考えていますか？」/ 本文はランダム補助テキスト
- **タップ時の挙動**: EditorScreen を `prompt_id` 付きで起動

---

## 画面設計

### 1. ホーム画面（タイムライン）

```
┌─────────────────────────┐
│ LifeTrace           [+] │  ← FABで新規記録
├─────────────────────────┤
│ 2026年6月8日             │
│ ┌───────────────────┐   │
│ │ 21:00             │   │
│ │ 今日は仕事で...    │   │
│ └───────────────────┘   │
│ ┌───────────────────┐   │
│ │ 12:00             │   │
│ │ ランチで友達と...  │   │
│ └───────────────────┘   │
│                         │
│ 2026年6月7日             │
│ ┌───────────────────┐   │
│ │ 08:00             │   │
│ │ 朝から天気が...    │   │
│ └───────────────────┘   │
└─────────────────────────┘
```

### 2. エディタ画面（入力・編集）

```
┌─────────────────────────┐
│ ← 今何を考えていますか？ │
├─────────────────────────┤
│                         │
│  [テキスト入力エリア]    │
│                         │
│                         │
├─────────────────────────┤
│         [保存する]      │
└─────────────────────────┘
```

### 3. 設定画面

```
┌─────────────────────────┐
│ ← 設定                  │
├─────────────────────────┤
│ 通知時刻                │
│  [08:00]  [削除]        │
│  [21:00]  [削除]        │
│  [+ 時刻を追加]         │
│                         │
│ (最大3件まで)            │
└─────────────────────────┘
```

---

## 使用ライブラリ

| ライブラリ | 用途 |
|-----------|------|
| Jetpack Compose | UI |
| Room | ローカルDB（記録の永続化） |
| AlarmManager | 通知スケジュール管理 |
| DataStore Preferences | 通知時刻設定の保存 |
| Navigation Compose | 画面遷移 |
| Hilt | DI（依存性注入） |
| Kotlin Coroutines / Flow | 非同期処理 |

---

## 権限

```xml
<!-- AndroidManifest.xml に追加が必要 -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />      <!-- API 33+ -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />  <!-- 再起動後の復元 -->
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />   <!-- AlarmManager正確な時刻 -->
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />        <!-- API 33+ 代替 -->
```

---

## 実装順序（フェーズ）

### Phase 1：基盤（1〜2日）
1. Hilt・Room・Navigation のセットアップ
2. Entry エンティティ・DAO・Repository の実装
3. ホーム画面（タイムライン）の UI と ViewModel

### Phase 2：コア機能（1〜2日）
4. エディタ画面の UI と ViewModel（新規作成・編集・削除）
5. 通知チャンネルの設定と NotificationHelper
6. AlarmManager によるスケジュール管理

### Phase 3：通知連携（1日）
7. BroadcastReceiver の実装（起動・再起動後の復元）
8. 通知タップ → エディタ画面への遷移（Deep Link）
9. 設定画面（通知時刻の追加・削除）

### Phase 4：仕上げ（1日）
10. エラーハンドリングと権限リクエスト（POST_NOTIFICATIONS）
11. UI の磨き込み（アニメーション・空状態）
12. 動作テスト・バグ修正

---

## 未決事項・検討点

- **問いかけ文のバリエーション**: まずは固定文「今何を考えていますか？」で開始し、Phase 2でランダム化を検討
- **通知の再スケジュール**: 端末再起動時は `RECEIVE_BOOT_COMPLETED` で AlarmManager を再設定する
- **Android 14以降の制限**: `SCHEDULE_EXACT_ALARM` の動的権限リクエストが必要な場合あり（設定画面で案内）
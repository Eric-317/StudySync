# StudySync 學習平台 - 執行指南

## 📚 專案簡介

StudySync 是一個功能完整的學習管理平台，整合了任務管理、番茄鐘計時器、行事曆、音樂播放等功能，幫助使用者提升學習效率和專注力。

### 🌟 主要功能
- ✅ **任務管理**: 新增、編輯、刪除學習任務，支援分類和篩選
- 🍅 **番茄鐘**: 25 分鐘專注計時，支援背景影片播放
- 📅 **行事曆**: 事件管理和任務整合顯示
- 🎵 **音樂播放**: 背景音樂播放，營造學習氛圍
- 👤 **使用者系統**: 註冊、登入、個人資料管理
- 💾 **資料庫支援**: SQLite（預設）+ MySQL（可選）

## 🚀 快速開始

#### 1. 環境檢查
確保您的系統已安裝：
- **Java 8 或更新版本**
- **IntelliJ IDEA**（推薦）或其他 Java IDE

#### 2. 依賴庫檢查
專案需要以下 JAR 檔案（已包含在 `libs/` 目錄中）：
- `sqlite-jdbc-3.45.3.0.jar` - SQLite 資料庫驅動
- `slf4j-api-2.0.7.jar` - 日誌介面
- `slf4j-simple-2.0.7.jar` - 日誌實作

#### 3. 編譯和執行
```bash
# 1. 編譯專案
javac -cp "libs/*:src" -d build src/com/studysync/view/MainWindow.java

# 2. 執行應用程式
java -cp "libs/*:build" com.studysync.view.MainWindow
```

### 🏗️ IDE 設定指引

#### IntelliJ IDEA 設定
1. **開啟專案**: File → Open → 選擇 studysync 資料夾
2. **設定 Libraries**:
   - File → Project Structure → Libraries
   - 點擊 "+" → Java → 選擇 `libs` 資料夾
   - 套用所有 JAR 檔案
3. **執行設定**:
   - 主類別: `com.studysync.view.MainWindow`
   - 類別路徑: 確保包含 libs 中的所有 JAR 檔案

## 🗄️ 資料庫設定

### SQLite（預設，推薦）
- **自動設定**: 首次啟動時自動建立 `studysync.db`
- **位置**: 專案根目錄
- **優點**: 免設定、輕量化、適合個人使用

### MySQL（進階選項）
如需使用 MySQL，請：

1. **下載 MySQL 驅動**:
   ```bash
   # 下載 mysql-connector-java-8.0.33.jar
   # 放置到 libs/ 目錄中
   ```

2. **建立資料庫**:
   ```sql
   CREATE DATABASE studysync CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **應用程式設定**:
   - 啟動應用程式
   - 設定 → 資料庫設定 → 選擇 MySQL
   - 輸入連線資訊

## 🎯 使用指南

### 初次使用
1. **啟動應用程式**: 使用上述任一方法啟動
2. **建立帳號**: 點擊「註冊」建立新使用者帳號
3. **登入系統**: 使用註冊的帳號密碼登入
4. **開始使用**: 探索各項功能

### 主要功能使用

#### 📋 任務管理
- **新增任務**: 點擊「+ Add Task」按鈕
- **管理分類**: 使用分類下拉選單新增、編輯、刪除分類
- **篩選任務**: 
  - `All`: 顯示所有任務
  - `Today`: 顯示今日任務
  - `Completed`: 顯示已完成任務
- **任務操作**: 點擊任務項目進行編輯或標記完成

#### 🍅 番茄鐘
- **開始計時**: 點擊圓形計時器開始 25 分鐘倒數
- **暫停/繼續**: 使用 Pause 按鈕控制
- **重置計時**: 使用 Reset 按鈕重新開始
- **調整時間**: 使用滑桿設定自訂時間
- **背景影片**: 點擊「設定背景影片」選擇 MP4 檔案

#### 📅 行事曆
- **檢視事件**: 月曆視圖顯示事件和任務
- **新增事件**: 點擊日期新增行事曆事件
- **導航**: 使用左右箭頭切換月份
- **事件詳情**: 右側面板顯示選定日期的事件列表

#### 🎵 音樂播放
- **新增音樂**: 點擊「新增音樂」匯入音訊檔案
- **播放控制**: 播放、暫停、上一首、下一首
- **音量控制**: 使用音量滑桿和靜音按鈕
- **管理清單**: 雙擊音樂項目刪除

## 🏗️ 程式架構說明

### 分層架構設計
StudySync 採用經典的 MVC（Model-View-Controller）分層架構：

```
src/com/studysync/
├── model/           # 資料模型層
│   ├── User.java         # 使用者資料模型
│   ├── Task.java         # 任務資料模型
│   └── CalendarEvent.java # 行事曆事件模型
├── view/            # 使用者介面層
│   ├── MainWindow.java    # 主視窗（已註解）
│   ├── LoginPanel.java    # 登入面板
│   ├── TaskPanel.java     # 任務管理面板
│   └── ...              # 其他 UI 元件
├── controller/      # 控制層
│   ├── UserController.java # 使用者控制器（已註解）
│   ├── TaskController.java # 任務控制器（已註解）
│   └── ...               # 其他控制器
├── service/         # 業務邏輯層
│   ├── UserService.java   # 使用者服務（已註解）
│   ├── TaskService.java   # 任務服務
│   └── ...              # 其他服務
├── repository/      # 資料存取層
│   ├── UserRepository.java # 使用者資料存取（已註解）
│   ├── TaskRepository.java # 任務資料存取
│   └── ...               # 其他資料存取
└── util/            # 工具類
    ├── DBUtil.java       # 資料庫工具（已註解）
    ├── AppSettings.java  # 應用設定（已註解）
    └── DatabaseInitializer.java # 資料庫初始化（已註解）
```

### 已加註解的核心類別

#### 🔧 工具類（Util Layer）
- **AppSettings.java**: 應用程式設定管理，單例模式實現
- **DBUtil.java**: 資料庫連線管理，支援 SQLite/MySQL 雙資料庫
- **DatabaseInitializer.java**: 資料庫表結構初始化

#### 📊 資料模型（Model Layer）
- **User.java**: 使用者資料模型，包含完整的 getter/setter
- **Task.java**: 任務資料模型，支援多種建構方式
- **CalendarEvent.java**: 行事曆事件模型，整合任務顯示

#### 🎮 控制層（Controller Layer）
- **UserController.java**: 使用者相關操作控制
- **TaskController.java**: 任務管理操作控制
- **CalendarEventController.java**: 行事曆事件控制器

#### 🔄 服務層（Service Layer）
- **UserService.java**: 使用者業務邏輯處理
- **TaskService.java**: 任務業務邏輯服務
- **CalendarEventService.java**: 行事曆事件業務邏輯
- **CategoryService.java**: 分類管理服務

#### 💾 資料存取層（Repository Layer）
- **UserRepository.java**: 使用者資料庫操作
- **CalendarEventRepository.java**: 行事曆事件資料庫操作
- **TaskRepository.java**: 任務資料庫操作
- **CategoryRepository.java**: 分類資料庫操作

#### 🖥️ 介面層（View Layer）
- **MainWindow.java**: 主視窗及導航控制
- **TaskPanel.java**: 任務管理面板
- **CalendarPanel.java**: 行事曆面板
- **DashboardPanel.java**: 儀表板面板
- **LoginPanel.java**: 登入面板
- **PomodoroPanel.java**: 番茄鐘面板
- **MusicPanel.java**: 音樂播放面板

## 📁 檔案結構說明

```
studysync/
├── src/                          # 原始碼目錄
│   └── com/studysync/
│       ├── controller/           # 控制層
│       ├── model/               # 資料模型
│       ├── repository/          # 資料存取層
│       ├── service/             # 業務邏輯層
│       ├── util/                # 工具類
│       ├── view/                # 使用者介面
│       └── assets/              # 資源檔案
├── libs/                        # 外部依賴 JAR 檔案
├── app_settings.properties      # 應用程式設定檔
├── studysync.db                 # SQLite 資料庫檔案
├── run.bat                      # Windows 執行腳本
├── run.sh                       # Unix/Linux 執行腳本
├── README.md                    # 專案說明文件
└── 程式碼註解報告.md             # 程式碼註解完成報告
```

## 🛠️ 疑難排解

### 常見問題

#### Q1: 啟動時出現 "找不到主類別" 錯誤
**解決方案**:
1. 確認類別路徑正確包含 libs 中的 JAR 檔案
2. 檢查主類別名稱: `com.studysync.view.MainWindow`
3. 重新編譯專案

#### Q2: 資料庫連線失敗
**解決方案**:
1. **SQLite**: 檢查 `sqlite-jdbc-3.45.3.0.jar` 是否在 libs 目錄中
2. **MySQL**: 確認 MySQL 服務正在運行，連線資訊正確
3. 檢查資料庫權限設定

#### Q3: 無法播放音樂或影片
**解決方案**:
1. 確認檔案格式支援（音樂: WAV, MP3；影片: MP4）
2. 檢查檔案路徑是否正確
3. 確認檔案沒有被其他應用程式占用

#### Q4: UI 顯示異常
**解決方案**:
1. 檢查 Java 版本（建議 Java 8+）
2. 重新啟動應用程式
3. 清除暫存檔案後重新執行

### 取得協助
如果遇到其他問題，請檢查：
1. 📋 [程式碼註解報告](./程式碼註解報告.md) - 了解程式架構
2. 🔍 控制台錯誤訊息 - 查看詳細錯誤資訊
3. 📂 日誌檔案 - 檢查應用程式日誌

## 🔄 更新日誌

### 版本 1.0.0
- ✅ 完成核心功能開發
- ✅ 添加完整的中文程式碼註解
- ✅ 支援 SQLite 和 MySQL 雙資料庫
- ✅ 實現 MVC 分層架構
- ✅ 完善錯誤處理和使用者體驗

## 📝 開發資訊

- **開發語言**: Java
- **資料庫**: SQLite (預設) / MySQL (可選)
- **架構模式**: MVC (Model-View-Controller)
- **開發工具**: IntelliJ IDEA
- **版本控制**: Git

## 📄 授權聲明

本專案為學習用途開發，遵循開源精神。

---

**StudySync 開發團隊** - 讓學習更有效率！ 🚀

如有任何問題或建議，歡迎聯繫開發團隊。
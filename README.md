# StudySync 學習平台 - 執行指南

## 概述
StudySync 是一個基於 Java Swing 開發的學習管理平台，具備任務管理、番茄鐘計時、音樂播放、日曆事件等功能。

## 系統需求

### 必要環境
- **Java 運行環境**: Java 11 或更新版本
- **作業系統**: Windows 10/11, macOS, 或 Linux
- **記憶體**: 至少 512MB RAM
- **硬碟空間**: 至少 100MB 可用空間

### 依賴套件
專案已包含以下必要的 JAR 檔案在 `libs/` 目錄中：
- `sqlite-jdbc-3.45.3.0.jar` - SQLite 資料庫驅動（必需）
- `slf4j-api-2.0.7.jar` - 日誌 API
- `slf4j-simple-2.0.7.jar` - 簡易日誌實現

## 執行方式

### 方式一：使用 Java 命令直接執行

#### Windows
```cmd
java -cp ".;src;libs\sqlite-jdbc-3.45.3.0.jar;libs\slf4j-api-2.0.7.jar;libs\slf4j-simple-2.0.7.jar" com.studysync.view.MainWindow
```

#### Linux/macOS
```bash
java -cp ".:src:libs/sqlite-jdbc-3.45.3.0.jar:libs/slf4j-api-2.0.7.jar:libs/slf4j-simple-2.0.7.jar" com.studysync.view.MainWindow
```

### 方式二：在 IntelliJ IDEA 中執行
1. 開啟 IntelliJ IDEA
2. 匯入專案
3. 確認專案設定中已添加 `libs/` 目錄下的所有 JAR 檔案
4. 找到 `src/com/studysync/view/MainWindow.java`
5. 右鍵點擊 → Run 'MainWindow.main()'

## 初次啟動流程

1. **資料庫配置**
   - 程式啟動時會顯示資料庫選擇對話框
   - 可選擇 SQLite（本地）或 MySQL（遠程）
   - 建議初次使用選擇 SQLite

2. **使用者註冊/登入**
   - 初次使用需要註冊帳號
   - 輸入 Email、密碼和出生日期
   - 註冊成功後可以登入使用

3. **功能探索**
   - 儀表板：查看學習概況
   - 任務：管理學習任務
   - 番茄鐘：專注時間管理
   - 音樂：背景音樂播放
   - 日曆：事件管理
   - 設定：個人設定調整

## 檔案結構說明

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
├── studysync.db                # SQLite 資料庫檔案
├── run.bat                     # Windows 執行腳本
├── run.sh                      # Linux/macOS 執行腳本
└── README.md                   # 本說明檔案
```

## 設定檔說明

### app_settings.properties
包含應用程式的各項設定：
- `pomodoro.video.path`: 番茄鐘背景影片路徑
- `custom.music.paths`: 自訂音樂檔案路徑（用分號分隔）
- `notifications.enabled`: 是否啟用通知
- `theme`: 主題設定
- `language`: 語言設定

## 常見問題排除

### 1. Java 版本問題
**錯誤**: `UnsupportedClassVersionError`
**解決**: 確認安裝 Java 11 或更新版本

### 2. 找不到 JAR 檔案
**錯誤**: `ClassNotFoundException`
**解決**: 確認 `libs/` 目錄中包含所有必要的 JAR 檔案

### 3. 資料庫連線問題
**錯誤**: 資料庫初始化失敗
**解決**: 
- 確認 SQLite JDBC 驅動已正確載入
- 檢查檔案讀寫權限
- 如使用 MySQL，確認服務器運行狀態

### 4. 字體顯示問題
**錯誤**: 中文字體顯示異常
**解決**: 確認系統已安裝中文字體（如微軟正黑體）

## 開發環境設定

如需進行開發或除錯：

1. **IDE 配置**
   - 使用 IntelliJ IDEA 或 Eclipse
   - 設定 JDK 版本為 11 或以上
   - 將 `libs/` 目錄添加到專案依賴

2. **建置工具**
   - 可考慮使用 Maven 或 Gradle 進行依賴管理
   - 當前專案使用傳統的 JAR 檔案管理方式

## 聯絡資訊

如有問題或建議，請聯絡開發團隊。

---
**StudySync 學習平台** - 讓學習更有效率！

@echo off
echo 正在啟動 StudySync...
java -jar studysync.jar
if %errorlevel% neq 0 (
    echo.
    echo 執行失敗！可能的原因：
    echo 1. 您的電腦沒有安裝 Java
    echo 2. Java 版本太舊（需要 Java 8 或更新版本）
    echo.
    echo 請到以下網址下載並安裝 Java：
    echo https://www.java.com/zh-TW/download/
    echo.
    pause
)

@echo off
cls
echo ===================================================================
echo                 AUTHGATE - SECURE RESIDENTIAL PORTAL
echo ===================================================================
echo.
echo   1. Portal Address:     http://localhost:8080
echo   2. Database Console:   http://localhost:8080/h2-console
echo.
echo   ---------------------------------------------------------------
echo   Demo Login Accounts:
echo     - Admin:        username: admin     / password: admin123
echo     - Host Resident:username: resident1 / password: pass123
echo     - Gate Guard:   username: guard     / password: guard123
echo   ---------------------------------------------------------------
echo.
echo   Starting server in quiet mode... (Please wait a few seconds)
echo.
.\mvnw spring-boot:run -q

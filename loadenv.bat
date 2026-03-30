@echo off
if not exist .env (
    echo .env file not found.
    exit /b
)
for /f "tokens=*" %%a in (.env) do set %%a
echo Environment variables loaded.

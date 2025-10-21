@echo off
echo =========================================
echo Empaquetando CumBBMApp para escritorio...
echo =========================================

:: Ir a la carpeta donde está este archivo (.bat)
cd /d %~dp0

:: Ejecutar el empaquetado multiplataforma
call gradlew :composeApp:packageReleaseDistribution

echo.
echo =========================================
echo ✅ Listo: Instaladores generados en:
echo   composeApp\build\compose\binaries
echo =========================================

pause

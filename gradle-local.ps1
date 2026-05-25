$root = Split-Path $PSScriptRoot -Parent
& "$root\gradlew.bat" -p $PSScriptRoot @args
exit $LASTEXITCODE

Get-ChildItem -Filter naturescompass_*.json | ForEach-Object { $content = Get-Content $_.FullName -Raw; $content = $content -replace '"nature_compass/', '"item/nature_compass/'; Set-Content $_.FullName $content }


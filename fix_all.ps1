for ($i = 0; $i -le 31; $i++) {
    $filename = "naturescompass_{0:D2}.json" -f $i
    $content = @"
{
    "parent": "item/generated",
    "textures": {
        "layer0": "item/nature_compass/naturescompass_{0:D2}"
    }
}
"@ -f $i
    Set-Content -Path $filename -Value $content
}


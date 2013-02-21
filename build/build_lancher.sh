#!/bin/sh

# ../res/drawable-hdpi/ic_launcher.png:  PNG image data, 72 x 72, 8-bit/color RGBA, non-interlaced
# ../res/drawable-ldpi/ic_launcher.png:  PNG image data, 36 x 36, 8-bit/color RGBA, non-interlaced
# ../res/drawable-mdpi/ic_launcher.png:  PNG image data, 48 x 48, 8-bit/color RGBA, non-interlaced
# ../res/drawable-xhdpi/ic_launcher.png: PNG image data, 96 x 96, 8-bit/color RGBA, non-interlaced


convert  -background none -resize 72x72 main.svg ../res/drawable-hdpi/ic_launcher.png
convert  -background none -resize 36x36 main.svg ../res/drawable-ldpi/ic_launcher.png
convert  -background none -resize 48x48 main.svg ../res/drawable-mdpi/ic_launcher.png
convert  -background none -resize 96x96 main.svg ../res/drawable-xhdpi/ic_launcher.png



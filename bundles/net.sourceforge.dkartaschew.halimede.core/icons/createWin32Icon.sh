#!/bin/sh
mkdir temp; 

# Convert SVG to PNG
inkscape -z -e temp/icon16.png -w 16 -h 16 application-icon.svg; 
inkscape -z -e temp/icon32.png -w 32 -h 32 application-icon.svg; 
inkscape -z -e temp/icon48.png -w 48 -h 48 application-icon.svg; 
inkscape -z -e temp/icon256.png -w 256 -h 256 application-icon.svg; 

# Remove transparency from smaller images
convert temp/icon16.png -background white -alpha remove temp/icon16_w.png
convert temp/icon32.png -background white -alpha remove temp/icon32_w.png
convert temp/icon48.png -background white -alpha remove temp/icon48_w.png

# Convert PNG to BMP of correct depths. (Note: Eclipse branding requires BMP3 format in ICO).
convert temp/icon16_w.png -type palette -background white -depth 8 BMP3:temp/icon16_8.bmp
convert temp/icon32_w.png -type palette -background white -depth 8 BMP3:temp/icon32_8.bmp
convert temp/icon48_w.png -type palette -background white -depth 8 BMP3:temp/icon48_8.bmp
convert temp/icon16.png -define bmp3:alpha=true BMP3:temp/icon16_32.bmp
convert temp/icon32.png -define bmp3:alpha=true BMP3:temp/icon32_32.bmp
convert temp/icon48.png -define bmp3:alpha=true BMP3:temp/icon48_32.bmp
convert temp/icon256.png -define bmp3:alpha=true BMP3:temp/icon256_32.bmp

# Merge BMPs as ICO
convert \
	temp/icon16_32.bmp \
	temp/icon16_8.bmp \
	temp/icon32_32.bmp \
	temp/icon32_8.bmp \
	temp/icon48_32.bmp \
	temp/icon48_8.bmp \
	temp/icon256_32.bmp \
	application-icon.ico; 

rm -rf temp;
cp application-icon.ico ../../../releng/net.sourceforge.dkartaschew.halimede.product/icons
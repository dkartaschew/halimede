#!/bin/sh
mkdir temp; 
inkscape -z -e temp/icon16.png -w 16 -h 16 application-icon.svg; 
inkscape -z -e temp/icon24.png -w 24 -h 24 application-icon.svg; 
inkscape -z -e temp/icon32.png -w 32 -h 32 application-icon.svg; 
inkscape -z -e temp/icon48.png -w 48 -h 48 application-icon.svg; 
inkscape -z -e temp/icon64.png -w 64 -h 64 application-icon.svg; 
inkscape -z -e temp/icon128.png -w 128 -h 128 application-icon.svg; 
inkscape -z -e temp/icon256.png -w 256 -h 256 application-icon.svg; 
convert temp/icon16.png png8:temp/icon16_8.png
convert temp/icon32.png png8:temp/icon32_8.png
convert temp/icon48.png png8:temp/icon48_8.png
convert temp/icon16.png \
	temp/icon16_8.png \
	temp/icon24.png \
	temp/icon32.png \
	temp/icon32_8.png \
	temp/icon48.png \
	temp/icon48_8.png \
	temp/icon64.png \
	temp/icon128.png \
	temp/icon256.png \
	application-icon.ico; 
rm -rf temp;
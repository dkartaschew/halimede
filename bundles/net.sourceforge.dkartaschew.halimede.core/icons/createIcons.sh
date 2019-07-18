#!/bin/sh

# Convert SVG to PNG
inkscape -z -e application-icon.png -w 64 -h 64 application-icon.svg; 
inkscape -z -e application-icon@1.5x.png -w 96 -h 96 application-icon.svg; 
inkscape -z -e application-icon@2x.png -w 128 -h 128 application-icon.svg; 
inkscape -z -e application-icon@3x.png -w 192 -h 192 application-icon.svg; 

inkscape -z -e menu-icon.png -w 16 -h 16 application-icon.svg; 
inkscape -z -e menu-icon@1.5x.png -w 24 -h 24 application-icon.svg; 
inkscape -z -e menu-icon@2x.png -w 32 -h 32 application-icon.svg; 
inkscape -z -e menu-icon@3x.png -w 48 -h 48 application-icon.svg; 

inkscape -z -e Icon1024.png -w 1024 -h 1024 application-icon.svg; 

# SVG to XPM
inkscape -z -e application-icon-256.png -w 256 -h 256 application-icon.svg;
convert application-icon-256.png application-icon.xpm
rm application-icon-256.png
cp application-icon.xpm ../../../releng/net.sourceforge.dkartaschew.halimede.product/icons
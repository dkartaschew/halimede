#!/bin/sh

# Convert SVG to PNG
inkscape -z -e lockall.png -w 16 -h 16 deadlock_ovr.svg; 
inkscape -z -e lockall@1.5x.png -w 24 -h 24 deadlock_ovr.svg; 
inkscape -z -e lockall@2x.png -w 32 -h 32 deadlock_ovr.svg; 
inkscape -z -e lockall@3x.png -w 48 -h 48 deadlock_ovr.svg; 

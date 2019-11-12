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

inkscape -z -e dialog-question.png -w 16 -h 16 dialog-question-symbolic.svg; 
inkscape -z -e dialog-question@1.5x.png -w 24 -h 24 dialog-question-symbolic.svg; 
inkscape -z -e dialog-question@2x.png -w 32 -h 32 dialog-question-symbolic.svg; 
inkscape -z -e dialog-question@3x.png -w 48 -h 48 dialog-question-symbolic.svg; 

inkscape -z -e dialog-question-dark.png -w 16 -h 16 dialog-question-symbolic-dark.svg; 
inkscape -z -e dialog-question-dark@1.5x.png -w 24 -h 24 dialog-question-symbolic-dark.svg; 
inkscape -z -e dialog-question-dark@2x.png -w 32 -h 32 dialog-question-symbolic-dark.svg; 
inkscape -z -e dialog-question-dark@3x.png -w 48 -h 48 dialog-question-symbolic-dark.svg; 

inkscape -z -e list-add.png -w 16 -h 16 list-add-symbolic.svg; 
inkscape -z -e list-add@1.5x.png -w 24 -h 24 list-add-symbolic.svg; 
inkscape -z -e list-add@2x.png -w 32 -h 32 list-add-symbolic.svg; 
inkscape -z -e list-add@3x.png -w 48 -h 48 list-add-symbolic.svg; 

inkscape -z -e list-add-dark.png -w 16 -h 16 list-add-symbolic-dark.svg; 
inkscape -z -e list-add-dark@1.5x.png -w 24 -h 24 list-add-symbolic-dark.svg; 
inkscape -z -e list-add-dark@2x.png -w 32 -h 32 list-add-symbolic-dark.svg; 
inkscape -z -e list-add-dark@3x.png -w 48 -h 48 list-add-symbolic-dark.svg;

inkscape -z -e list-remove.png -w 16 -h 16 list-remove-symbolic.svg; 
inkscape -z -e list-remove@1.5x.png -w 24 -h 24 list-remove-symbolic.svg; 
inkscape -z -e list-remove@2x.png -w 32 -h 32 list-remove-symbolic.svg; 
inkscape -z -e list-remove@3x.png -w 48 -h 48 list-remove-symbolic.svg; 

inkscape -z -e list-remove-dark.png -w 16 -h 16 list-remove-symbolic-dark.svg; 
inkscape -z -e list-remove-dark@1.5x.png -w 24 -h 24 list-remove-symbolic-dark.svg; 
inkscape -z -e list-remove-dark@2x.png -w 32 -h 32 list-remove-symbolic-dark.svg; 
inkscape -z -e list-remove-dark@3x.png -w 48 -h 48 list-remove-symbolic-dark.svg;

inkscape -z -e list-edit.png -w 16 -h 16 document-edit-symbolic.svg; 
inkscape -z -e list-edit@1.5x.png -w 24 -h 24 document-edit-symbolic.svg; 
inkscape -z -e list-edit@2x.png -w 32 -h 32 document-edit-symbolic.svg; 
inkscape -z -e list-edit@3x.png -w 48 -h 48 document-edit-symbolic.svg; 

inkscape -z -e list-edit-dark.png -w 16 -h 16 document-edit-symbolic-dark.svg; 
inkscape -z -e list-edit-dark@1.5x.png -w 24 -h 24 document-edit-symbolic-dark.svg; 
inkscape -z -e list-edit-dark@2x.png -w 32 -h 32 document-edit-symbolic-dark.svg; 
inkscape -z -e list-edit-dark@3x.png -w 48 -h 48 document-edit-symbolic-dark.svg;

# SVG to XPM
inkscape -z -e application-icon-256.png -w 256 -h 256 application-icon.svg;
convert application-icon-256.png application-icon.xpm
rm application-icon-256.png
cp application-icon.xpm ../../../releng/net.sourceforge.dkartaschew.halimede.product/icons
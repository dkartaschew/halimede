#!/bin/sh

# Convert SVG to PNG
inkscape -z -e lockall.png -w 16 -h 16 deadlock_ovr.svg; 
inkscape -z -e lockall@1.5x.png -w 24 -h 24 deadlock_ovr.svg; 
inkscape -z -e lockall@2x.png -w 32 -h 32 deadlock_ovr.svg; 
inkscape -z -e lockall@3x.png -w 48 -h 48 deadlock_ovr.svg; 

inkscape -z -e certificate_self.png -w 16 -h 16 certificate_self.svg; 
inkscape -z -e certificate_self@1.5x.png -w 24 -h 24 certificate_self.svg; 
inkscape -z -e certificate_self@2x.png -w 32 -h 32 certificate_self.svg; 
inkscape -z -e certificate_self@3x.png -w 48 -h 48 certificate_self.svg; 

inkscape -z -e certificate_view.png -w 16 -h 16 certificate_view.svg; 
inkscape -z -e certificate_view@1.5x.png -w 24 -h 24 certificate_view.svg; 
inkscape -z -e certificate_view@2x.png -w 32 -h 32 certificate_view.svg; 
inkscape -z -e certificate_view@3x.png -w 48 -h 48 certificate_view.svg; 

inkscape -z -e pkcs12_view.png -w 16 -h 16 pkcs12_view.svg; 
inkscape -z -e pkcs12_view@1.5x.png -w 24 -h 24 pkcs12_view.svg; 
inkscape -z -e pkcs12_view@2x.png -w 32 -h 32 pkcs12_view.svg; 
inkscape -z -e pkcs12_view@3x.png -w 48 -h 48 pkcs12_view.svg; 

inkscape -z -e certificate_req.png -w 16 -h 16 certificate_req.svg; 
inkscape -z -e certificate_req@1.5x.png -w 24 -h 24 certificate_req.svg; 
inkscape -z -e certificate_req@2x.png -w 32 -h 32 certificate_req.svg; 
inkscape -z -e certificate_req@3x.png -w 48 -h 48 certificate_req.svg; 

inkscape -z -e crl_view.png -w 16 -h 16 crl_view.svg; 
inkscape -z -e crl_view@1.5x.png -w 24 -h 24 crl_view.svg; 
inkscape -z -e crl_view@2x.png -w 32 -h 32 crl_view.svg; 
inkscape -z -e crl_view@3x.png -w 48 -h 48 crl_view.svg; 

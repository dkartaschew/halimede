# 
# Halimede Certificate Manager Plugin for Eclipse 
# Copyright (C) 2017-2019 Darran Kartaschew 
#
# All rights reserved. This program and the accompanying materials are
# made available under the terms of the Eclipse Public License v2.0 which
# accompanies this distribution and is available at
#
# http://www.eclipse.org/legal/epl-v20.html
# 
# This Source Code may also be made available under the following Secondary
# Licenses when the conditions for such availability set forth in the Eclipse
# Public License, v. 2.0 are satisfied: GNU General Public License, version 2
# with the GNU Classpath Exception which is
# available at https://www.gnu.org/software/classpath/license.html.

$halimede = "Halimede CA-win32.win32.x86_64.zip" 
$zulu = "zulu11.33.15-ca-jre11.0.4-win_x64"
$PASSWORD = $args[0]
# Build is the number of days from 1/Jan/2000, following Visual Studios  
# automatic assignment for build/revision.
$BUILD = (New-TimeSpan -Start (Get-Date -Year 2000 -Month 1 -Day 1)).Days

$url = "https://cdn.azul.com/zulu/bin/$zulu.zip"
$output = "$zulu.zip"
$start_time = Get-Date
$buildFolder = "halimede"

$tsa = "http://sha256timestamp.ws.symantec.com/sha256/timestamp"

# Download Azul Zulu

if (!(Test-Path $output)) {
	Write-Output "Downloading Azul Zulu JRE 11 - $output" 
	Invoke-WebRequest -Uri $url -OutFile $output
	Write-Output "Time taken: $((Get-Date).Subtract($start_time).Seconds) second(s)"
}

# Create Build folder and extract all items.
if ((Test-Path $buildFolder)){
	Remove-Item $buildFolder -Recurse -Force -ErrorAction Ignore
}

New-Item -Name $buildFolder -ItemType "directory"
Expand-Archive -Path $halimede -DestinationPath $buildFolder
Expand-Archive -Path $output -DestinationPath $buildFolder
Rename-Item -Path "$buildFolder/$zulu" -NewName jre

# Cleanup old items.
if ((Test-Path "Resource.rc")){
	Remove-Item "Resource.rc"
}
if ((Test-Path "Halimede CA-win32.x86_64.zip")){
	Remove-Item "Halimede CA-win32.x86_64.zip"
}
if ((Test-Path "delcert.exe")){
	Remove-Item "delcert.exe"
}
Expand-Archive -Path delcert.zip -DestinationPath ./

# Remove current signatures. (Note: signtool remove doesn't do this).
./delcert.exe "$buildFolder/eclipsec.exe" | Out-Default
./delcert.exe "$buildFolder/halimede.exe" | Out-Default

# Write out a resource file to include.
@"

1 VERSIONINFO
 FILEVERSION 1,0,0,$BUILD
 PRODUCTVERSION 1,0,0,$BUILD
 FILEOS 0x40004L
 FILETYPE 0x1L
BEGIN
    BLOCK "StringFileInfo"
    BEGIN
        BLOCK "000904b0"
        BEGIN
            VALUE "CompanyName", "D.Kartaschew"
            VALUE "FileDescription", "Halimede Certificate Authority"
            VALUE "FileVersion", "1.0.0.$BUILD"
            VALUE "InternalName", "halimede.exe"
            VALUE "LegalCopyright", "Copyright (C) 2019"
            VALUE "OriginalFilename", "halimede.exe"
            VALUE "ProductName", "Halimede CA"
            VALUE "ProductVersion", "1.0.0.$BUILD"
        END
    END
    BLOCK "VarFileInfo"
    BEGIN
        VALUE "Translation", 0x9, 1200
    END
END

"@ | Set-Content -Path "Resource.rc" 

# Update *.exe resources and signatures.

Start-Process ResourceHacker -NoNewWindow -Wait -ArgumentList "-open","Resource.rc","-save","Resource.res","-action","compile"
Start-Process ResourceHacker -NoNewWindow -Wait -ArgumentList "-open","$buildFolder/eclipsec.exe","-save","$buildFolder/eclipsec.exe.new","-resource","Resource.res","-action","addoverwrite"
Start-Process ResourceHacker -NoNewWindow -Wait -ArgumentList "-open","$buildFolder/halimede.exe","-save","$buildFolder/halimede.exe.new","-resource","Resource.res","-action","addoverwrite"

Remove-Item "Resource.rc"
Remove-Item "Resource.res"

Remove-Item "$buildFolder/eclipsec.exe"
Remove-Item "$buildFolder/halimede.exe"
Rename-Item -Path "$buildFolder/eclipsec.exe.new" -NewName eclipsec.exe
Rename-Item -Path "$buildFolder/halimede.exe.new" -NewName halimede.exe

signtool sign /f "$HOME/.keystore/halimede.p12" /p "$PASSWORD" /fd sha256 /tr http://timestamp.digicert.com /td sha256 "$buildFolder/eclipsec.exe" | Out-Default
signtool sign /f "$HOME/.keystore/halimede.p12" /p "$PASSWORD" /fd sha256 /tr http://timestamp.digicert.com /td sha256 "$buildFolder/halimede.exe" | Out-Default

# Build the MSI installer.
heat dir "$buildFolder" -cg HalimedeCAFiles -dr INSTALLDIR -var var.HalimedeFilesDir -o obj\ProductFiles.wxs -sreg -sfrag -ag -srd -ke | Out-Default
candle Product.wxs -arch x64 -o obj\ -ext WixUIExtension -dVersionNumber="1.0.0.$BUILD" | Out-Default
candle obj\ProductFiles.wxs -arch x64 -o obj\ -ext WixUIExtension -dHalimedeFilesDir="$buildFolder" | Out-Default
light obj\Product.wixobj obj\ProductFiles.wixobj -o "Halimede CA.msi" -cultures:en-us -loc Product_en-us.wxl -ext WixUIExtension -sice:ICE61 | Out-Default

signtool sign /f "$HOME/.keystore/halimede.p12" /p "$PASSWORD"  /fd sha256 /tr http://timestamp.digicert.com /td sha256 "Halimede CA.msi" | Out-Default

# Rebuild the distribution ZIP
Remove-Item $buildFolder/jre -Recurse -Force -ErrorAction Ignore
Compress-Archive -Path $buildFolder -DestinationPath "Halimede CA-win32.x86_64.zip"

# Clean up Wix Temp files
if ((Test-Path obj)){
	Remove-Item obj -Recurse -Force -ErrorAction Ignore
}
if ((Test-Path "Halimede CA.wixpdb")){
	Remove-Item "Halimede CA.wixpdb"
}
# Clean up extracted build folder.
if ((Test-Path $buildFolder)){
	Remove-Item $buildFolder -Recurse -Force -ErrorAction Ignore
}
# Cleanup old items.
if ((Test-Path "Resource.rc")){
	Remove-Item "Resource.rc"
}
if ((Test-Path "delcert.exe")){
	Remove-Item "delcert.exe"
}
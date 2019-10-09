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
$RH = "ResourceHacker"
$AI = "AdvancedInstaller"
$BUILD = Get-Date -Format "yyyyMMdd"

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

# Update *.exe resources and signatures.
signtool remove /s "$buildFolder/eclipsec.exe"
signtool remove /s "$buildFolder/halimede.exe"

if ((Test-Path "Resource.rc")){
	Remove-Item "Resource.rc"
}

# Write out a resource file to include.
@"
#include "winres.h"

LANGUAGE LANG_ENGLISH, SUBLANG_ENGLISH_AUS

VS_VERSION_INFO VERSIONINFO
 FILEVERSION 1,0,0,$BUILD
 PRODUCTVERSION 1,0,0,$BUILD
 FILEFLAGSMASK 0x3fL
#ifdef _DEBUG
 FILEFLAGS 0x1L
#else
 FILEFLAGS 0x0L
#endif
 FILEOS 0x40004L
 FILETYPE 0x1L
 FILESUBTYPE 0x0L
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

$RH -open Resources.rc -save Resources.res -action compile -log NUL
$RH -open "$buildFolder/eclipsec.exe" -save "$buildFolder/eclipsec.exe.new" -resource Resource.res -action addoverwrite
$RH -open "$buildFolder/halimede.exe" -save "$buildFolder/halimede.exe.new" -resource Resource.res -action addoverwrite

Remove-Item "Resource.rc"
Remove-Item "Resource.res"

Remove-Item "$buildFolder/eclipsec.exe"
Remove-Item "$buildFolder/halimede.exe"
Rename-Item -Path "$buildFolder/eclipsec.exe.new" -NewName eclipsec.exe
Rename-Item -Path "$buildFolder/halimede.exe.new" -NewName halimede.exe

signtool sign /f "$HOME/.keystore/halimede.p12" /p "$PASSWORD" /t $tsa "$buildFolder/eclipsec.exe"
signtool sign /f "$HOME/.keystore/halimede.p12" /p "$PASSWORD" /t $tsa "$buildFolder/halimede.exe"

# Build the MSI installer.
$AI /rebuild halimede.aip
signtool sign /f "$HOME/.keystore/halimede.p12" /p "$PASSWORD" /t $tsa "Halimede CA.msi"

# Rebuild the distribution ZIP
Remove-Item $buildFolder/jre -Recurse -Force -ErrorAction Ignore
Compress-Archive -Path $buildFolder -DestinationPath "Halimede CA-win32.x86_64.zip"


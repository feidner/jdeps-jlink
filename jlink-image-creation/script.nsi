SilentInstall silent
RequestExecutionLevel user
ShowInstDetails hide

# define the name of the installer
Outfile "target/ulc-client.exe"

# define the directory to install to, the desktop in this case as specified
# by the predefined $DESKTOP variable
#InstallDir $DESKTOP

# default section
Section

# define the output path for this file
SetOutPath $TEMP\hfe

# define what to install and place it in the output path
File /r "target\jlink-out\*"

nsExec::Exec '"$TEMP\hfe\bin\launch.bat"'

SectionEnd
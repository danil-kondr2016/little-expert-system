add_executable (LESRun "")

if (CMAKE_VERSION VERSION_GREATER 3.12)
  set_property(TARGET LESRun PROPERTY CXX_STANDARD 20)
endif()

target_sources(LESRun PRIVATE "LESRun.cpp")

find_package(nowide REQUIRED)
find_package(argparse REQUIRED)

target_link_libraries(LESRun PRIVATE nowide::nowide argparse::argparse libles)
# Amend manifest to tell Windows that the application is DPI aware (needed for Windows 8.1 and up)
IF (MSVC)
	IF (CMAKE_MAJOR_VERSION LESS 3)
		MESSAGE(WARNING "CMake version 3.0 or newer is required use build variable TARGET_FILE")
	ELSE()
		ADD_CUSTOM_COMMAND(
			TARGET LESRun
			POST_BUILD
			COMMAND "mt.exe" -manifest \"${CMAKE_CURRENT_SOURCE_DIR}\\utf8.manifest\" -inputresource:\"$<TARGET_FILE:LESRun>\"\;\#1 -outputresource:\"$<TARGET_FILE:LESRun>\"\;\#1
			COMMENT "Adding display aware manifest..." 
		)
	ENDIF()
ENDIF(MSVC)
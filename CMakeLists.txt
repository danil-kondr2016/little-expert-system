# CMakeList.txt: проект CMake для LittleExpertSystem; включите исходный код и определения,
# укажите здесь логику для конкретного проекта.
#
cmake_minimum_required (VERSION 3.8)
set(CMAKE_PREFIX_PATH ${CMAKE_PREFIX_PATH};${CMAKE_SOURCE_DIR}/icu4c)

# Включение горячей перезагрузки для компиляторов MSVC, если поддерживается.
if (POLICY CMP0141)
  cmake_policy(SET CMP0141 NEW)
  set(CMAKE_MSVC_DEBUG_INFORMATION_FORMAT "$<IF:$<AND:$<C_COMPILER_ID:MSVC>,$<CXX_COMPILER_ID:MSVC>>,$<$<CONFIG:Debug,RelWithDebInfo>:EditAndContinue>,$<$<CONFIG:Debug,RelWithDebInfo>:ProgramDatabase>>")
endif()

project ("LittleExpertSystem")

# Добавьте источник в исполняемый файл этого проекта.
add_executable (LittleExpertSystem "LittleExpertSystem.cpp" "LittleExpertSystem.h" "Parser.h")

if (CMAKE_VERSION VERSION_GREATER 3.12)
  set_property(TARGET LittleExpertSystem PROPERTY CXX_STANDARD 20)
endif()

find_package(ICU REQUIRED COMPONENTS uc)
target_link_libraries(LittleExpertSystem ICU::uc)

# Amend manifest to tell Windows that the application is DPI aware (needed for Windows 8.1 and up)
IF (MSVC)
	IF (CMAKE_MAJOR_VERSION LESS 3)
		MESSAGE(WARNING "CMake version 3.0 or newer is required use build variable TARGET_FILE")
	ELSE()
		ADD_CUSTOM_COMMAND(
			TARGET LittleExpertSystem
			POST_BUILD
			COMMAND "mt.exe" -manifest \"${CMAKE_CURRENT_SOURCE_DIR}\\utf8.manifest\" -inputresource:\"$<TARGET_FILE:LittleExpertSystem>\"\;\#1 -outputresource:\"$<TARGET_FILE:LittleExpertSystem>\"\;\#1
			COMMENT "Adding display aware manifest..." 
		)
	ENDIF()
ENDIF(MSVC)
add_library(libles STATIC "")

if (CMAKE_VERSION VERSION_GREATER 3.12)
  set_property(TARGET libles PROPERTY CXX_STANDARD 20)
endif()

find_package(nowide REQUIRED)

target_sources(libles 
	PRIVATE
		"src/LittleExpertSystem.cpp"
		"src/MKBParser.cpp"
		"src/LittleExpertSystem.h"
		"src/Parser.h"
		"src/CApi.cpp"
	PUBLIC
		${CMAKE_CURRENT_SOURCE_DIR}/include/libles/expert.h
)

target_include_directories(libles PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/include)
target_link_libraries(libles PRIVATE nowide::nowide)
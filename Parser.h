#pragma once

#include "LittleExpertSystem.h"

#include <unicode/unistr.h>

class KnowledgeBaseParser
{
public:
	virtual KnowledgeBase parse() = 0;
};
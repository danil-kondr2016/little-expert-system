#pragma once

#include "LittleExpertSystem.h"

#include <iostream>
#include <stdexcept>

namespace les {

class KnowledgeBaseParser
{
protected:
	std::istream &m_input;
public:
	KnowledgeBaseParser(std::istream &input) : m_input(input) {}
	virtual KnowledgeBase parse() = 0;
};

class MKBSyntaxError : public std::runtime_error
{
public:
	MKBSyntaxError(std::string what) : std::runtime_error(what) {}
};

class MKBInvalidReferenceToQuestion : public std::runtime_error
{
public:
	MKBInvalidReferenceToQuestion(std::string what) : std::runtime_error(what)
	{
	}
};


class MKBParser : public KnowledgeBaseParser
{
private:
	KnowledgeBase m_result;
	int m_lineIndex;

	bool nextLine(std::string &line);

	void parseComment();
	void parseQuestions();
	void parseSingleHypothesis(std::string &line);
	void parseHypotheses();
public:
	MKBParser(std::istream &input) : KnowledgeBaseParser(input), m_lineIndex(0) {}
	KnowledgeBase parse() override;
};

};

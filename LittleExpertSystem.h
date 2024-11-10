﻿// LittleExpertSystem.h : включаемый файл для стандартных системных включаемых файлов
// или включаемые файлы для конкретного проекта.

#pragma once

#include <iostream>
#include <vector>
#include <string>
#include <map>

#include <unicode/unistr.h>

struct Question
{
	icu::UnicodeString description;
	double value;
	bool used : 1;
};

struct Evidence
{
	double pYes;
	double pNo;
};

const Evidence DEFAULT_EVIDENCE = { 0.5, 0.5 };

struct Hypothesis
{
	icu::UnicodeString name;
	double pPrior;
	double pPriorOriginal;
	std::map<int, Evidence> evidences;
};

struct KnowledgeBase
{
	icu::UnicodeString comment;
	std::vector<Question> questions;
	std::vector<Hypothesis> hypotheses;

	void reset();
};

class LittleExpertSystem
{
private:
	KnowledgeBase m_kb;
	bool m_running = false;
	int m_currentQuestion = 0;

	double m_yesLevel, m_noLevel, m_dunnoLevel;
	int selectQuestion();
	bool nextQuestion();
	void calculateValues();
	void recalculate(double normLevel);
public:
	LittleExpertSystem() : m_noLevel(-5), m_yesLevel(5), m_dunnoLevel(0), 
		m_currentQuestion(0), m_running(false) {}
	LittleExpertSystem(double noLevel, double yesLevel) 
		: m_noLevel(noLevel),
		  m_yesLevel(yesLevel),
		  m_dunnoLevel((yesLevel+noLevel)/2.0),
		  m_currentQuestion(0), m_running(false)
	{}

	void loadKnowledgeBase(KnowledgeBase &kb);
	double getYesLevel() const;
	double getNoLevel() const;
	void setLevels(double noLevel, double yesLevel);

	void run();
	bool isRunning() const;
	void answer(double level);
	void stop();
	void reset();

	int getHypothesesCount() const;
	int getQuestionsCount() const;
	icu::UnicodeString getComment() const;
	icu::UnicodeString getQuestion(int index) const;
	icu::UnicodeString getHypothesis(int index) const;
	double getHypothesisValue(int index) const;
};
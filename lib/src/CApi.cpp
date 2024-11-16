#include "../include/libles/expert.h"

#include "LittleExpertSystem.h"
#include "Parser.h"

#include <nowide/fstream.hpp>
#include <nowide/iostream.hpp>
#include <nowide/convert.hpp>

#include <string.h>

using CPP_Les = les::LittleExpertSystem;
using les::KnowledgeBase;
using les::MKBParser;

LittleExpertSystem *les_expert_create(void)
{
	CPP_Les *Expert;
	Expert = new CPP_Les;

	return reinterpret_cast<LittleExpertSystem *>(Expert);
}

void les_expert_destroy(LittleExpertSystem *expert)
{
	if (!expert)
		return;

	CPP_Les *E = reinterpret_cast<CPP_Les *>(expert);
	delete E;
}

int les_LoadKnowledgeBase(LittleExpertSystem *expert, const char* path, char** errorString)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);

	try {
		nowide::ifstream input;
		input.open(path, std::ios_base::binary);
		if (!input) {
			throw std::system_error(errno, std::system_category());
		}

		MKBParser parser(input);
		KnowledgeBase kb = parser.parse();

		self->loadKnowledgeBase(kb);
		if (errorString)
			*errorString = NULL;
		return 1;
	}
	catch (const std::exception &e) {
		if (errorString)
			*errorString = strdup(e.what());
		return 0;
	}
}

int les_LoadKnowledgeBaseW(LittleExpertSystem *expert, const wchar_t* path, char** errorString)
{
	return les_LoadKnowledgeBase(expert, nowide::narrow(path).c_str(), errorString);
}

double les_GetYesLevel(LittleExpertSystem *expert)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return self->getYesLevel();
}

double les_GetNoLevel(LittleExpertSystem *expert)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return self->getNoLevel();
}

int les_SetLevels(LittleExpertSystem *expert, double noLevel, double yesLevel)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	try {
		self->setLevels(noLevel, yesLevel);
		return 1;
	}
	catch (std::invalid_argument &e) {
		return 0;
	}
}

void les_Run(LittleExpertSystem *expert)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	self->run();
}

int les_IsRunning(LittleExpertSystem *expert)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return self->isRunning() ? 1 : 0;
}

int les_Answer(LittleExpertSystem *expert, double level)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);

	try {
		self->answer(level);
		return 1;
	}
	catch (std::invalid_argument &e) {
		return 0;
	}
}

void les_Stop(LittleExpertSystem *expert)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	self->stop();
}

void les_Reset(LittleExpertSystem *expert)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	self->reset();
}

int les_GetCurrentQuestionIndex(LittleExpertSystem *expert)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return self->getCurrentQuestionIndex();
}

int les_GetHypothesesCount(LittleExpertSystem *expert)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return self->getHypothesesCount();
}

int les_GetQuestionsCount(LittleExpertSystem *expert)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return self->getQuestionsCount();
}

const char* les_GetComment(LittleExpertSystem *expert)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return strdup(self->getComment().c_str());
}

const char* les_GetQuestion(LittleExpertSystem *expert, int index)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return strdup(self->getQuestion(index).c_str());
}

double les_GetQuestionValue(LittleExpertSystem *expert, int index)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return self->getQuestionValue(index);
}

const char* les_GetHypothesis(LittleExpertSystem *expert, int index)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return strdup(self->getHypothesis(index).c_str());
}

double les_GetHypothesisValue(LittleExpertSystem *expert, int index)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return self->getHypothesisValue(index);
}

void les_SetQuestionTurnedOff(LittleExpertSystem *expert, int index, int turnedOff)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	self->setQuestionTurnedOff(index, turnedOff != 0);
}

int les_GetConsultationStepCount(LittleExpertSystem *expert)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return self->getConsultationStepCount();
}

int les_GetConsultationStepQuestion(LittleExpertSystem *expert, int index)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return self->getConsultationStepQuestion(index);
}

double les_GetConsultationStepValue(LittleExpertSystem *expert, int index)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	return self->getConsultationStepValue(index);
}

void les_UndoConsultationStep(LittleExpertSystem *expert, int index)
{
	CPP_Les* self = reinterpret_cast<CPP_Les*>(expert);
	self->undoConsultationStep(index);
}

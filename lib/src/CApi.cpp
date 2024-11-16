#include "../include/libles/expert.h"

#include "LittleExpertSystem.h"
#include "Parser.h"

#include <nowide/fstream.hpp>
#include <nowide/iostream.hpp>

les_expert_t *les_expert_create(void)
{
	LittleExpertSystem *Expert;
	Expert = new LittleExpertSystem;

	return reinterpret_cast<les_expert_t *>(Expert);
}

void les_expert_destroy(les_expert_t *expert)
{
	if (!expert)
		return;

	LittleExpertSystem *E = reinterpret_cast<LittleExpertSystem *>(expert);
	delete E;
}

int les_LoadKnowledgeBase(les_expert_t *expert, const char* path, char** errorString)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);

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

int les_LoadKnowledgeBaseW(les_expert_t *expert, const wchar_t* path, char** errorString)
{
	return les_LoadKnowledgeBase(expert, nowide::narrow(path).c_str(), errorString);
}

double les_GetYesLevel(les_expert_t *expert)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	return self->getYesLevel();
}

double les_GetNoLevel(les_expert_t *expert)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	return self->getNoLevel();
}

void les_SetLevels(les_expert_t *expert, double noLevel, double yesLevel)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	self->setLevels(noLevel, yesLevel);
}

void les_Run(les_expert_t *expert)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	self->run();
}

int les_IsRunning(les_expert_t *expert)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	return self->isRunning() ? 1 : 0;
}

void les_Answer(les_expert_t *expert, double level)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	self->answer(level);
}

void les_Stop(les_expert_t *expert)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	self->stop();
}

void les_Reset(les_expert_t *expert)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	self->reset();
}

int les_GetCurrentQuestionIndex(les_expert_t *expert)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	return self->getCurrentQuestionIndex();
}

int les_GetHypothesesCount(les_expert_t *expert)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	return self->getHypothesesCount();
}

int les_GetQuestionsCount(les_expert_t *expert)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	return self->getQuestionsCount();
}

const char* les_GetComment(les_expert_t *expert)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	return strdup(self->getComment().c_str());
}

const char* les_GetQuestion(les_expert_t *expert, int index)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	return strdup(self->getQuestion(index).c_str());
}

const char* les_GetHypothesis(les_expert_t *expert, int index)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	return strdup(self->getHypothesis(index).c_str());
}

double les_GetHypothesisValue(les_expert_t *expert, int index)
{
	LittleExpertSystem* self = reinterpret_cast<LittleExpertSystem*>(expert);
	return self->getHypothesisValue(index);
}
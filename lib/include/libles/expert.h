#pragma once
#ifndef _LIBLES_EXPERT_H_
#define _LIBLES_EXPERT_H_

struct les_expert;
typedef struct les_expert LittleExpertSystem;

#ifdef __cplusplus
extern "C" {
#endif

LittleExpertSystem *les_expert_create(void);
void les_expert_destroy(LittleExpertSystem *expert);

int les_LoadKnowledgeBase(LittleExpertSystem *expert, const char *path, char **errorString);
int les_LoadKnowledgeBaseW(LittleExpertSystem *expert, const wchar_t *path, char** errorString);

double les_GetYesLevel(LittleExpertSystem *expert);
double les_GetNoLevel(LittleExpertSystem *expert);
int les_SetLevels(LittleExpertSystem *expert, double noLevel, double yesLevel);

void les_Run(LittleExpertSystem *expert);
int les_IsRunning(LittleExpertSystem *expert);
int les_Answer(LittleExpertSystem *expert, double level);
void les_Stop(LittleExpertSystem *expert);
void les_Reset(LittleExpertSystem *expert);

int les_GetCurrentQuestionIndex(LittleExpertSystem *expert);
int les_GetHypothesesCount(LittleExpertSystem *expert);
int les_GetQuestionsCount(LittleExpertSystem *expert);

const char *les_GetComment(LittleExpertSystem *expert);

const char *les_GetQuestion(LittleExpertSystem *expert, int index);
double les_GetQuestionValue(LittleExpertSystem *expert, int index);

const char *les_GetHypothesis(LittleExpertSystem *expert, int index);
double les_GetHypothesisValue(LittleExpertSystem *expert, int index);

void les_SetQuestionTurnedOff(LittleExpertSystem *expert, int index, int turnedOff);

#ifdef __cplusplus
}
#endif

#endif

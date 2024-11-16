#pragma once
#ifndef _LIBLES_EXPERT_H_
#define _LIBLES_EXPERT_H_

struct les_expert;
typedef struct les_expert les_expert_t;

#ifdef __cplusplus
extern "C" {
#endif

les_expert_t *les_expert_create(void);
void les_expert_destroy(les_expert_t *expert);

int les_LoadKnowledgeBase(les_expert_t *expert, const char *path, char **errorString);
int les_LoadKnowledgeBaseW(les_expert_t *expert, const wchar_t *path, char** errorString);

double les_GetYesLevel(les_expert_t *expert);
double les_GetNoLevel(les_expert_t *expert);
int les_SetLevels(les_expert_t *expert, double noLevel, double yesLevel);

void les_Run(les_expert_t *expert);
int les_IsRunning(les_expert_t *expert);
int les_Answer(les_expert_t *expert, double level);
void les_Stop(les_expert_t *expert);
void les_Reset(les_expert_t *expert);

int les_GetCurrentQuestionIndex(les_expert_t *expert);
int les_GetHypothesesCount(les_expert_t *expert);
int les_GetQuestionsCount(les_expert_t *expert);

const char *les_GetComment(les_expert_t *expert);

const char *les_GetQuestion(les_expert_t *expert, int index);
double les_GetQuestionValue(les_expert_t *expert, int index);

const char *les_GetHypothesis(les_expert_t *expert, int index);
double les_GetHypothesisValue(les_expert_t *expert, int index);

void les_SetQuestionTurnedOff(les_expert_t *expert, int index, int turnedOff);

#ifdef __cplusplus
}
#endif

#endif

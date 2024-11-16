#pragma once

#include <list>

namespace les {

struct ConsultationStep {
	int currentQuestion;
	double value;
};

typedef std::vector<ConsultationStep> ConsultationSteps;

};

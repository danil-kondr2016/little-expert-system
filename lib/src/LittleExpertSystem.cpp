#include "LittleExpertSystem.h"
#include <cmath>

using std::fabs;
using std::nan;

using namespace les;

void KnowledgeBase::reset()
{
	for (size_t i = 0; i < this->questions.size(); i++) {
		questions[i].used = false;
	}

	for (size_t i = 0; i < this->hypotheses.size(); i++) {
		hypotheses[i].pPrior = hypotheses[i].pPriorOriginal;
	}
}

void LittleExpertSystem::loadKnowledgeBase(KnowledgeBase &kb)
{
	this->m_kb = kb;
	this->m_kb.reset();
	calculateValues();
}

double LittleExpertSystem::getYesLevel() const
{
	return this->m_yesLevel;
}

double LittleExpertSystem::getNoLevel() const
{
	return this->m_noLevel;
}

void LittleExpertSystem::setLevels(double noLevel, double yesLevel)
{
	if (noLevel >= yesLevel)
		throw std::invalid_argument("\"No\" level should be less than \"yes\" level");
	this->m_yesLevel = yesLevel;
	this->m_noLevel = noLevel;
	this->m_dunnoLevel = (yesLevel + noLevel) / 2.0;
}

void LittleExpertSystem::calculateValues()
{
	for (size_t i = 0; i < m_kb.questions.size(); i++)
		m_kb.questions[i].value = 0;

	for (size_t i = 0; i < m_kb.hypotheses.size(); i++) {
		for (auto const& j : m_kb.hypotheses[i].evidences) {
			int questionId = j.first;
			if (m_kb.questions[questionId].used)
				continue;
			if (m_kb.questions[questionId].turnedOff)
				continue;

			double Py = j.second.pYes;
			double Pn = j.second.pNo;
			double P = m_kb.hypotheses[i].pPrior;

			// Такой способ вычисления значения вопроса использовал
			// Алексей Бухнин в своей "Малой ЭС 2.0".
			double PHy = Py * P / (Py * P + Pn * (1 - P));
			double PHn = Pn * P / (Pn * P + (1 - Pn) * (1 - P));

			m_kb.questions[questionId].value += fabs(PHy - PHn);
		}
	}
}

void LittleExpertSystem::recalculate(double normLevel)
{
	if (normLevel == 0)
		return;

	for (size_t i = 0; i < m_kb.hypotheses.size(); i++) {
		Hypothesis &H = m_kb.hypotheses[i];
		if (H.evidences.find(m_currentQuestion) == H.evidences.end())
			continue;

		const Evidence &E = H.evidences[m_currentQuestion];

		double P = H.pPrior;
		double Py = E.pYes;
		double Pn = E.pNo;

		double P_E = Py * P / (Py * P + Pn * (1 - P));
		double P_not_E = (1 - Py) * P / ((1 - Py) * P + (1 - Pn) * (1 - P));

		if (normLevel > 0)
			H.pPrior = P + (P_E - P) * normLevel;
		else
			H.pPrior = P + (P_not_E - P) * (-normLevel);
	}

	calculateValues();
}

int LittleExpertSystem::selectQuestion()
{
	double maxValue = 0;
	int maxQuestion = -1;
	for (size_t i = 0; i < m_kb.questions.size(); i++) {
		if (m_kb.questions[i].value > maxValue) {
			maxValue = m_kb.questions[i].value;
			maxQuestion = (int)i;
		}
	}

	if (maxQuestion == -1 || m_kb.questions[maxQuestion].value == 0)
		return -1;

	return maxQuestion;
}

bool LittleExpertSystem::nextQuestion()
{
	m_currentQuestion = selectQuestion();
	if (m_currentQuestion == -1)
		return false;
	m_kb.questions[m_currentQuestion].used = true;

	return true;
}

void LittleExpertSystem::run()
{
	m_running = nextQuestion();
}

bool LittleExpertSystem::isRunning() const
{
	return m_running;
}

void LittleExpertSystem::answer(double level)
{
	if (!m_running)
		return;

	if (level < m_noLevel || level > m_yesLevel)
		throw std::invalid_argument("Level is out of range");

	ConsultationStep &step = m_steps.emplace_back();
	step.currentQuestion = m_currentQuestion;
	step.value = level;

	double normLevel = ((level - m_dunnoLevel) / (m_yesLevel - m_noLevel)) * 2.0;
	recalculate(normLevel);

	m_running = nextQuestion();
}

void LittleExpertSystem::stop()
{
	m_running = false;
}

void LittleExpertSystem::reset()
{
	m_currentQuestion = 0;
	m_running = false;

	m_steps.clear();
	m_kb.reset();
	calculateValues();
}

int LittleExpertSystem::getHypothesesCount() const
{
	return (int)m_kb.hypotheses.size();
}

int LittleExpertSystem::getQuestionsCount() const
{
	return (int)m_kb.questions.size();
}

std::string LittleExpertSystem::getComment() const
{
	return m_kb.comment;
}

std::string LittleExpertSystem::getQuestion(int index) const
{
	if (index < 0 || index >= m_kb.questions.size())
		return "";
	return m_kb.questions[index].description;
}

double LittleExpertSystem::getQuestionValue(int index) const
{
	if (index < 0 || index >= m_kb.questions.size())
		return nan("IVAL");
	return m_kb.questions[index].value;
}

std::string LittleExpertSystem::getHypothesis(int index) const
{
	if (index < 0 || index >= m_kb.hypotheses.size())
		return "";
	return m_kb.hypotheses[index].name;
}

double LittleExpertSystem::getHypothesisValue(int index) const
{
	if (index < 0 || index >= m_kb.hypotheses.size())
		return nan("IVAL");
	return m_kb.hypotheses[index].pPrior;
}

int LittleExpertSystem::getCurrentQuestionIndex() const
{
	if (!m_running || m_currentQuestion < 0)
		return -1;
	return m_currentQuestion;
}

void LittleExpertSystem::setQuestionTurnedOff(int index, bool turnedOff)
{
	if (index < 0 || index >= m_kb.questions.size())
		return;

	m_kb.questions[index].turnedOff = turnedOff;
}

int LittleExpertSystem::getConsultationStepCount() const
{
	return (int)m_steps.size();
}

int LittleExpertSystem::getConsultationStepQuestion(int index) const
{
	if (index < 0 || index >= m_steps.size())
		return -1;
	return m_steps[index].currentQuestion;
}

double LittleExpertSystem::getConsultationStepValue(int index) const
{
	if (index < 0 || index >= m_steps.size())
		return nan("IVAL");
	return m_steps[index].value;
}

void LittleExpertSystem::undoConsultationStep(int index) const
{
	if (index < 0 || index >= m_steps.size())
		return;

	// Remove consultation step from list
	m_steps.erase(m_steps.begin()+index);

	// Replay consultation steps
	m_kb.reset();
	calculateValues();
	m_running = true;
	for (ConsultationStep step: m_steps) {
		m_currentQuestion = step.currentQuestion;

		double normLevel = ((level - m_dunnoLevel) / (m_yesLevel - m_noLevel)) * 2.0;
		recalculate(normLevel);
	}
	m_running = nextQuestion();
}

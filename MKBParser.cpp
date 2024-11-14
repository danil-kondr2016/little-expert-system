#include "Parser.h"

#include <string>
#include <memory>

#include <unicode/ucnv.h>
#include <unicode/errorcode.h>
#include <unicode/normalizer2.h>
#include <unicode/numberformatter.h>
#include <unicode/fmtable.h>
#include <unicode/msgfmt.h>

#include <fmt/format.h>

using std::getline;
using std::string;
using icu::UnicodeString;
using icu::UnicodeStringAppendable;
using icu::Normalizer2;

using std::unique_ptr;

MKBSyntaxError MKBSyntaxErrorAt(int row, int col, std::string reason="")
{
	std::string what;

	what = fmt::format("Syntax error at line {} column {}", row, col);
	if (!reason.empty())
		what += ": " + reason;

	return MKBSyntaxError(what);
}

MKBInvalidReferenceToQuestion MKBInvalidReferenceToQuestionAt(int row, int col, int question)
{
	std::string what;
	what = fmt::format("Invalid reference to question {} at line {} column {}", question, row, col);
	return MKBInvalidReferenceToQuestion(what);
}

bool MKBParser::nextLine(icu::UnicodeString &output)
{
	string currentLine;
	getline(m_input, currentLine);
	if (currentLine.length() == 0)
		return false;

	m_lineIndex++;
	output = UnicodeString(currentLine.c_str(), "CP1251");
	return true;
}

void MKBParser::parseComment()
{
	m_result.comment.truncate(0);

	UnicodeString line;
	while (nextLine(line)) {
		m_result.comment.append(line);
		m_result.comment.append("\r\n");
	}
}

void MKBParser::parseQuestions()
{
	UnicodeString line;

	while (nextLine(line)) {
		Question &Q = m_result.questions.emplace_back();
		Q.description = line;
	}
}

static bool TryUnicodeStringToDouble(UnicodeString str, double &result)
{
	icu::ErrorCode error;
	const Normalizer2* normalizer = Normalizer2::getNFKCInstance(error);

	UnicodeString normStr = normalizer->normalize(str, error);
	string normU8Str;
	normStr.toUTF8String(normU8Str);

	try {
		result = std::stod(normU8Str);
		return true;
	}
	catch (std::exception e) {
		return false;
	}
}

static bool TryUnicodeStringToInt(UnicodeString str, int &result)
{
	icu::ErrorCode error;
	const Normalizer2* normalizer = Normalizer2::getNFKCInstance(error);

	UnicodeString normStr = normalizer->normalize(str, error);
	string normU8Str;
	normStr.toUTF8String(normU8Str);

	try {
		result = std::stoi(normU8Str);
		return true;
	}
	catch (std::exception e) {
		return false;
	}
}

static double UnicodeStringToDouble(UnicodeString str)
{
	icu::ErrorCode error;
	const Normalizer2 *normalizer = Normalizer2::getNFKCInstance(error);

	UnicodeString normStr = normalizer->normalize(str, error);
	string normU8Str;
	normStr.toUTF8String(normU8Str);

	return std::stod(normU8Str);
}

static int UnicodeStringToInt(UnicodeString str)
{
	icu::ErrorCode error;
	const Normalizer2* normalizer = Normalizer2::getNFKCInstance(error);

	UnicodeString normStr = normalizer->normalize(str, error);
	string normU8Str;
	normStr.toUTF8String(normU8Str);

	return std::stoi(normU8Str);
}

static string UnicodeStringToUTF8(UnicodeString str)
{
	string result;
	str.toUTF8String(result);
	return result;
}

void MKBParser::parseSingleHypothesis(UnicodeString &line)
{
	int tokenIndex = 0;
	int32_t pos = 0, next_pos, length;

	length = line.length();

	Hypothesis &H = m_result.hypotheses.emplace_back();
	int QuestionIndex;
	Evidence E;
	while (pos > 0 && pos < length) {
		next_pos = line.indexOf(',', pos);

		int32_t tokenSize;
		if (next_pos == -1)
			next_pos = length + 1;
		tokenSize = next_pos - pos - 1;
		UnicodeString token(line, pos, tokenSize);

		switch (tokenIndex) {
		case 0:
			H.name = token;
			break;
		case 1:
			if (!TryUnicodeStringToDouble(token, H.pPriorOriginal)) {
				throw MKBSyntaxErrorAt(m_lineIndex, pos);
			}
			H.pPrior = H.pPriorOriginal;
			break;
		default: {
			int evidenceTokenIndex = (tokenIndex - 2) % 3;
			switch (evidenceTokenIndex) {
			case 0:
				if (!TryUnicodeStringToInt(token, QuestionIndex))
					throw MKBSyntaxErrorAt(m_lineIndex, pos, string("Invalid value of question index field: ") + UnicodeStringToUTF8(token));
				if (QuestionIndex <= 0 || QuestionIndex >= m_result.questions.size())
					throw MKBInvalidReferenceToQuestionAt(m_lineIndex, pos, QuestionIndex);
				break;
			case 1:
				if (!TryUnicodeStringToDouble(token, E.pYes))
					throw MKBSyntaxErrorAt(m_lineIndex, pos, string("Invalid value of \"yes\" answer field: ")+UnicodeStringToUTF8(token));
				break;
			case 2:
				if (!TryUnicodeStringToDouble(token, E.pNo))
					throw MKBSyntaxErrorAt(m_lineIndex, pos, string("Invalid value of \"no\" answer field: ") + UnicodeStringToUTF8(token));
				H.evidences[QuestionIndex] = E;
				QuestionIndex = 0;
				E.pYes = 0;
				E.pNo = 0;
				break;
			}
		}
		}

		pos = next_pos + 1;

		tokenIndex++;
	}
}

void MKBParser::parseHypotheses()
{
	UnicodeString line;

	while (nextLine(line))
		parseSingleHypothesis(line);
}

KnowledgeBase MKBParser::parse()
{
	m_lineIndex = 0;
	m_result.comment.truncate(0);
	m_result.questions.clear();
	m_result.hypotheses.clear();

	parseComment();
	parseQuestions();
	parseHypotheses();

	return m_result;
}
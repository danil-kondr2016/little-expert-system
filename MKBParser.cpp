#include "Parser.h"

#include <string>
#include <memory>
#include <algorithm>

#include <fmt/format.h>

using std::getline;
using std::string;

using std::unique_ptr;

static string CP1251ToUTF8(string input)
{
	static const char *unicode[] = {
		"\0",  "\1",  "\2",  "\3",  "\4",  "\5",  "\6",  "\7",
		"\10", "\11", "\12", "\13", "\14", "\15", "\16", "\17",
		"\20", "\21", "\22", "\23", "\24", "\25", "\26", "\27",
		"\30", "\31", "\32", "\33", "\34", "\35", "\36", "\37",
		" ",   "!",   "\"",  "#",   "$",   "%",   "&",   "'",
		"(",   ")",   "*",   "+",   ",",   "-",   ".",   "/",
		"0",   "1",   "2",   "3",   "4",   "5",   "6",   "7",
		"8",   "9",   ":",   ";",   "<",   "=",   ">",   "?",
		"@",   "A",   "B",   "C",   "D",   "E",   "F",   "G",
		"H",   "I",   "J",   "K",   "L",   "M",   "N",   "O",
		"P",   "Q",   "R",   "S",   "T",   "U",   "V",   "W",
		"X",   "Y",   "Z",   "[",   "\\",  "]",   "^",   "_",
		"`",   "a",   "b",   "c",   "d",   "e",   "f",   "g",
		"h",   "i",   "j",   "k",   "l",   "m",   "n",   "o",
		"p",   "q",   "r",   "s",   "t",   "u",   "v",   "w",
		"x",   "y",   "z",   "{",   "|",   "}",   "~",   "\x7f",
		"\xd0\x82",     "\xd0\x83",     "\xe2\x80\x9a",  "\xd1\x93",
		"\xe2\x80\x9e", "\xe2\x80\xa6", "\xe2\x80\xa0", "\xe2\x80\xa1",
		"\xe2\x82\xac", "\xe2\x80\xb0", "\xd0\x89",     "\xe2\x80\xb9",
		"\xd0\x8a",     "\xd0\x8c",     "\xd0\x8b",     "\xd0\x8f",
		"\xd1\x92",     "\xe2\x80\x98", "\xe2\x80\x99", "\xe2\x80\x9c",
		"\xe2\x80\x9d", "\xe2\x80\xa2", "\xe2\x80\x93", "\xe2\x80\x94",
		"\xef\xbf\xbd", "\xe2\x84\xa2", "\xd1\x99",     "\xe2\x80\xba",
		"\xd1\x9a",     "\xd1\x9c",     "\xd1\x9b",     "\xd1\x9f",
		"\xc2\xa0",     "\xd0\x8e",     "\xd1\x9e",     "\xd0\x88",
		"\xc2\xa4",     "\xd2\x90",     "\xc2\xa6",     "\xc2\xa7",
		"\xd0\x81",     "\xc2\xa9",     "\xd0\x84",     "\xc2\xab",
		"\xc2\xac",     "\xc2\xad",     "\xc2\xae",     "\xd0\x87",
		"\xc2\xb0",     "\xc2\xb1",     "\xd0\x86",     "\xd1\x96",
		"\xd2\x91",     "\xc2\xb5",     "\xc2\xb6",     "\xc2\xb7",
		"\xd1\x91",     "\xe2\x84\x96", "\xd1\x94",     "\xc2\xbb",
		"\xd1\x98",     "\xd0\x85",     "\xd1\x95",     "\xd1\x97",
		"\xd0\x90", "\xd0\x91", "\xd0\x92", "\xd0\x93", "\xd0\x94",
		"\xd0\x95", "\xd0\x96", "\xd0\x97", "\xd0\x98", "\xd0\x99",
		"\xd0\x9a", "\xd0\x9b", "\xd0\x9c", "\xd0\x9d", "\xd0\x9e",
		"\xd0\x9f", "\xd0\xa0", "\xd0\xa1", "\xd0\xa2", "\xd0\xa3",
		"\xd0\xa4", "\xd0\xa5", "\xd0\xa6", "\xd0\xa7", "\xd0\xa8",
		"\xd0\xa9", "\xd0\xaa", "\xd0\xab", "\xd0\xac", "\xd0\xad",
		"\xd0\xae", "\xd0\xaf", "\xd0\xb0", "\xd0\xb1", "\xd0\xb2",
		"\xd0\xb3", "\xd0\xb4", "\xd0\xb5", "\xd0\xb6", "\xd0\xb7",
		"\xd0\xb8", "\xd0\xb9", "\xd0\xba", "\xd0\xbb", "\xd0\xbc",
		"\xd0\xbd", "\xd0\xbe", "\xd0\xbf", "\xd1\x80", "\xd1\x81",
		"\xd1\x82", "\xd1\x83", "\xd1\x84", "\xd1\x85", "\xd1\x86",
		"\xd1\x87", "\xd1\x88", "\xd1\x89", "\xd1\x8a", "\xd1\x8b",
		"\xd1\x8c", "\xd1\x8d", "\xd1\x8e", "\xd1\x8f",
	};
	string result;

	for (char x : input) {
		unsigned char i = static_cast<unsigned char>(x);
		result += unicode[i];
	}

	return result;
}

// From StackOverflow
// trim from start (in place)
static inline void ltrim(std::string& s)
{
	s.erase(s.begin(), std::find_if(s.begin(), s.end(), [](unsigned char ch)
		{
			return !std::isspace(ch);
		}));
}
// trim from end (in place)
static inline void rtrim(std::string& s)
{
	s.erase(std::find_if(s.rbegin(), s.rend(), [](unsigned char ch)
		{
			return !std::isspace(ch);
		}).base(), s.end());
}
// trim from both ends (in place)
static inline void trim(std::string& s)
{
	rtrim(s);
	ltrim(s);
}
// trim from start (copying)
static inline std::string ltrim_copy(std::string s)
{
	ltrim(s);
	return s;
}
// trim from end (copying)
static inline std::string rtrim_copy(std::string s)
{
	rtrim(s);
	return s;
}
// trim from both ends (copying)
static inline std::string trim_copy(std::string s)
{
	trim(s);
	return s;
}

MKBSyntaxError MKBSyntaxErrorAt(size_t row, size_t col, std::string reason="")
{
	std::string what;

	what = fmt::format("Syntax error at line {} column {}", row, col);
	if (!reason.empty())
		what += ": " + reason;

	return MKBSyntaxError(what);
}

MKBInvalidReferenceToQuestion MKBInvalidReferenceToQuestionAt(size_t row, size_t col, int question)
{
	std::string what;
	what = fmt::format("Invalid reference to question {} at line {} column {}", question, row, col);
	return MKBInvalidReferenceToQuestion(what);
}

bool MKBParser::nextLine(string &output)
{
	string currentLine;
	getline(m_input, currentLine);
	
	if (currentLine.length() == 0)
		return false;
	if (currentLine[currentLine.length() - 1] == '\r')
		currentLine.resize(currentLine.length() - 1);
	if (currentLine.length() == 0)
		return false;

	m_lineIndex++;
	output = CP1251ToUTF8(currentLine);
	return true;
}

void MKBParser::parseComment()
{
	m_result.comment.clear();

	string line;
	while (nextLine(line)) {
		m_result.comment.append(line);
		m_result.comment.append("\r\n");
	}
}

void MKBParser::parseQuestions()
{
	string line;

	while (nextLine(line)) {
		Question &Q = m_result.questions.emplace_back();
		Q.description = line;
	}
}

static bool TryStringToDouble(string str, double &result)
{
	try {
		result = std::stod(str);
		return true;
	}
	catch (std::exception e) {
		return false;
	}
}

static bool TryStringToInt(string str, int &result)
{
	try {
		result = std::stoi(str);
		return true;
	}
	catch (std::exception e) {
		return false;
	}
}

void MKBParser::parseSingleHypothesis(string &line)
{
	int tokenIndex = 0;
	size_t pos = 0, next_pos, length;

	length = line.length();

	Hypothesis &H = m_result.hypotheses.emplace_back();
	int QuestionIndex;
	Evidence E;
	while (pos >= 0 && pos < length) {
		next_pos = line.find(',', pos);

		size_t tokenSize;
		if (next_pos == string::npos)
			next_pos = length + 1;
		tokenSize = next_pos - pos;
		string token = trim_copy(line.substr(pos, tokenSize));

		switch (tokenIndex) {
		case 0:
			H.name = token;
			break;
		case 1:
			if (!TryStringToDouble(token, H.pPriorOriginal)) {
				throw MKBSyntaxErrorAt(m_lineIndex, pos);
			}
			H.pPrior = H.pPriorOriginal;
			break;
		default: {
			int evidenceTokenIndex = (tokenIndex - 2) % 3;
			switch (evidenceTokenIndex) {
			case 0:
				if (!TryStringToInt(token, QuestionIndex))
					throw MKBSyntaxErrorAt(m_lineIndex, pos, string("Invalid value of question index field: ") + token);
				if (QuestionIndex <= 0 || QuestionIndex >= m_result.questions.size())
					throw MKBInvalidReferenceToQuestionAt(m_lineIndex, pos, QuestionIndex);
				break;
			case 1:
				if (!TryStringToDouble(token, E.pYes))
					throw MKBSyntaxErrorAt(m_lineIndex, pos, string("Invalid value of \"yes\" answer field: ") + token);
				break;
			case 2:
				if (!TryStringToDouble(token, E.pNo))
					throw MKBSyntaxErrorAt(m_lineIndex, pos, string("Invalid value of \"no\" answer field: ") + token);
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
	string line;

	while (nextLine(line))
		parseSingleHypothesis(line);
}

KnowledgeBase MKBParser::parse()
{
	m_lineIndex = 0;
	m_result.comment.clear();
	m_result.questions.clear();
	m_result.hypotheses.clear();

	parseComment();
	parseQuestions();
	parseHypotheses();

	return std::move(m_result);
}
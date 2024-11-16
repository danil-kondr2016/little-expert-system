#include "LittleExpertSystem.h"
#include "Parser.h"

#include <nowide/args.hpp>
#include <nowide/fstream.hpp>
#include <nowide/iostream.hpp>
#include <iomanip>

#include <argparse/argparse.hpp>

using std::endl;
using std::setprecision;
using std::setw;
using nowide::cout;
using nowide::cerr;
using nowide::cin;
using std::fixed;
using std::defaultfloat;
using argparse::ArgumentParser;

int main(int argc, char** argv)
{
	nowide::args a(argc, argv);
	ArgumentParser program("LESRun");

	program.add_argument("input_file")
		.help("Knowledge base");

	try {
		program.parse_args(argc, argv);
	}
	catch (const std::exception &e) {
		cerr << e.what() << endl;
		cerr << program;
		std::exit(1);
	}

	std::string input_name = program.get("input_file");
	nowide::ifstream input(input_name);
	MKBParser parser(input);

	KnowledgeBase kb = parser.parse();
	cout << "Comment: " << kb.comment << endl;
	cout << "Questions:" << endl;

	for (Question question : kb.questions)
		cout << question.description << endl;

	cout << endl << "Hypotheses: " << endl;
	for (Hypothesis hypothesis : kb.hypotheses) {
		cout << "H: " << hypothesis.name << ", P=" << hypothesis.pPriorOriginal << endl;
		cout << "Evidences:" << endl;
		for (std::pair<int, Evidence>&& evidence_pair : hypothesis.evidences) {
			cout << "  q=" << evidence_pair.first << ", pY=" << evidence_pair.second.pYes << ", pN=" << evidence_pair.second.pNo << endl;
		}
	}

	LittleExpertSystem les;
	les.loadKnowledgeBase(kb);

	cout << "This knowledge base will be executed." << endl;
	les.run();
	while (les.isRunning()) {
		for (int i = 0; i < les.getHypothesesCount(); i++) {
			cout << les.getHypothesis(i) << ": " << fixed << les.getHypothesisValue(i) << std::defaultfloat << endl;
		}
		cout << les.getQuestion(les.getCurrentQuestionIndex()) << endl;
		cout << "Value [" << les.getNoLevel() << "; " << les.getYesLevel() << "]: ";
		double value;
		cin >> value;
		les.answer(value);
	}
	for (int i = 0; i < les.getHypothesesCount(); i++) {
		cout << les.getHypothesis(i) << ": " << fixed << les.getHypothesisValue(i) << defaultfloat << endl;
	}

	return 0;
}

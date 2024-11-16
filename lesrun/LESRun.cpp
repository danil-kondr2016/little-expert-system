#include <nowide/args.hpp>
#include <nowide/fstream.hpp>
#include <nowide/iostream.hpp>
#include <iomanip>

#include <argparse/argparse.hpp>
#include <libles/expert.h>

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
	std::unique_ptr<les_expert_t, decltype(&les_expert_destroy)> les(les_expert_create(), les_expert_destroy);

	char *errorString;
	if (!les_LoadKnowledgeBase(les.get(), input_name.c_str(), &errorString)) {
		cerr << "Failed to open file " << input_name << ": " << errorString << endl;
		delete errorString;
		std::exit(1);
	}
	delete errorString;

	cout << "This knowledge base will be executed." << endl;
	les_Run(les.get());
	while (les_IsRunning(les.get())) {
		for (int i = 0; i < les_GetHypothesesCount(les.get()); i++) {
			std::unique_ptr<const char> hypothesis(les_GetHypothesis(les.get(), i));
			cout << hypothesis.get() << ": " << fixed << les_GetHypothesisValue(les.get(), i) << std::defaultfloat << endl;
		}
		std::unique_ptr<const char> question(les_GetQuestion(les.get(), les_GetCurrentQuestionIndex(les.get())));
		cout << question.get() << endl;
		cout << "Value [" << les_GetNoLevel(les.get()) << "; " << les_GetYesLevel(les.get()) << "]: ";
		double value;
		cin >> value;
		les_Answer(les.get(), value);
	}
	for (int i = 0; i < les_GetHypothesesCount(les.get()); i++) {
		std::unique_ptr<const char> hypothesis(les_GetHypothesis(les.get(), i));
		cout << hypothesis.get() << ": " << fixed << les_GetHypothesisValue(les.get(), i) << std::defaultfloat << endl;
	}

	return 0;
}

default:
	javac ir/*/*.java
clean:
	rm -f ir/*/*.class
	rm -f KNN_K*.gplot
	rm -f Rocchio*.gplot
	rm -f NaiveBayes*.gplot
turnin: default
	rm -f proj4_et7226_code.zip
	zip proj4_et7226_code.zip ir/classifiers/*KNN* ir/classifiers/*Rocchio* ir/vsr/HashMapVector.*
	unzip -l proj4_et7226_code.zip
bayes:
	java ir.classifiers.TestNaiveBayes
knn:
	java ir.classifiers.TestKNN -k 1
	java ir.classifiers.TestKNN -k 3
	java ir.classifiers.TestKNN -k 5
rocchio:
	java ir.classifiers.TestRocchio
	java ir.classifiers.TestRocchio -neg
index_trace:
	 script proj3_et7226_retrieve_trace.txt
plot:
	gnuplot All.gplot > plot.ps
	gv plot.ps
plot_train:
	gnuplot AllTrain.gplot > plot.ps
	gv plot.ps
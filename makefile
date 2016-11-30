default:
	javac ir/*/*.java
clean:
	rm -f ir/*/*.class
	rm -f KNN_K*.gplot
turnin: default
	rm -f proj3_et7226_code.zip
	zip proj3_et7226_code.zip ir/*/PageRank*
	unzip -l proj3_et7226_code.zip
knn_test:
	java ir.classifiers.TestKNN -k 1
	java ir.classifiers.TestKNN -k 3
	java ir.classifiers.TestKNN -k 5
knn_test_debug:
	java ir.classifiers.TestKNN -k 1 -debug
	java ir.classifiers.TestKNN -k 3 -debug
	java ir.classifiers.TestKNN -k 5 -debug
index_trace:
	 script proj3_et7226_retrieve_trace.txt
plot:
	gnuplot KNN.gplot > plot.ps
	gv plot.ps
plot_train:
	gnuplot KNNTrain.gplot > plot.ps
	gv plot.ps
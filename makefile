default:
	javac ir/*/*.java
clean:
	rm -f ir/*/*.class
	rm -f KNN_K*.gplot
	rm -f Rocchio*.gplot
turnin: default
	rm -f proj3_et7226_code.zip
	zip proj3_et7226_code.zip ir/*/PageRank*
	unzip -l proj3_et7226_code.zip
knn:
	java ir.classifiers.TestKNN -k 1
	java ir.classifiers.TestKNN -k 3
	java ir.classifiers.TestKNN -k 5
rocchio:
	java ir.classifiers.TestRocchio
index_trace:
	 script proj3_et7226_retrieve_trace.txt
plot:
	gnuplot KNN.gplot > plot.ps
	gv plot.ps
plot_train:
	gnuplot KNNTrain.gplot > plot.ps
	gv plot.ps
plot_r:
	gnuplot Rocchio.gplot > plot.ps
	gv plot.ps
	rm plot.ps
plot_train_r:
	gnuplot RocchioTrain.gplot > plot.ps
	gv plot.ps
	rm plot.ps
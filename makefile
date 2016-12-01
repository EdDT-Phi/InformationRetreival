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
	gnuplot All.gplot | ps2pdf - eval.pdf
plot_train:
	gnuplot AllTrain.gplot | ps2pdf - train.pdf
report:plot plot_train
	pdfunite proj4_et7226_report.pdf train.pdf eval.pdf out.pdf
	mv out.pdf proj4_et7226_report.pdf
	rm eval.pdf
	rm train.pdf
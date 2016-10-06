default:
	javac ir/*/*.java
clean:
	rm ir/*/*.class
turnin: default
	rm -f proj2_et7226_code.zip
	zip proj2_et7226_code.zip ir/vsr/InvertedIndex.* ir/vsr/Feedback.* ir/eval/Experiment.class ir/eval/Experiment.java
	unzip -l proj2_et7226_code.zip
test_bigrams:
	java ir.vsr.InvertedIndex -html -v /u/mooney/ir-code/corpora/cs-faculty/
test_fb:
	java ir.vsr.InvertedIndex -html -v -feedback /u/mooney/ir-code/corpora/cf/
test_pf:
	java ir.vsr.InvertedIndex -html -v -pseudofeedback 1 -feedbackparams 1 1 1 /u/mooney/ir-code/corpora/cf/
test_pfb:
	java ir.vsr.InvertedIndex -html -v -feedback -pseudofeedback 5 /u/mooney/ir-code/corpora/cf/
test_exp:
	java ir.eval.Experiment -pseudofeedback 1 -feedbackparams 1 1 1 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph
test_pseudo:
	java ir.eval.Experiment -pseudofeedback 0 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph00
	java ir.eval.Experiment -pseudofeedback 1 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph01
	java ir.eval.Experiment -pseudofeedback 5 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph05
	java ir.eval.Experiment -pseudofeedback 10 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph10
	java ir.eval.Experiment -pseudofeedback 15 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph15
	java ir.eval.Experiment -pseudofeedback 30 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph30
	java ir.eval.Experiment -pseudofeedback 50 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph50
	gnuplot pseudo_plot.gplot > proj2_et7226_amount_results.ps
	rm pseudo_graph*
	gv proj2_et7226_amount_results.ps
test_beta:
	java ir.eval.Experiment -feedbackparams 1 0.1 1 -pseudofeedback 5 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries beta_graph01
	java ir.eval.Experiment -feedbackparams 1 0.5 1 -pseudofeedback 5 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries beta_graph05
	java ir.eval.Experiment -feedbackparams 1 1.0 1 -pseudofeedback 5 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries beta_graph10
	gnuplot beta_plot.gplot > proj2_et7226_beta_results.ps
	rm beta_graph*
	gv proj2_et7226_beta_results.ps
test_params:
	java ir.eval.Experiment -pseudofeedback 5 -feedbackparams 0.1 0.2 0.3 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries cf-rp
default:
	javac ir/*/*.java
clean:
	rm ir/*/*.class
turnin: default
	rm -f proj2_et7226_code.zip
	zip proj2_et7226_code.zip ir/vsr/InvertedIndex.* ir/vsr/Feedback.* ir/eval/Experiment.*
	unzip -l proj2_et7226_code.zip
test_bigrams:
	java ir.vsr.InvertedIndex -html -v /u/mooney/ir-code/corpora/cs-faculty/
test_fb:
	java ir.vsr.InvertedIndex -html -v -feedback /u/mooney/ir-code/corpora/cf/
test_pf:
	java ir.vsr.InvertedIndex -html -v -pseudofeedback 5 /u/mooney/ir-code/corpora/cf/
test_pfb:
	java ir.vsr.InvertedIndex -html -v -feedback -pseudofeedback 5 /u/mooney/ir-code/corpora/cf/
test_exp:
	java ir.eval.Experiment /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries cf-rp
	gnuplot cf-rp.gplot > cf-rp.ps
	gv cf-rp.ps
test_pseudo:
	java ir.eval.Experiment -pseudofeedback 0 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph00
	java ir.eval.Experiment -pseudofeedback 1 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph01
	java ir.eval.Experiment -pseudofeedback 5 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph05
	java ir.eval.Experiment -pseudofeedback 10 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph10
	java ir.eval.Experiment -pseudofeedback 15 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph15
	java ir.eval.Experiment -pseudofeedback 30 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph30
	java ir.eval.Experiment -pseudofeedback 50 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries pseudo_graph50
	rm pseudo_graph*.gplot
	gnuplot pseudo_plot.gplot > pseudo_graph.ps
	gv pseudo_graph.ps
test_beta:
	java ir.eval.Experiment -feedbackparams 1 0.1 1 -pseudofeedback 5 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries beta_graph01
	java ir.eval.Experiment -feedbackparams 1 0.5 1 -pseudofeedback 5 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries beta_graph05
	java ir.eval.Experiment -feedbackparams 1 1.0 1 -pseudofeedback 5 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries beta_graph10
	rm beta_graph*.gplot
	gnuplot beta_plot.gplot > beta_graph.ps
	gv beta_graph.ps
test_params:
	java ir.eval.Experiment -pseudofeedback 5 -feedbackparams 0.1 0.2 0.3 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries cf-rp
	gnuplot cf-rp.gplot > cf-rp.ps
	gv cf-rp.ps
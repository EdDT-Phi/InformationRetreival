default:
	javac ir/*/*.java
clean:
	rm ir/*/*.class
turnin: default
	rm proj2_et7226_code.zip
	zip proj2_et7226_code.zip ir/vsr/InvertedIndex.* ir/vsr/InvertedPhraseIndex* ir/vsr/Document.*
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
	java ir.eval.Experiment -pseudofeedback 0 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries cf-rp00
	java ir.eval.Experiment -pseudofeedback 1 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries cf-rp01
	java ir.eval.Experiment -pseudofeedback 5 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries cf-rp05
	java ir.eval.Experiment -pseudofeedback 10 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries cf-rp10
	java ir.eval.Experiment -pseudofeedback 15 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries cf-rp15
	java ir.eval.Experiment -pseudofeedback 30 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries cf-rp30
	java ir.eval.Experiment -pseudofeedback 50 /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries cf-rp50
	rm cf-rp*
	gnuplot actual_plot.gplot > visual.ps
	gv visual.ps
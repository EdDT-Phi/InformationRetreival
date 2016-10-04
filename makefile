default:
	javac ir/*/*.java
clean:
	rm ir/*/*.class
turnin: default
	rm proj1_et7226_code.zip
	zip proj1_et7226_code.zip ir/vsr/InvertedIndex.* ir/vsr/InvertedPhraseIndex* ir/vsr/Document.*
	unzip -l proj1_et7226_code.zip
test_bigrams:
	java ir.vsr.InvertedPhraseIndex -html -v /u/mooney/ir-code/corpora/cs-faculty/
test_fb:
	java ir.vsr.InvertedPhraseIndex -html -v -feedback /u/mooney/ir-code/corpora/cs-faculty/
test_pf:
	java ir.vsr.InvertedPhraseIndex -html -v -pseudofeedback /u/mooney/ir-code/corpora/cs-faculty/
test_exp:
	java ir.eval.Experiment /u/mooney/ir-code/corpora/cf/ /u/mooney/ir-code/queries/cf/queries cf-rp
	gnuplot cf-rp.gplot > cf-rp.ps
	gv cf-rp.ps

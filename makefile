default:
	javac ir/*/*.java
clean:
	rm ir/*/*.class
turnin: default
	rm proj1_et7226_code.zip
	zip proj1_et7226_code.zip ir/vsr/InvertedIndex.* ir/vsr/InvertedPhraseIndex* ir/vsr/Document.*
	unzip -l proj1_et7226_code.zip
test:
	java ir.vsr.InvertedPhraseIndex -html -v /u/mooney/ir-code/corpora/cs-faculty/
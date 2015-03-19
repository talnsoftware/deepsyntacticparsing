<h2>Deep-Syntactic Parser</h2>

<h2> See <a href='https://github.com/miguelballesteros/deepsyntacticparsing/wiki'><a href='https://github.com/miguelballesteros/deepsyntacticparsing/wiki'>https://github.com/miguelballesteros/deepsyntacticparsing/wiki</a></a> </h2>


``Deep-syntactic" dependency structures that capture the argumentative, attributive and coordinative relations between full words of a sentence have a great potential for a number of NLP-applications. The abstraction degree of these structures is in between the output of a syntactic dependency parser (connected trees defined over all words of a sentence and language-specific grammatical functions) and the output of a semantic parser (forests of trees defined over individual lexemes or phrasal chunks and abstract semantic role labels which capture the argument structure of predicative elements, dropping all attributive and coordinative dependencies). We propose a parser that delivers deep syntactic structures as output.

<ul>
<blockquote><li>Download of the Deep-Syntactic Parser: <a href='https://drive.google.com/file/d/0B8nESzOdPhLsS1lIZDJtbWdMVXc/edit?usp=sharing'>deepParser.jar</a>
</li>
<li>Download of the evaluation script: <a href='https://drive.google.com/file/d/0B8nESzOdPhLsa3NIei1ZX0QwMkE/edit?usp=sharing'>evaluation.jar</a>
</li>
</ul></blockquote>


---


This package contains the implementation of the system described in (Miguel Ballesteros, Bernd Bohnet, Simon Mille and Leo Wanner. Deep-Syntactic Parsing. COLING 2014)

<h3>USAGE - training and parsing.</h3>

<pre> java -jar deepParser.jar -s surfacetreebank -d deeptreebank -st surfaceinput -t 1 </pre>

this would train an SVM model and parse a surface input. It would produce a file "dsynt\_final\_output.conll" which is the output of the system.

<h3> USAGE - only parsing. </h3>

If you want to parse with an existing training model, you can use the following command.

<pre> java -jar deepParser.jar -s surfacetreebank -d deeptreebank -st surfaceinput -t 0 </pre>

It would produce a file "dsynt\_final\_output.conll" which is the output of the system.
Note that you should call it from the same folder in which you trained the model.

<h3> USAGE - long version. </h3>

Assuming that you want to parse plain text sentences and you have a SURFACE treebank and a DEEP treebank.

Let <pre>test.text</pre> be a corpus that you want to parse in which you have plain text sentences, one sentence per line.
The steps are:

1. Tokenize. You should tokenize your data. For that you can use any available tokenizer.
<br>
2. Transform to one token per line, that is correct CoNLL 2009 data format. You may use the Mate anna parser script for that:<br>
<a href='http://code.google.com/p/mate-tools/downloads/list'><a href='http://code.google.com/p/mate-tools/downloads/list'>http://code.google.com/p/mate-tools/downloads/list</a></a>
<br>

<pre>java -cp anna-3.3.jar is2.util.Split test.txt > testOneWordPerLine.txt</pre>

<br>
3. Lemmatize your data. Again you can use anna parser for that, and download any of the models available in <a href='http://code.google.com/p/mate-tools/downloads/list'><a href='http://code.google.com/p/mate-tools/downloads/list'>http://code.google.com/p/mate-tools/downloads/list</a></a>
<br>

<pre> java -Xmx2G -cp anna-3.3.jar is2.lemmatizer.Lemmatizer -model model.lemmatizer.model -test testOneWordPerLine.txt -out test_lemma.txt</pre>

<br>
4. Train and parse test_lemma.txt with a dependency parser, a pos-tagger and a morphology tagger. For that, we recommend Mate joint transition-based, pos tagger and morph tagger: <a href='http://code.google.com/p/mate-tools/wiki/ParserAndModels'><a href='http://code.google.com/p/mate-tools/wiki/ParserAndModels'>http://code.google.com/p/mate-tools/wiki/ParserAndModels</a></a>
Follow the instructions of Mate joint parser but you will end up running a script like this:<br>
<br>

<pre> nohup ./pet-lang-model >log.txt & </pre>

<br>
5. The output of Mate parser only fill predicted columns, you should write a script that fills all columns to be the input of the Deep-syntactic parser.<br>
Something like this would do, though you can come up with any script in any programming language. See conll2009 data format <a href='https://ufal.mff.cuni.cz/conll2009-st/task-description.html'> here</a>.<br>
<br>

<pre>cat outMate.txt | awk 'NF==0{print "\t"} NF{print $1, $4, $4, $4, $6, $6, $8, $8, $10, $10, $12, $12, $13, $14}' OFS="\t" > outputSurfaceParser.txt</pre>

<strong>Important note:</strong>
The Deep-Syntactic parser has as input <a href='https://ufal.mff.cuni.cz/conll2009-st/task-description.html'>CoNLL09 data format files</a>. In the deep syntax and surface syntax versions.<br>
It uses the following columns as input/features: (1) FORM, (2) LEMMA, (3) POS (4) FEAT, (5) HEAD, (6) DEPREL. That is, columns 0, 1, 2, 4, 5, 7 and 9. <br>
This means that the input should have this columns filled and it should be correctly formatted.<br>
<br>
6. Replace all /n/t by /n. Just to be sure that you have correct conll2009 data format.<br>
<br>
7. Train and parse with the <strong>deep-syntactic parser.</strong>
<br>
<pre>java -Xmx16g -jar deepParser.jar -s CorpusSSynt.conll -d CorpusDSynt.conll -st outputSurfaceParser.txt -t 1</pre>

(the flag -t 1 will train and parse, if it is 0 then it will only parse)<br>
<br>
<img src='http://taln.upf.edu/system/files/resources_files/dsynt.jpg' width='630' />
<br>(in the figure, "transducer" refers to Deep-Syntax parser)<br>
<hr />

<h3> EVALUATION </h3>
This script is able to evaluate the deep syntax output produced by the parser.<br>
<br>
<pre> java -jar evaluation.jar -g deepgold -s deepoutput </pre>

<hr />

If you have any questions or issues, please do not hesitate to contact <a href='http://miguelballesteros.com'>Miguel Ballesteros</a> (miguel.ballesteros@upf.edu)<br>
<br>
<hr />

<h3> References </h3>
Please, cite the following paper if you use the <b>deep-syntactic parser</b>.<br>
<br>
<ul>
<li>Miguel Ballesteros, Bernd Bohnet, Simon Mille and Leo Wanner. 2014. Deep-Syntactic Parsing. The 24th International Conference on Computational Linguistics (COLING 2014), Dublin, Ireland. </li>
</ul>

Please, cite the following paper if you use the <b>Spanish corpus</b>.<br>
<ul>
<li>Simon Mille, Alicia Burga and Leo Wanner. 2013. Ancora-UPF: A Multi-Level Annotation of Spanish. The 2nd International conference on Dependency Linguistics (DEPLING 2013)</li>
</ul>

If you have any doubts, please, do not hesitate and contact the authors.<br>

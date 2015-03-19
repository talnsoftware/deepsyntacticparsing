<h1>Deep-Syntactic Parser</h1>

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


---


<h3> EVALUATION </h3>
This script is able to evaluate the deep syntax output produced by the parser.

<pre> java -jar evaluation.jar -g deepgold -s deepoutput </pre>


---


If you want to use the whole pipeline, you should train a surface model with the joint parser, pos-tagger and morphology tagger. See, https://code.google.com/p/mate-tools/wiki/ParserAndModels
and then use its output as the input of the deep-syntactic parser.

<img src='http://taln.upf.edu/system/files/resources_files/dsynt.jpg' width='630' />
<br>(in the figure, "transducer" refers to Deep-Syntax parser)<br>
<br>
<strong>Important note:</strong>
The Deep-Syntactic parser has as input <a href='https://ufal.mff.cuni.cz/conll2009-st/task-description.html'>CoNLL09 data format files</a>. In the deep syntax and surface syntax versions.<br>
It uses the following columns as input/features: (1) FORM, (2) LEMMA, (3) POS (4) FEAT, (5) HEAD, (6) DEPREL. That is, columns 0, 1, 2, 4, 5, 7 and 9. <br>
This means that the input should have this columns filled and it should be correctly formatted.<br>
<br>
<hr />

If you have any questions or issues, please do not hesitate to contact <a href='http://miguelballesteros.com'>Miguel Ballesteros</a> (miguel.ballesteros@upf.edu)<br>
<br>
<hr />

Miguel Ballesteros, Bernd Bohnet, Simon Mille and Leo Wanner.
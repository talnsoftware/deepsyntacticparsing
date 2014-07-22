				================================
				Version 0.1 of Corpus contains:
				================================

=========================================================
		Sentences: 3.513
=========================================================

=========================================================
		Tokens: 66.980 (CHECK!!!)
=========================================================

=========================================================
	Relations: without ROOT; 7 different
=========================================================
APPEND
ATTR
COORD
I
II
III
IV

===============================================================
	  Attributes:702.919  (10,5/token; 48 different)
===============================================================
In the colums before column 7:
id (66.980; only used for storing in CoNLL format)
slex (66.980; actually the dlex, but called this way so it is stored in the second column)
lemma (66.980; actually additional information such as LF value or pronoun in SSynt)
PoS (66.980)

In column 7 (434.999, including attributes for mapping to semantics):
abbreviation
abs_pred
aux_rel
aux_rel2
bin_junct
control
coref_id
definiteness
double_coref
dpos
emphasis
finiteness
gender_coref
gender
id0
id1
id2
id3
id4
id5
interrogative
juxtaposition
main
mood
number_coref
number
person
prolepsis
quotative
reciprocal
refl_pronoun
rel_form
restrictive
sem_gov
sem_rel
sent_type
sent_type0
sequential
spos_coref
spos
ssynt_rel
tense
thematicity
voice


=========================================================
			About IDs
=========================================================
---------
In DSynt
---------
- Each node is identified with a unique ID: value of attribute id and id0; the value is always the same for those two (id0 because attribute id is lost in conll format).

--------------------------------
Correspondence with SSynt nodes
--------------------------------
Possible configurations:
* 1 DSynt Node <=> 1 SSynt Node
* 1 DSynt Node <=> 2 to n SSynt Nodes (dets, auxs, functional preps, phrasemes)
* 1 DSynt Node <=> nothing (empty subject pronouns)

- When a node only has id/id0, it indicates the correspondence with the SSyn node with the same id.
- when a node also has id1/2/3/4/5, these attributes indicate the correspondence with the various SSynt nodes.

This means that in DSynt in .str format, it is possible to find 3 times the same value: once for id, once for id0, and once for id1, id2, id3, id4, or id5.

------------------------------
Correspondence with Sem nodes
------------------------------
Possible configurations:
* 1 Sem Node <=> 1 DSynt Node
* 1 Sem Node <=> 2 to n DSynt Nodes (coreferences)
* 1 Sem Node <=> nothing (ROOT)
* n Sem Nodes <=> 1 DSynt Node (META nodes: TIME n={3}, ELABORATION n={2,3}, POSSESS n={2,3})

- the attribute id/id0 (DSynt) has a correspondence with the attribute id0/1/2/3/4/5... (Sem)
- the correspondence with SSynt nodes is also kept in Sem: the attributes id1/2/3/4/5 (DSynt) have a correspondence with the attributes idn_ssynt (Sem), with n={11-110}
Which means that:
- if a Sem node only has id0, it means that it corresponds to the node with the same id/id0 in DSynt and SSynt.
- if a Sem node has id0 and only id1/2/3/4/5, the correspondence with the DSynt and SSynt nodes should be found in the value of id1/2/3/4/5.
- if a Sem node has id0, id1/2/3/4/5, and idn_ssynt, id1/2/3/4/5 indicates the correspondence with the DSynt nodes and idn_ssynt the correspondence with the SSYnt nodes.

Maybe we want to simplify that in the future...

=========================================================
		STR Post-processing
=========================================================
- From correct_SSynt.str (see readme SSyntSPA), apply the following grammars in this order:
	- a-ssyntLin-dsynt.rl
	- CHECK_dsynt.rl

!!! The file CHECK_dsynt.rl in the folder of each version of the corpus (0.1, 0.2, etc.) is the version of the grammar that was used for getting the final DSynt. DO NOT EDIT THIS GRAMMAR!!!

=========================================================
		CONLL Post-processing
=========================================================
After applying the CHECK_dsynt grammar, some more changes to perform:

(1) add undescores to 'de nuevo'/'esto es' part of nodes
'de nuevo'
_de_nuevo_
'esto es
esto_es
(2*) copy nodenames when they are missing
^([0-9]+)\s+_\s+([^\s]+)
$1\t$2\t$2
(3) add missing ROOT column
^([0-9]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+ROOT\s+)([^\s]+\s+[^\s]+\s+)$
$1ROOT\t$2
(4) remove tab at end of each line
^([0-9]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+\s+[^\s]+)\t$
Replace by $1: there should be as many replacements as the total number of lines MINUS (number of sentences-1), which is also the number of tokens

For obtaining final DSynt, removed also:
- \|thematicity=[^\s\|]+
- \|main=[^\s\|]+
- cancel_first_conj=yes\|
- double_coref=yes\|
- first_conj=yes\|
- rel_form=[^\s\|]+\|
- \|ssynt_rel=[^\s\|]+
- ssynt_rel=[^\s\|]+\|
- \|type=[^\s\|]+
- abs_pred=yes\|
- interrogative=yes\|
- remove person from verbs
^(.*VV.+)\|person=[1-3] => $1

(also removed \|spos_noun=applied when it was not disabled in the CHECK_DSynt rules)

------------------------------------
BEFORE FINAL MAPPING
------------------------------------

- the ill-formed attributes may not have been passed to DSynt; look manually in SSynt_SPA for such cases and change them manually in DSynt.
	EX: gener= 
- Why do those attributes not show up in DSynt??
	spos=adjetive; spos=ajective; spos=advreb
Did I miss them while checking whih attributes are in DSynt? Or have they been lost while annotating?










=========================================================
 Attributes from original annotation: ??? (?? different)
=========================================================
--------------------
To be removed:
--------------------
aux_form=andan|andar|aparece|era|eran|es|está|estaba|estábamos|estaban|estamos|están|estar|estarán|estaría|esté|estoy|estuviera|estuviese|Estuvimos|fue|fueran|fueron|Fui|ha|haber|haberse|había|habíamos|habían|habías|habrá|habrás|habremos|habría|habríamos|habrían|han|has|haya|hayan|he|hemos|hubiera|hubiéramos|hubieran|hubiese|hubiesen|iba|iban|ir|sea|sean|ser|será|serán|sería|son|va|vamos|van|venía|voy
aux_form2=estado|ido|sido|venido
******* ADD aux_lemma?*******
blocked=yes
bubble=yes
ellided=yes
ellipsis=yes
leisme=yes
LEX=OK
ls=no
original_relation=obl_obj1
quasi_coord=yes
relativity=ABS|REL
type=Vgoverned_N
variant=yes

---------------------------------
Probably not needed at all?
---------------------------------
preposition=a|ante|como|con|contra|de|del|desde|durante|en|entre|hacia|hasta|para|por|que|sobre	//we could keep it to ease up teh learning of production of governed preps? SEE WITH BERND
rel_dep=adv|copul|dobj|obl_obj1|obl_obj2|subj

---------------------------------------------
Needed only for mapping to Sem (?):
We have to take into account the granularity of the SSyntRel tagset before removing/adding attributes (abbrev needed if we have the SSyntRel abbrev, not needed otherwise).
---------------------------------------------
!abbreviation=yes						(add node)
!abs_pred=yes							(adapt SemStr)
!(?) bin_junct=yes						(?)
!control=yes							(adapt SemStr)
!(?) double_coref=yes						(pro has two different nodes as antecedents: one case)
!emphasis=yes							(adapt CommStr); identical deps not removed so far!!
first_conj=yes
!juxtaposition=yes						(adapt SemStr)
!main=sentence|yes						(add main of each span)
!prolepsis=yes							(adapt CommStr)
!(?) quotative=yes						(adapt SemStr)
!reciprocal=yes							(adapt SemStr)
(?) refl_pronoun=me						(?)
rel_form=cual|cuando|donde|que|quien|quienes			(adapt SemStr)
!(?) restrictive=yes						(adapt CommStr -rest cannot be specifier)
sem_gov=I|II							(adapt SemStr)
sem_rel=1							(adapt SemStr)
!sequential=yes
ssynt_rel=compl1|compl2|obj_copred|subj_copred			(adapt SemStr)
thematicity=rheme|specifier|theme

------------------------------
Needed for final version
------------------------------
aux_rel=analyt_fut|analyt_pass|analyt_perf|analyt_progr		//together with tense, we don't lose anything of the verb form
aux_rel2=analyt_pass|analyt_progr				//together with tense, we don't lose anything of the verb form
definiteness=DEF|INDEF
dpos=A|Adj|Adv|AN|formula|IN|n|NN|noun|NP|Punct|V
finiteness=FIN|GER|IMP|INF|PART
gender=c|FEM|MASC|n|neuter|SG
*******ADD id0?*******
id1=0|1|10|100|101|102|103|104|105|106|107|109|11|110|111|112|113|114|115|117|118|119|12|120|121|122|124|125|126|128|129|13|130|132|133|136|137|13a|14|140|141|143|146|15|16|17|18|19|2|20|20a|21|22|23|24|25|26|27|28|29|3|30|31|32|33|34|34_bis|35|36|37|38|39|4|40|41|42|43|44|45|46|47|48|48a|49|5|50|51|52|53|54|55|56|57|58|59|6|60|61|62|63|64|65|66|67|68|69|7|70|71|72|73|74|75|76|77|78|79|8|80|81|82|83|84|85|86|87|88|89|9|90|91|92|93|94|95|96|97|98|99
id2=1|10|10_bis|100|101|102|103|104|105|106|107|108|109|11|110|111|112|113|114|115|116|117|118|119|12|120|121|122|123|126|127|128|13|131|132|135|137|138|139|14|142|144|145|15|16|17|18|19|2|20|21|22|23|24|25|26|26_bis|27|27_bis|28|29|3|30|31|32|33|34|34_bis|34_ter|35|36|37|38|39|4|40|41|42|43|43a|44|45|46|47|48|48b|49|5|50|51|52|53|54|55|56|57|58|59|6|60|61|62|63|64|65|66|67|68|69|7|7_bis|70|71|72|73|74|75|76|77|78|79|8|80|81|82|83|84|85|86|87|88|89|9|90|91|92|93|94|95|96|97|98|99
id3=10|102|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|29_bis|3|30|31|32|32_bis|33|34|35|36|37|37_bis|38|39|4|40|41|42|43|43b|44|46|47|48_bis|49|5|50|51|52|53|56|58|6|61|63|65|7|7_bis|8|80|9|9_bis
id4=11|13|17|19|20|25|27|29|32|35|36|37|6|9
id5=26
interrogative=yes
mood=IMP|IND|SUBJ
number=MASC|PL|SG
person=1|2|3
sent_type=declarative|exclamative|interrogative|suspensive
sent_type0=declarative|interrogative
spos=adjective|adverb|auxiliary|clitic_pronoun|conjunction|coord_conj|coord_conjunction|coordinating_conjunction|copul|copula|date|determiner|exclamative_determiner|exclamative_pronoun|formula|IN|interjection|interrogative_pronoun|negative_adverb|NN|noun|number|percentage|personal_pronoun|prepos|prepositiob|preposition|pronoun|pronun|proper_noun|propnoun|Punct|punctuation|relative_pronoun|roman_numeral|subordinate_conjunction|subordinating_conjunction|unit|ver|verb
tense=FUT|PAST|PRES
voice=PASS




=========================================
		PROBLEMS
=========================================
- There are errors in original AnCora: tags appear as nodes (all start with "ZZ").
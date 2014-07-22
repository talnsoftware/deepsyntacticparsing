======================
ATTRIBUTES DSYNT V2.0
======================

- abbreviation: the dependent of the ATTR/APPEND relation is an abbreviation of the governor. 
Possible values: yes

- aux_rel: substitutes to tense for analytical forms (temporary)
Possible values: analyt_fut|analyt_pass|analyt_perf|analyt_progr

- aux_rel2: substitutes to tense for multiple analytical forms (temporary)
Possible values: analyt_pass|analyt_progr

- bin_junct: specifies the DSyntRel: the Gov and Dep are linked by an edge bin_junct in SSynt (temporary)
Possible values: yes

- control: specifies the type of the verb; can have an influence on the SSyntRel of surrounding nodes
Possible values: yes

- coref_id: indicates with which other node of the sentence the current node has a coreference relation with; the value of coref_id corresponds to the value of id0 on the coreferring node
Possible values: 1|1_prosubj|10|10_prosubj|102|103|104|105|106|107|108|109|11|11_prosubj|110|111|115|117|12|12_prosubj|121|13|13_prosubj|14|14_prosubj|15|15_prosubj|16|16_prosubj|17|17_prosubj|18|18_prosubj|19|19_prosubj|2|2_prosubj|20|20_prosubj|21|22|22_prosubj|23|24|24_prosubj|25|25_prosubj|26|26_prosubj|27|27_prosubj|28|29|29_prosubj|3|3_prosubj|30|31|32|32_prosubj|33|34|34_prosubj|35|35_prosubj|36|37|38|38_prosubj|39|4|4_prosubj|40|41|42|43|44|44_prosubj|45|46|47|47_prosubj|48|48_prosubj|49|5|5_prosubj|50|51|52|53|54|55|56|57|58|59|59_prosubj|6|6_prosubj|60|61|62|63|64|64_prosubj|65|66|67|7|7_prosubj|70|71|72|73|74|76|77|78|8|8_prosubj|81|83|84|85|86|87|9|9_prosubj|90|91|92|95|96|98

- definiteness: indicates that noun has a determiner, and which kind
Possible values: DEF|INDEF

- dpos: deep part-of-speech
Possible values: A|Adv|N|V

- ellipsis: indicates that the DSynt node does not appear in the corresponding SSynt
Possible values: yes

- emphasis: piece of information structure that can indicate a pronominal duplication of the argument in the final sentence
Possible values: yes

- finiteness: indicates if the verb is finite, infinitive, gerund, or past-participle
Possible values: FIN|GER|INF|PART

- focalized: piece of information structure that can constrain the order of the words (focalized=in front of other elements in the sentence)
Possible values: yes

- gender_coref: indicates gender of coreferring element
Possible values: C|FEM|MASC

- gender: indicates gender
Possible values: C|FEM|MASC

- id0: unique ID in the DSynt structure
Possible values: 1|1_prosubj|10|10_prosubj|100|101|102|103|104|104_prosubj|105|106|106_prosubj|107|108|109|11|11_prosubj|110|111|112|113|114|115|116|116_prosubj|117|118|118_prosubj|119|12|12_prosubj|120|120_prosubj|121|122|123|124|125|126|127|128|128_prosubj|129|13|13_prosubj|130|131|132|133|134|134_prosubj|135|135_prosubj|136|137|138|138_prosubj|139|14|14_prosubj|140|141|142|143|144|146|15|15_prosubj|16|16_prosubj|17|17_prosubj|18|18_prosubj|19|19_prosubj|2|2_prosubj|20|20_prosubj|21|21_prosubj|22|22_prosubj|23|23_prosubj|24|24_prosubj|24bis|25|25_prosubj|26|26_prosubj|27|27_prosubj|28|28_prosubj|29|29_prosubj|3|3_prosubj|30|30_prosubj|31|31_prosubj|32|32_prosubj|33|33_prosubj|34|34_prosubj|35|35_prosubj|36|36_prosubj|37|37_prosubj|38|38_prosubj|39|39_prosubj|4|4_prosubj|40|40_prosubj|41|41_prosubj|42|42_prosubj|43|43_prosubj|44|44_prosubj|45|45_prosubj|46|46_prosubj|47|47_prosubj|48|48_prosubj|49|49_prosubj|5|5_bis|5_prosubj|50|50_prosubj|51|51_prosubj|52|52_prosubj|53|53_prosubj|54|54_prosubj|55|55_prosubj|56|56_prosubj|57|57_prosubj|58|58_proObj|58_prosubj|59|59_prosubj|6|6_prosubj|60|60_prosubj|61|61_prosubj|62|62_prosubj|63|63_prosubj|64|64_prosubj|65|65_prosubj|66|66_prosubj|67|67_prosubj|68|68_prosubj|69|69_prosubj|7|7_prosubj|70|70_prosubj|71|71_prosubj|72|73|74|74_prosubj|75|76|77|77_prosubj|78|78_prosubj|79|79_prosubj|8|8_prosubj|80|80_prosubj|81|81_prosubj|82|83|84|84_prosubj|85|86|86_prosubj|87|88|89|89_prosubj|9|9_prosubj|90|91|91_prosubj|92|92_prosubj|93|94|95|96|97|98|99|99_prosubj

- id1: correspondence with SSynt nodes; the value of id1 corresponds to the value of "id" on the SSynt node
Possible values: 1|10|100|101|102|103|104|105|106|107|108|109|11|110|111|112|113|114|115|117|118|119|12|121|122|124|125|126|129|13|130|132|133|134|136|137|14|140|141|143|146|15|16|17|18|19|2|20|21|22|23|24|25|26|27|28|29|3|30|31|32|33|34|35|36|37|38|39|4|40|41|42|43|44|45|46|47|48|49|5|50|51|52|53|54|55|56|57|58|59|6|60|61|62|63|64|65|66|67|68|69|7|70|71|72|73|74|75|76|77|78|79|8|80|81|82|83|84|85|86|87|88|89|9|90|91|92|93|94|95|96|97|98|99

- id2: correspondence with SSynt nodes; the value of id1 corresponds to the value of "id" on the SSynt node
Possible values: 1|10|100|101|102|103|104|105|106|107|108|109|11|110|111|112|113|114|115|116|117|118|12|120|121|122|123|125|126|127|128|13|130|131|132|133|135|137|138|139|14|142|144|145|15|16|17|18|19|2|20|21|22|23|24|25|26|27|28|29|3|30|31|32|33|34|35|36|37|38|39|4|40|41|42|43|44|45|46|47|48|49|5|50|51|52|53|54|55|56|57|58|59|6|60|61|62|63|64|65|66|67|68|69|7|70|71|72|73|74|75|76|77|78|79|8|80|81|82|83|84|85|86|87|88|89|9|90|91|92|93|94|95|96|97|98|99

- id3: correspondence with SSynt nodes; the value of id1 corresponds to the value of "id" on the SSynt node
Possible values: 10|103|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|3|30|31|32|33|34|35|36|37|38|39|4|40|41|42|43|44|46|47|49|5|50|51|54|56|58|59|6|61|63|65|7|8|80|9|11|13

- id4: correspondence with SSynt nodes; the value of id1 corresponds to the value of "id" on the SSynt node
Possible values: 15|17|19|20|25|27|29|32|35|36|37|4|41|52|6|8|9

- id5: correspondence with SSynt nodes; the value of id1 corresponds to the value of "id" on the SSynt node
Possible values: 26

- juxtaposition: indicates if the subtree governed by this node is juxtaposed to the governing element (juxtapos edge in SSynt)
Possible values: yes

- mood: mood of the verb
Possible values: IMP|IND|SUBJ

- number_coref: indicates number of coreferring element 
Possible values: PL|SG|PL|SG

- person: lexical feature: person of the noun/pronoun
Possible values: 1|2|3

- prolepsis: indicates if the node is an element that is anteposed without connector (this node always duplicates a dependent of the main node)
Possible values: yes

- quotative: indicates if the group governed by this node is direct speech
Possible values: yes

- reciprocal: subspecifies verb types (only one instance of that thing in the corpus)
Possible values: yes

- restrictive: lexical feature that subspecifies a type of ATTR (mapped to "restr" SSyntRel)
Possible values: yes

- sem_gov: indicates which of its siblings is its semantic governor; it has an impact on the agreement between sentence units
Possible values: I|II

- sem_rel: indicates which relation connects the semantic governor with this node (in order to know which is the semantic governor, see sem_gov)
Possible values: 1

- sent_type: meta-attribute that indicates the type of sentence; results in introducing final punctuation (. ! ? ...)
Possible values: declarative|exclamative|interrogative|suspensive

- sent_type0: meta-attribute that indicates the type of a phrase that this embedded verb governs; results in introducing a punctuation sign (. ?)
Possible values: declarative|interrogative

- sequential: subspecifies the type of ATTR (temporary); incoming ATTR mapped to "sequent" SSyntRel in this case
Possible values: yes

- spos_coref: indicates spos of coreferring element
Possible values: adjective|adverb|copula|determiner|interjection|noun|number|preposition|pronoun|proper_noun|relative_pronoun|verb

- spos: surface part-of-speech (fine-grained)
Possible values: adjective|adverb|auxiliary|conjunction|copula|determiner|formula|interjection|interrogative_pronoun|noun|number|percentage|preposition|pronoun|proper_noun|punctuation|relative_pronoun|roman_numeral|verb

- tense: tense of the verb
Possible values: FUT|PAST|PRES

- voice: voice of the verb
Possible values: PASS

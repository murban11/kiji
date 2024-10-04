# Kiji

An article classifier that categorizes articles based on the country their content pertains.

## Building

```console
$ mvn compile assembly:single
```

## Running

```console
$ java -jar target/kiji-<VERSION>-jar-with-dependencies.jar [OPTIONS]
```

Replace `<version>` in the command above with specific version tag, e.g. `1.0-SNAPSHOT`.

### Options

| Option | Description |
| --- | --- |
| `-k COUNT` / `--neighbour-count COUNT` | Set the number of neighbours used in $k$-NN algorithm to `COUNT`. The default neighbour count is `2`. |
| `-r RATIO` / `--training-ratio RATIO` | Set the training to testing ratio to `RATIO`. The default training ratio is `0.1`. |
| `-s` / `--shuffle` | Shuffle the whole dataset before splitting into training and testing sets. |
| `-m METRIC` / `--metric METRIC` | Set the metric used when comparing feature vectors to `METRIC`, which can be one of the following: `euclidean`, `taxicab`, or `chebyshev`. By default, the `euclidean` metric is used. |
| `--disable-<FEATURE>` | Disable the given `FEATURE`, which can be one of the following: `west-german-political-count`, `canadian-city-freq`, `french-bank-presence`, `uk-acronym-presence`, `japanese-company-presence`, `usa-state-presence`, `capitals-presence`, `currencies-presence`, `first-capitalized-word`, `first-number`, `most-frequent-acronym`, or `title` |
| `--ignore-<COUNTRY>` | Ignore the given `COUNTRY`, which can be one of the following: `west-germany`, `usa`, `france`, `uk`, `canada`, `japan`. |

## Objective

The goal of the project is to classify articles with respect to the country to which the content relates.

Both training and testing is done based on the Reuters-21578 dataset[^1]. Articles are classified according to the places defined by the following labels: **west-germany**, **usa**, **france**, **uk**, **canada**, **japan**. All articles are from issues of Reuters magazine published in 1987.

The desired result is as effective classification (assording to accuracy, precision, sensitivity, and $F_1$ measures) as possible.

## Supervised classification using the $k$-NN method

The $k$-NN ($k$-nearest neighbours) method involves classifying an object (such as a text document) based on the labels of its $k$ nearest neighbors. The object is assigned the class most common among its neighbours.

The input parameters of the $k$-NN method are the number of neighbours ($k$), the proportions of the training and test sets, the feature vector, and metric or similarity measure according to which the nearest neighbours will be determined.

The first stage of classification is training, which involves determining the values of feature vectors for each object from the training set. The next step is to test a given classifier based on the effectiveness of classifying objects from the test set.

The result of the classification is the assignment of labels to objects in the test set.

## Feature extraction

Feature extraction is the process of determining a sequence of qualities (features) describing a given object.

Below is a list of the features we have definied. Unless otherwise noted, these features refer to the state as of 1987, e.g. for the feature being the number of occurrences of names of West German politicians, we consider only politicians who held political office in 1987. In the following descriptions, by multiset we mean a set whose elements can repeat.

1. Number of occurrences of names of West German politicians:

    $$\mathit{np}^D_{\text{west-germany}} = |p: p \in P_{\text{west-germany}} \land p \in D|$$

    where:

      - $\mathit{np}^D_{\text{west-germany}}$ &ndash; The number of occurences of the names of West German politicians in the multiset of words of a given document $D$, while also counting all repretitions of a given name.
      - $P_{\text{west-germany}}$ &ndash; A set of names of West German politicians.
      - $D$ &ndash; Multiset of all words in a given document.

    For example, for the text "In 1987, *Willy Brandt*, the former Chancellor of West Germany, made an important speech regarding East-West relations," the value of this feature is 1.

2. Frequency of Canadian city names:

    $$\mathit{fw}^D_{\text{canada}} = |w: w \in W_{\text{canada}} \land w \in D|/|D|$$

    where:

      - $\mathit{fw}^D_{\text{canada}}$ &ndash; The frequency of occurrence of Canadian cities in the multiset of words of a given document $D$, while also counting all repetitions of a given city name.
      - $W_{\text{canada}}$ &ndash; A set of Canadian city names.
      - $D$ &ndash; Multiset of all words in a given document.

    Here we assume that the set of words of each document is non-empty. For the text "Tourists came to Canada in record numbers last year, attracted by the relatively weak Canadian dollar and Expo 86 in *Vancouver*, which alone had more than 22 mln visitors," the value of this feature is $1/29$.

3. Presence of French bank names:

    $`\begin{align}\mathit{o}^D_{\text{france}} = \begin{cases}1\quad \text{if } \{o: o \in O_{\text{france}} \land o \in D\} \neq \emptyset \\ 0\quad \text{otherwise}\end{cases}\end{align}`$

    where:

      - $\mathit{o}^D_{\text{france}}$ &ndash; A boolean value indicating whether document $D$ contains a name of a Franch bank.
      - $O_k$ &ndash; A set of Frech bank names.
      - $D$ &ndash; Multiset of all words in a given document.

    For example, for the text "The issue will be co-led by *Banque Nationale de Paris*, *Caisse Nationale de Credit Agricole* and the *Societe Marseillaise de Credit*," the value of this feature is 1.

4. Presence of the "U.K." acronym:

    $`\begin{align}\mathit{u}^D_{\text{uk}} = \begin{cases}1\quad \text{if } \text{``U.K.''} \in D \\ 0\quad \text{otherwise}\end{cases}\end{align}`$

    where:

      - $\mathit{u}^D_{\text{uk}}$ &ndash; A boolean value indicating whether document $D$ contains the "U.K." acronym.
      - $D$ &ndash; Multiset of all words in a given document.

    For example, for the text "Sony (*U.K.*) Ltd said it would be doubling capacity at its Bridgend, Wales, television and components factory over the next three years in a 30 mln stg expansion," the value of this feature is 1.

5. Presence of Japanese company names:

    $`\begin{align}\mathit{c}^D_{\text{japan}} = \begin{cases}1\quad \text{if } \{c: c \in C_{\text{japan}} \land c \in D\} \neq \emptyset \\ 0\quad \text{otherwise}\end{cases}\end{align}`$

    where:

      - $\mathit{c}^D_{\text{japan}}$ &ndash; A boolean value indicating whether document $D$ contains a name of a Japanese company.
      - $C_{\text{japan}}$ &ndash; A set of Japanese company names.
      - $D$ &ndash; Multiset of all words in a given document.

    For example, for the text "*Mazda* appoints John Smith as the new CEO of its European division, aiming to strengthen its presence in the competitive automotive market," the value of this feature is 1.

6. Presence of the names of the states from United States:

    $`\begin{align}\mathit{h}^D_{\text{usa}} = \begin{cases}1\quad \text{if } \{h: h \in H_{\text{usa}} \land h \in D\} \neq \emptyset \\ 0\quad \text{otherwise}\end{cases}\end{align}`$

    where:

      - $\mathit{h}^D_{\text{usa}}$ &ndash; A boolean value indicating whether document $D$ contains a name of a state from United States.
      - $H_k$ &ndash; A set of names of the states from United States.
      - $D$ &ndash; Multiset of all words in a given document.

    For example, for the text "High volume memory chips have become "a perfect commodity market," Robert Brodersen, a professor of electrical engineering at the University of *California* at Berkeley, told an industry forum,", the value of this feature is 1.

7. Presence of captial names of each country:

    $`\mathit{s}^D = [\mathit{s}^D_{\text{west-germany}}, \mathit{s}^D_{\text{usa}}, \mathit{s}^D_{\text{france}}, \mathit{s}^D_{\text{uk}}, \mathit{s}^D_{\text{canada}}, \mathit{s}^D_{\text{japan}}]`$

    where:

      - $`\mathit{s}^D`$ &ndash; A feature vector in which each component determines whether the name of the corresponding capital is present in the $D$ document.
      - $`\mathit{s}^D_k`$ &ndash; A boolean value indicating whether the document $D$ contains the capital name of the $k$ country.
      - $`s_k`$ &ndash; The capital name of the $k$ country.
      - $D$ &ndash; Multiset of all words in a given document.

    For example, for the text "The mayors of *Washington, D.C.* and *Tokyo* met to discuss strategies for urban development and cultural exchange between their respective capitals," the value of this feature is $`[0, 1, 0, 0, 0, 1]`$.

8. Presence of currencies from each country:

    $`\mathit{m}^D = [\mathit{m}^D_{\text{west-germany}}, \mathit{m}^D_{\text{usa}}, \mathit{m}^D_{\text{france}}, \mathit{m}^D_{\text{uk}}, \mathit{m}^D_{\text{canada}}, \mathit{m}^D_{\text{japan}}]`$

    whereby:

    $`\begin{align}\mathit{m}^D_k = \begin{cases}1\quad \text{if } \{m: m = m_k \land m \in D\} \neq \emptyset \\ 0\quad \text{otherwise}\end{cases}\end{align}`$

    where:

      - $`\mathit{m}^D`$ &ndash; A feature vector in which each component determines whether the name of the corresponding currency is present in the $D$ document.
      - $`\mathit{m}^D_k`$ &ndash; A boolean value indicating whether the document $D$ contains the name of the currency from the $k$ country.
      - $`m_k`$ &ndash; The name of the currency from the $k$ country.
      - $D$ &ndash; Multiset of all words in a given document.

    For example, for the text "The latest financial report indicates a significant increase in the value of the *British pound* against the *US dollar*," the value of this feature is $`[0, 1, 0, 1, 0, 0]`$.

9. The first capitalized word in the document:

    $`\mathit{w}^{D} = d_0`$

    where:

      - $`\mathit{w}^{D}`$ &ndash; The first capitalized word in the $D$ document.
      - $`(d_n)_{n \in \mathbb{N}}`$ &ndash; The sequance of consecutive capitalized words in the $D$ document.

    For example, for the text "*Bean* shippers were reluctant to offer nearby shipment and only limited sales were booked for March shipment at 1,750 to 1,780 dlrs per tonne to ports to be named,", the value of this feature is "Bean".

10. The first numeric value in the document:

    $`\begin{align}\mathit{r}^{D} = \begin{cases}a_0 \quad \text{if } \{a_n\} \neq \emptyset \\ \text{"" (empty text)} \quad \text{otherwise}\end{cases} \end{align}`$

    where:

      - $`\mathit{r}^D`$ &ndash; The first numeric value in the $D$ document.
      - $`(a_n)_{n \in \mathbb{N}}`$ &ndash; The sequence of consecutive numeric values in the $D$ document.

    By numeric value we mean a sequence of digits optionally containing a single dot at a position other than the first or last or a number of commas at positions that are multiples of the number 4 counting from the end excluding the first (leftmost) position.

    For example, for the text "The survey gave the Conservatives *44* pct of the votes, left-wing Labour and the centrist Liberal-Social Democratic Alliance 27 pct each," the value of this feature is "44".

11. The most common acronym in the document:

    $`\mathit{n^{D}} = b\text{,} \quad b \in A \land \left(\sum_{d \in A} 1_{b}(d) \geq \sum_{d \in A} 1_{c}(d) \quad \forall c, c \neq b \land c \in A\right)`$

    where:

      - $`\mathit{n^{D}}`$ &ndash; The most common acronym in the $D$ document.
      - $A$ &ndash; The multiset of all acronyms in the $D$ document.

    In case we have more than one most common acronym in the document, we choose the one that is first alphabetically. By acronym we mean a sequence of capital letters, with a length greater than 1, optionally containing a period character after each letter.

    For example, for the text "Analyst Rosemarie Morbelli of Ingalls and Snyder said ServiceMaster Companies *L.P.* &lt;*SVM*&gt; or Rollins Inc &lt;*ROL*&gt; were examples of companies that could be interested," the value of this feature is "*L.P.*", because it is before "*SVG*" or "*ROL*" alphabetically.

12. The title of the given document:

    $`t^D`$

    where:

      - $`t^D`$ &ndash; The title of the $D$ document.

## Feature vector

A feature vector is a data structure that stores the values of features calculated for an object. Its individual components are values of one of four types: logical, numerical, textual, or vector. The existence of feature vectors is essential for the operation of the $k$-NN algorithm. They are used to determine the similarity of the features of a given object with those from the training set. Based on this similarity, the distance between an object and its neighbours is determined according to a selected measure.

In our implementation, the feature vector has the following form:
```math
\begin{align*}
    V^D = [ \\
        &\mathit{np}^D_{\text{west-germany}} & &\mathit{fw}^D_{\text{canada}}, & &\mathit{o}^D_{\text{france}}, & &\mathit{u}^D_{\text{uk}}, & &\mathit{c}^D_{\text{japan}}, & &\mathit{h}^D_{\text{usa}}, \\ 
        &\mathit{s}^D, & &\mathit{m}^D, & &\mathit{w}^D, & &\mathit{r}^D, & &\mathit{n}^D, & &\mathit{t}^D \\
    ]
\end{align*}
```

## Measures of classification quality

Let $C_e$ denote class $e$, that is, the set of articles whose proper label is label $e$, and let $N_{i,j}$ denote the number of articles whose proper class is class $C_i$, but which have been assigned to class $C_j$, where $e,i,j \in \\{\text{west-germany}, \text{usa}, \text{france}, \text{uk}, \text{canada}, \text{japan}\\}$.

We distinguish the following possible results of classifying an object against the class $C_e$:

* **Correctly assigned** to class $C_e$, i.e. one in which the article has been assigned to the class $C_e$ to which it actually belongs, e.g., if an article has been assigned the label **uk** and its content is actually related to the United Kingdom, we consider that the article has been *correctly assigned* to class $C_{\text{uk}}$. The number of *correctly assigned* articles relative to class $C_e$ is given by the following formula:

    $`\mathit{CA}_e = N_{e,e}`$

* **Incorrectly assigned** to class $C_e$, i.e. one in which the article has been assigned to the class $C_e$ to which it does not actually belongs, e.g., if an article has been labeled *france*, but its content is related to United States rather than France, we consider the article to have been incorrectly assigned to class $C_{\text{france}}$. The number of *incorrectly assigned* articles relative to class $C_e$ is given by the following formula:

    $`\mathit{IA}_e = \sum_{\substack{j=1 \\ j\neq e}}^6 N_{e, j}`$

* **Correctly unassigned** to class $C_e$, i.e. one in which the article has not been assigned to the class $C_e$ to which it in fact does not belong, e.g., if an article has not been assigned the label *canada* and its content is in fact not related to Canada, we consider that the article has been correctly unassigned to the class $`C_{\text{canada}}`$. The number of *correctly unassigned* articles relative to class $`C_e`$ is given by the following formula:

    $`\mathit{CU}_e = \sum_{\substack{i=1 \\ i\neq e}}^6 \sum_{\substack{j=1 \\ j\neq e}}^6 N_{i, j}`$

* **Incorrectly unassigned** to class $C_e$, i.e. one in which the article has not been assigned to the class $C_e$ to which it actually belongs, e.g., if the content of the acticle is about Japan, and it has been given a label other than *japan*, we consider the article to have been *incorrectly unassigned* to class $`C_{\text{japan}}`$. The number of *correctly unassigned* articles relative to class $`C_e`$ is given by the following formula:

    $`\mathit{IU}_e = \sum_{\substack{i=1 \\ i\neq e}}^6 N_{i, e}`$

The following measures have been used in order to evaluate the quality of classification:

* **Accuracy**, i.e. the ratio of the number of articles that have been correctly classified to the number of all articles in the dataset, e.g. if 37 articles have been correctly classified, while there are 45 articles in total, the accuracy of this classification is $`37/45\approx0.82`$. Unlike the other measures, we calculate accuracy in total for all classes instead of for each class individually. We calculate the accuracy using the following formula:

    $`\mathit{ACC} = \frac{\sum_{i=1}^6 N_{i, i}}{\sum_{i=1}^6 \sum_{j=1}^6 N_{i, j}}`$

* **Sensitivity** or *correctly assigned ratio*, which is the ratio of the number of articles belonging to a given class that were correctly classified to the number of all articles in the class, e.g. if 13 articles were correctly classified as belonging to class *uk*, while in fact the content of 15 articles is related to UK, then the sensitivity in the context of class $`C_{\text{uk}}`$ is $`13/15\approx0.87`$. We calculate the sensitivity using the following formula:

    $`\mathit{CAR}_e = \frac{\mathit{CA}_e}{\mathit{CA}_e + \mathit{IU}_e} = \frac{N_{e,e}}{\sum_{j=1}^6 N_{e,j}}`$

* **Precision** or *positive predictive value*, which is the ratio of the number of articles correctly classified as belonging to a given class to the number of all articles that were classified as belonging to that class, e.g. if 13 articles were correctly classified as belonging to class *uk*, while a total of 18 articles were classified as belonging to this class, the precision in context of class $`C_{\text{france}}`$ is $`13/18\approx0.72`$. We calculate the precision using the following formula:

    $`\mathit{PPV}_e = \frac{\mathit{CA}_e}{\mathit{CA}_e+\mathit{IA}_e} = \frac{N_{e, e}}{\sum_{i=1}^6 N_{i, e}}`$

* **$F_1$**, which is the harmonic average of precision and sensitivity, which we calculate using the following formula:

    $`F_1^e = 2\cdot\frac{\mathit{PPV}_e\cdot\mathit{CAR}_e}{\mathit{PPV}_e+\mathit{CAR}_e} = 2\cdot\frac{N_{e,e}}{\sum_{i=1}^6 N_{i, e}+\sum_{j=1}^6 N_{e, j}}`$

* **Weighted mean of sensitivity** which we calculate using the following formula:

    $`\overline{\overline{{\mathit{CAR}}}} = \sum_{i=e}^6 \mathit{CAR}_e \cdot \frac{\sum_{j=1}^6 N_{e, j}}{\sum_{i=1}^6 \sum_{j=1}^6 N_{i, j}}`$

* **Weighted mean of precision** which we calculate using the following formula:

    $`\overline{\overline{{\mathit{PPV}}}} = \sum_{i=e}^6 \left(\frac{\mathit{PPV}_e \cdot \sum_{j=1}^6 N_{e, j}}{\sum_{i=1}^6 \sum_{j=1}^6 N_{i, j}}\right)`$

    The expression to the right side of the multiplication is the ratio of the number of articles whose actual class is $C_e$ to the number of all articles.

* **Weighted mean of $F_1$** which we calculate using the following formula:

    $`\overline{\overline{{F_1}}} = \sum_{i=e}^6 F_1^e \cdot \frac{\sum_{j=1}^6 N_{e, j}}{\sum_{i=1}^6 \sum_{j=1}^6 N_{i, j}}`$

## Metrics and measures of text similarity in classification

For $k$-NN classification to be possible, there must be a way to determine the relative distance of two feature vectors. We will determine the distance of two feature vectors by determining the distance of the corresponding pairs of components of these vectors according to a distance measure appropiate for the type of the given feature, and determining the total distance of these vectors.

### Distance measures of individual features

Below are the measures we will use to calculate the distances of corresponding pairs of features.

1. In case of the $`\mathrm{np}`$ feature, the distance is calculated as follows:

    $`\mu\left(\mathrm{np}^{D_1}, \mathrm{np}^{D_2}\right) = \left|\mathrm{np}^{D_1} - \mathrm{np}^{D_2}\right| / \mathrm{np}_{\text{max}}`$

    where $`\mathrm{np}_{\text{max}}`$ is the largest value of this feature for all articles in the dataset. For example, if the number of occurrences of politicians' names in document $`D_1`$ is 7, while in document $`D_2`$ the value of this feature is 11, while the value of $`\mathrm{np}_{\text{max}}`$ is 22, then the normalized distance of these features is: $`|7-11|/22\approx0.18`$.

2. In case of the $`\mathrm{fw}`$ feature, the distance is calculated as follows:

    $`\mu\left(\mathrm{fw}^{D_1}, \mathrm{fw}^{D_2}\right) = \left|\mathrm{fw}^{D_1} - \mathrm{fw}^{D_2}\right| / \mathrm{fw}_{\text{max}}`$

    where $`\mathrm{np}_{\text{max}}`$ is the largest value of this feature for all articles. For example, if the frequency of occurrences of Canadian city names in document $`D_1`$ is $`0.003`$, while in document $`D_2`$ it is $`0.008`$, while the value of $`\mathrm{np}_{\text{max}}`$ is 0.014, then the distance of the values of these feature is: $`|0.003-0.008| / 0.014 \approx 0.35`$.

3. In case of features $o$, $u$, $c$, $h$, which take values of 1 or 0 representing the presence or absence of a certain value or pattern in the document respectively, we calculate the distance as follows:

    $`\begin{equation}\mu\left(\mathrm{x}^{D_1}, \mathrm{x}^{D_2}\right) = \begin{cases} 0\quad \text{if } \mathrm{x}^{D_1} = \mathrm{x}^{D_2} \\ 1\quad \text{otherwise}\end{cases}\end{equation}`$

    where $x$ is one of the features $o$, $u$, $c$, or $h$. For example, if both the article $`D_1`$ and $`D_2`$ contain the name of a Japanese company (not necessarily the same one), then the distance of the values $`c^{D_1}`$ and $`c^{D_2}`$ will be 0.

4. In case of the $s$ and $m$ features, we calculate the distance based on the Hamming distance of the sequences of component values of the vectors representing these features:

    $`\mu\left(\mathrm{y}^{D_1}, \mathrm{y}^{D_2}\right) = \frac{H\left(\mathrm{y}^{D_1}, \mathrm{y}^{D_2}\right)}{H_{\text{max}}}`$

    where $y$ is one of the features $s$ or $m$, $`H\left(\mathrm{y}^{D_1}, \mathrm{y}^{D_2}\right)`$ is the Hamming distance of the values of these features, while $`H_{\text{max}}`$ is the largest possible distance between two different values of a feature equal to the number of components of the feature vector. For both the feature $s$ and the feature $m$, the value of $`H_{\text{max}}`$ is 6. For example, if the value of $`s^{D_1}`$ is $`[1, 0, 1, 1, 0, 1]`$, while the value of $s^{D_2}$ is $`[0, 0, 1, 0, 0, 1]`$, then their distance has the value: $2/6\approx0.33$.

5. In case of the $w$ feature, we calculate the distance based on the 3-gram method:

    $`\mu\left(\mathrm{w}^{D_1}, \mathrm{w}^{D_2}\right) = 1 - \frac{1}{N-2}\sum_{i=1}^{N-2} h(i)`$

    where $N$ is the number of letters of the longer of the words that are values of $`\mathrm{w}^{D_1}`$ and $`\mathrm{w}^{D_2}`$. The $`h(i)`$ is equal to 1 if the $i$-th 3-letter sequence from $`\mathrm{w}^{D_1}`$ occurs in $`\mathrm{w}^{D_2}`$ and 0 otherwise. For example, for the words "compiler" and "compilation", the following 3-letter sequences of the first word also occurs in the second word: *com*, *omp*, *mpi*, and *pil*, so their distance is: $`1 - (1/11)\cdot4\approx0.64`$.

6. In case of features $r$ and $n$, we calculate the distance by comparing the values of these features as follows:

    $`\mu\left(\mathrm{z}^{D_1}, \mathrm{z}^{D_2}\right) = \begin{cases} 0\quad \text{if } \mathrm{z}^{D_1} = \mathrm{z}^{D_2} \\ 1\quad \text{otherwise}\end{cases}`$

    where $z$ is one of the features $r$ or $n$. For example, if the value of the $r$ feature in document $`D_1`$ is 1970, while in document $`D_2`$ it is 1971, their distance is 1.

7. In case of the $t$ feature, we calculate the distance by filtering out words using a stop list, stemming the words in titles of the two documents, eliminating duplicate words in each title separately, determining the number of words repeating in both titles and dividing the resulting value by the number of words in the longer of the resulting strings. This can be represented by the following formula:

    $`\mu\left(\mathrm{t}^{D_1}, \mathrm{t}^{D_2}\right) = 1 - \frac{\sum_{i=1}^N h(i)}{N}`$

    where $`h(i)`$ is equal to 1 in case, where the $i$-th word in the (processed) title of $`D_1`$ article is also present in the $`D_2`$ article, and 0 otherwise. Let the following titles be given:

      - "The Importance of Data Analysis in Business Decision Making"
      - "Data Driven Strategies Leveraging Data for Effective Business Decisions"

    After removing duplicates and words that are in the stop list ("the", "of", "in", "for"), and after the stemming process using the Snowball stemmer in the English version[^2], the titles take the following form:

      - "import data analysi busi decis make"
      - "driven strategi leverag data effect busi decis"

    The words occuring in both of the strings are: "data", "busi", and "decis". The distance of these titles is therefore: $`1 - 3/7\approx0.58`$.

### Total distance of feature vectors

After calculating the distance between the corresponding features from articles $D_1$ and $D_2$, we get a normalized vector in the form:

```math
\begin{align*}
    V_{\mu}^{D_1, D_2} = [ \\
        &\mu\left(\mathrm{np}^{D_1}, \mathrm{np}^{D_2}\right),
        & &\mu\left(\mathrm{fw}^{D_1}, \mathrm{fw}^{D_2}\right),
        & &\mu\left(\mathrm{o}^{D_1}, \mathrm{o}^{D_2}\right), \\
        &\mu\left(\mathrm{u}^{D_1}, \mathrm{u}^{D_2}\right),
        & &\mu\left(\mathrm{c}^{D_1}, \mathrm{c}^{D_2}\right),
        & &\mu\left(\mathrm{h}^{D_1}, \mathrm{h}^{D_2}\right), \\
        &\mu\left(\mathrm{s}^{D_1}, \mathrm{s}^{D_2}\right),
        & &\mu\left(\mathrm{m}^{D_1}, \mathrm{m}^{D_2}\right),
        & &\mu\left(\mathrm{w}^{D_1}, \mathrm{w}^{D_2}\right), \\
        &\mu\left(\mathrm{r}^{D_1}, \mathrm{r}^{D_2}\right),
        & &\mu\left(\mathrm{n}^{D_1}, \mathrm{n}^{D_2}\right),
        & &\mu\left(\mathrm{t}^{D_1}, \mathrm{t}^{D_2}\right) \\
    ]
\end{align*}
```

We then calculate the distance between vectors $`V^{D_1}`$ and $`V^{D_2}`$ using the following metrics:

* Euclidean distance:

    $`\mu\left(V^{D_1}, V^{D_2}\right) = \sqrt{\sum_{i=1}^{12} \left(V_{\mu}^{D_1, D_2}(i)\right)^2}`$

* Taxicab distance:

    $`\mu\left(V^{D_1}, V^{D_2}\right) = \sum_{i=1}^{12} |V_{\mu}^{D_1, D_2}(i)|`$

* Chebyshev distance:

    $`\mu\left(V^{D_1}, V^{D_2}\right) = \max_i\left(|V_{\mu}^{D_1, D_2}(i)|\right)`$

where $`V_{\mu}^{D_1, D_2}(i)`$ denotes the value of the $i$-th component of the $`V_{\mu}^{D_1, D_2}`$ vector, while $`\max_i\left(|V_{\mu}^{D_1, D_2}(i)|\right)`$ denotes the largest value of the distance modulus between any pair of feature values of the $`V^{D_1}`$ and $`V^{D_2}`$ vectors.

For example, if after calculating the distance of the features of the articles $`D_1`$ and $`D_2`$, we got the vector $`V_{\mu}^{D_1, D_2} = [0.82, 0.64, 1, 0, 1, 0.67, 0.22, 0.36, 0, 1, 0.42]`$, then the Euclidean distance is approximately:
```math
\sqrt{0.64+0.40+1+0+1+0.44+0.04+0.12+0+1+0.17} \approx 2.20
```

## Results

Let $`\mathit{rt}`$ be the ratio of the size of the training set to the total number of articles in the dataset.

### Effect of $k$ value on classification results

Results for $`\mathit{rt}=50\%`$ and Euclidean metric:

| ![](https://github.com/user-attachments/assets/e2e06ca5-b336-4ad5-8194-6bfcdcbc2e9c) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles depending on the value of k.* |

The highest accuracy was obtained for $k=5$.

<div style="text-align: center;">
    <table>
      <caption>Results for k = 5</caption>
      <tr>
        <th></th>
        <th>sensitivity</th>
        <th>precision</th>
        <th>F<sub>1</sub></th>
        <th>accuracy</th>
      </tr>
      <tr>
        <td>WEST-GERMANY</td>
        <td>0.602</td>
        <td>0.915</td>
        <td>0.727</td>
        <th rowspan="6">0.884</th>
      </tr>
      <tr>
        <td>USA</td>
        <td>0.984</td>
        <td>0.900</td>
        <td>0.940</td>
      </tr>
      <tr>
        <td>FRANCE</td>
        <td>0.595</td>
        <td>0.714</td>
        <td>0.649</td>
      </tr>
      <tr>
        <td>UK</td>
        <td>0.582</td>
        <td>0.868</td>
        <td>0.697</td>
      </tr>
      <tr>
        <td>CANADA</td>
        <td>0.239</td>
        <td>0.622</td>
        <td>0.345</td>
      </tr>
      <tr>
        <td>JAPAN</td>
        <td>0.670</td>
        <td>0.746</td>
        <td>0.706</td>
      </tr>
      <tr>
        <td><b>Weighted mean</b></td>
        <td><b>0.926</b></td>
        <td><b>0.872</b></td>
        <td><b>0.869</b></td>
      </tr>
    </table>
</div>

Values of all the measures are smaller for $`k=2`$ relative to results for $`k=1`$, while they are larger for $`k=3`$ relative to the results for smaller $`k`$. For $`k>3`$ the accuracy initially undergoes only minor fluctuations, then begins to gradually decrease for $`k>8`$. The weighted mean of $`F_1`$ decreases for $`k>3`$. The weighted mean of precision undergoes the smallest fluctuations. For $`k \le 20`$ the value of this measure remains at a similar level, with barely noticable decreases relative to precending results for $`k=4`$ and $`k=6`$. We observe a different pattern of changes in sensitivity relative to the other measures.

The impact of the number of neighbours $`k`$ on the measure values while ignoring the *usa* label was also analyzed. The results of this experiment are shown below.

Results for $`\mathit{rt}=50\%`$ and Euclidean metric while ignoring the *usa* label:

| ![](https://github.com/user-attachments/assets/0ae629dc-7d16-4c93-be18-00b7630a93cf) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles depending on the value of k while ignoring the usa label.* |

The characteristics of changes in accuracy in this case are similar to those of other quality measures. In addition, we observe noticeably lower values of the measures for all $k$ relative to the results of the previous experiment. We obtained the highest accuracy value for $`k=5`$.

### Effect of training set size on classification results

Results for $`k=10`$ and Euclidean metric:

| ![](https://github.com/user-attachments/assets/818b714b-9302-4eca-b190-6a5d3d653b8f) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles depending on the training set size.* |

<div style="text-align: center;">
    <table>
      <caption>Results for rt=50%</caption>
      <tr>
        <th></th>
        <th>sensitivity</th>
        <th>precision</th>
        <th>F<sub>1</sub></th>
        <th>accuracy</th>
      </tr>
      <tr>
        <td>WEST-GERMANY</td>
        <td>0.607</td>
        <td>0.907</td>
        <td>0.729</td>
        <th rowspan="6">0.880</th>
      </tr>
      <tr>
        <td>USA</td>
        <td>0.987</td>
        <td>0.889</td>
        <td>0.935</td>
      </tr>
      <tr>
        <td>FRANCE</td>
        <td>0.556</td>
        <td>0.795</td>
        <td>0.654</td>
      </tr>
      <tr>
        <td>UK</td>
        <td>0.561</td>
        <td>0.882</td>
        <td>0.686</td>
      </tr>
      <tr>
        <td>CANADA</td>
        <td>0.190</td>
        <td>0.675</td>
        <td>0.296</td>
      </tr>
      <tr>
        <td>JAPAN</td>
        <td>0.614</td>
        <td>0.749</td>
        <td>0.675</td>
      </tr>
      <tr>
        <td><b>Weighted mean</b></td>
        <td><b>0.933</b></td>
        <td><b>0.869</b></td>
        <td><b>0.801</b></td>
      </tr>
    </table>
</div>

The values of all measures increase as the size of the training set size increases except for $`\mathit{rt}=80\%`$, where there is a barely visible decrease in accuracy and $`F_1`$ value relative to the values for $`\mathit{rt}=70\%`$. As the size of the training set increases, the difference between the accuracy and the weighted mean of precision decreases, with the weighted mean of precision beginning to exceed the accuracy value for $`\mathit{rt}=80\%`$.

### Effect of metric type on the classification results

Results for $`k=10`$ and $`\mathit{rt}=50\%`$:

| ![](https://github.com/user-attachments/assets/383ab275-8a0f-48ea-825c-d025e4faae1a) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles depending on the used metric.* |

<div style="text-align: center;">
    <table>
      <caption>Results for the Euclidean distance</caption>
      <tr>
        <th></th>
        <th>sensitivity</th>
        <th>precision</th>
        <th>F<sub>1</sub></th>
        <th>accuracy</th>
      </tr>
      <tr>
        <td>WEST-GERMANY</td>
        <td>0.609</td>
        <td>0.907</td>
        <td>0.729</td>
        <th rowspan="6">0.880</th>
      </tr>
      <tr>
        <td>USA</td>
        <td>0.987</td>
        <td>0.889</td>
        <td>0.935</td>
      </tr>
      <tr>
        <td>FRANCE</td>
        <td>0.556</td>
        <td>0.795</td>
        <td>0.654</td>
      </tr>
      <tr>
        <td>UK</td>
        <td>0.561</td>
        <td>0.882</td>
        <td>0.686</td>
      </tr>
      <tr>
        <td>CANADA</td>
        <td>0.190</td>
        <td>0.675</td>
        <td>0.296</td>
      </tr>
      <tr>
        <td>JAPAN</td>
        <td>0.614</td>
        <td>0.749</td>
        <td>0.675</td>
      </tr>
      <tr>
        <td><b>Weighted mean</b></td>
        <td><b>0.933</b></td>
        <td><b>0.869</b></td>
        <td><b>0.861</b></td>
      </tr>
    </table>
</div>

<div style="text-align: center;">
    <table>
      <caption>Results for the taxicab distance</caption>
      <tr>
        <th></th>
        <th>sensitivity</th>
        <th>precision</th>
        <th>F<sub>1</sub></th>
        <th>accuracy</th>
      </tr>
      <tr>
        <td>WEST-GERMANY</td>
        <td>0.528</td>
        <td>0.895</td>
        <td>0.664</td>
        <th rowspan="6">0.877</th>
      </tr>
      <tr>
        <td>USA</td>
        <td>0.989</td>
        <td>0.883</td>
        <td>0.933</td>
      </tr>
      <tr>
        <td>FRANCE</td>
        <td>0.540</td>
        <td>0.819</td>
        <td>0.651</td>
      </tr>
      <tr>
        <td>UK</td>
        <td>0.536</td>
        <td>0.880</td>
        <td>0.667</td>
      </tr>
      <tr>
        <td>CANADA</td>
        <td>0.190</td>
        <td>0.733</td>
        <td>0.301</td>
      </tr>
      <tr>
        <td>JAPAN</td>
        <td>0.575</td>
        <td>0.757</td>
        <td>0.654</td>
      </tr>
      <tr>
        <td><b>Weighted mean</b></td>
        <td><b>0.935</b></td>
        <td><b>0.868</b></td>
        <td><b>0.855</b></td>
      </tr>
    </table>
</div>

<div style="text-align: center;">
    <table>
      <caption>Results for the Chebyshev distance</caption>
      <tr>
        <th></th>
        <th>sensitivity</th>
        <th>precision</th>
        <th>F<sub>1</sub></th>
        <th>accuracy</th>
      </tr>
      <tr>
        <td>WEST-GERMANY</td>
        <td>1.0</td>
        <td>0.904</td>
        <td>0.950</td>
        <th rowspan="6">0.931</th>
      </tr>
      <tr>
        <td>USA</td>
        <td>0.997</td>
        <td>0.929</td>
        <td>0.962</td>
      </tr>
      <tr>
        <td>FRANCE</td>
        <td>0.500</td>
        <td>0.969</td>
        <td>0.660</td>
      </tr>
      <tr>
        <td>UK</td>
        <td>0.958</td>
        <td>0.960</td>
        <td>0.959</td>
      </tr>
      <tr>
        <td>CANADA</td>
        <td>0.192</td>
        <td>1.0</td>
        <td>0.322</td>
      </tr>
      <tr>
        <td>JAPAN</td>
        <td>0.854</td>
        <td>0.921</td>
        <td>0.886</td>
      </tr>
      <tr>
        <td><b>Weighted mean</b></td>
        <td><b>0.976</b></td>
        <td><b>0.935</b></td>
        <td><b>0.914</b></td>
      </tr>
    </table>
</div>

The sensitivity values in the last table for the *west-germany*, *uk*, and *japan* labels are significantly higher than in the previous two tables. A comparision of classification quality while excluding these three labels is shown in the chart below.

| ![](https://github.com/user-attachments/assets/825934ad-cda1-4280-9f69-2c452a1b152f) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles depending on the used metric while excluding the west-germany, uk, and japan labels.* |

### Effect of ignoring a single feature in classification

Results for $`k=10`$, $`\mathit{rt}=50\%`$, and Euclidean distance:

| ![](https://github.com/user-attachments/assets/ca49b684-3738-45a6-89df-d9ff06783d1d) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles depending on the excluded feature.* |

Results for $`k=10`$, $`\mathit{rt}=50\%`$, and taxicab distance:

| ![](https://github.com/user-attachments/assets/13069cfb-4e4e-49de-9188-947c0a5e3f6c) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles depending on the excluded feature.* |

Results for $`k=10`$, $`\mathit{rt}=50\%`$, and Chebyshev distance:

| ![](https://github.com/user-attachments/assets/3cf03a68-9531-4312-b912-05158b935772) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles depending on the excluded feature.* |

The $`r^D`$ feature has negative effect on the quality of classification for all metrics, with the most pronounced effect on the Chebyshev metric. The $`n^D`$ and $`t^D`$ features have the most favorable impact on classification quality.

Below we show the effect of ignoring a single feature for selected labels on the value of the classification quality measure of the set representing that label.

Results for $`k=10`$, $`\mathit{rt}=50\%`$, Chebyshev distance, and *west-germany* label:

| ![](https://github.com/user-attachments/assets/3b40b609-e494-4999-982a-2f22cc5cab16) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles depending on the excluded feature for west-germany label.* |

Results for $`k=10`$, $`\mathit{rt}=50\%`$, Chebyshev distance, and *uk* label:

| ![](https://github.com/user-attachments/assets/bb3f7c3c-f5cb-4c14-a9bb-c62d89e579c4) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles depending on the excluded feature for uk label.* |

Results for $`k=10`$, $`\mathit{rt}=50\%`$, Chebyshev distance, and *japan* label:

| ![](https://github.com/user-attachments/assets/9034c629-444b-43df-92a2-2b77c16bdd0f) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles depending on the excluded feature for japan label.* |

### Effect of $k$ value and Chebyshev metric on classification results

Results for $`\mathit{rt}=90\%`$, Chebyshev metric, and disabled $`r^D`$ feature:

| ![](https://github.com/user-attachments/assets/3c516eaa-0a3a-4113-862c-56cb43b34912) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles depending on the $k$ value with disabled $`r^D`$ feature.* |

### Impact of feature subsect selection on classification results

Results for $`\mathit{rt}=50\%`$:

| ![](https://github.com/user-attachments/assets/d31e49f6-e4c8-4232-aec4-d4bfef63722d) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles for features *w*, *n*, and *t* depending on the metric.* |

| ![](https://github.com/user-attachments/assets/d7476b5d-8fc7-4f90-8a4e-f29e55db5569) | 
|:--:| 
| *Accuracy, weighted average of sensitivity, weighted average of precision and weighted average of F<sub>1</sub> measure of classified articles for all features except *w*, *n*, and *t* depending on the metric.* |

We can see that using the Chebyshev metric when determining distances based on features *w*, *n* and *t* gives the best classification quality, while this metric is the worst when we ignore those features.

## Conclusions

In the first experiment, for odd $`k`$ values less than 6, the sensitivity is always less than the sensitivity for $`k-1`$. Since the size of the *usa* class is the largest ($`79\%`$ of all articles), it also has the largest impact on the value of weighted measures. In our implementation, if among the nearest neighbours there is more than one set of articles of a certain class that is the largest, and one of them is a set of articles with *usa* label, then the classified article will always be assigned the *usa* label. For example, if exactly half of the neighbours belong to the *usa* class, while the other half belong to the *uk* class, then the classified article will be assigned to the *usa* class. In such situation, the chance of incorrectly assigning a classified article to the *usa* class increases, thereby reducing the sensitivity value for that class (*uk* in this example). Its most likely to occur for small and even values of $k$. When the sizes of the sets representing the labels are similar, this phenomenon does not occur.

The Chebyshev metric works best when the target labels are *west-germany*, *uk*, and *japan*.

The feature taken into account by the Chebyshev distance is almost always one of *w*, *n*, or *t*. For this metric, these three features have greater impact on the quality of classification than all the others combined.

The best classification quality among the the parameters we tested was obtained for the Chebyshev metric, $`\mathit{rt}=90\%`$, $`k=10`$ and disabled $`r^D`$ feature. For those parameters the results are as follows:

* Accuracy: 0.948
* Weighted mean of sensitivity: 0.971
* Weighted mean of precision: 0.950
* Weighted mean of $F_1$: 0.941

[^1]: Lewis, David. (1997). Reuters-21578 Text Categorization Collection. UCI Machine Learning Repository. <https://doi.org/10.24432/C52G6M>.
[^2]: Porter, M.F. "Snowball: A Language for Stemming Algorithms." <https://snowballstem.org/>

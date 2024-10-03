import csv
import matplotlib.pyplot as plt


K_IMPACT_CSV = "data/k_impact.csv"
K_IMPACT_IGNORING_USA_CSV = "data/k_impact_ignoring_usa.csv"
TRAINING_SET_SIZE_IMPACT_CSV = "data/training_set_size_impact.csv"
METRIC_IMPACT_CSV = "data/metric_impact.csv"
METRIC_IMPACT_IGNORING_WEST_GERMANY_JAPAN_AND_UK_CSV = "data/metric_impact_ignoring_west_germany_japan_and_uk.csv"
EUCLIDEAN_FEATURE_EXCLUSION_IMPACT_CSV = "data/feature_exclusion_impact_euclidean.csv"
TAXICAB_FEATURE_EXCLUSION_IMPACT_CSV = "data/feature_exclusion_impact_taxicab.csv"
CHEBYSHEV_FEATURE_EXCLUSION_IMPACT_CSV = "data/feature_exclusion_impact_chebyshev.csv"
K_IMPACT_WITH_CHEBYSHEV_AND_DISABLED_FIRST_NUMBER_CSV = "data/k_impact_with_chebyshev_and_disabled_first_number.csv"
METRIC_IMPACT_IGNORING_CAPITALIZED_WORD_ACRONYM_AND_TITLE_CSV = "data/metric_impact_ignoring_capitalized_word_acronym_and_title.csv"
METRIC_IMPACT_IGNORING_ALL_EXCEPT_CAPITALIZED_WORD_ACRONYM_AND_TITLE_CSV = "data/metric_impact_ignoring_all_except_capitalized_word_acronym_and_title.csv"

K_IMPACT_PLOT = "data/k_impact"
K_IMPACT_IGNORING_USA_PLOT = "data/k_impact_ignoring_usa"
TRAINING_SET_SIZE_IMPACT_PLOT = "data/training_set_size_impact"
METRIC_IMPACT_PLOT = "data/metric_impact"
METRIC_IMPACT_IGNORING_WEST_GERMANY_JAPAN_AND_UK_PLOT = "data/metric_impact_ignoring_west_germany_japan_and_uk"
EUCLIDEAN_FEATURE_EXCLUSION_IMPACT_PLOT = "data/feature_exclusion_impact_euclidean"
TAXICAB_FEATURE_EXCLUSION_IMPACT_PLOT = "data/feature_exclusion_impact_taxicab"
CHEBYSHEV_FEATURE_EXCLUSION_IMPACT_PLOT = "data/feature_exclusion_impact_chebyshev"
K_IMPACT_WITH_CHEBYSHEV_AND_DISABLED_FIRST_NUMBER_PLOT = "data/k_impact_with_chebyshev_and_disabled_first_number"
METRIC_IMPACT_IGNORING_CAPITALIZED_WORD_ACRONYM_AND_TITLE_PLOT = "data/metric_impact_ignoring_capitalized_word_acronym_and_title"
METRIC_IMPACT_IGNORING_ALL_EXCEPT_CAPITALIZED_WORD_ACRONYM_AND_TITLE_PLOT = "data/metric_impact_ignoring_all_except_capitalized_word_acronym_and_title"

OPTION_TO_LABEL = {
    "--disable-west-german-political-count": "$np^D$",
    "--disable-canadian-city-freq": "$fw^D$",
    "--disable-french-bank-presence": "$o^D$",
    "--disable-uk-acronym-presence": "$u^D$",
    "--disable-japanese-company-presence": "$c^D$",
    "--disable-usa-state-presence": "$h^D$",
    "--disable-capitals-presence": "$s^D$",
    "--disable-currencies-presence": "$m^D$",
    "--disable-first-capitalized-word": "$w^D$",
    "--disable-first-number": "$r^D$",
    "--disable-most-frequent-acronym": "$n^D$",
    "--disable-title": "$t^D$",
}


def plot_results(csv_path, plot_path, iter_extractor, xlabel, ylim, lines=[]):
    with open(csv_path, newline='') as csvfile:
        i = []
        acc = []
        sens = []
        prec = []
        f1 = []

        reader = csv.DictReader(csvfile)
        for row in reader:
            i.append(iter_extractor(row))
            acc.append(float(row['Accuracy']))
            sens.append(float(row['Weighted mean of sensitivity']))
            prec.append(float(row['Weighted mean of precision']))
            f1.append(float(row['Weighted mean of F1']))

        fig, ax = plt.subplots()
        x = [v for v in range(len(i))]

        l0, = ax.plot(x, acc, label='Weighted mean of accuracy', marker='.')
        l1, = ax.plot(x, sens, label='Weighted mean of sensitivity', marker='.')
        l2, = ax.plot(x, prec, label='Weighted mean of precision', marker='.')
        l3, = ax.plot(x, f1, label='Weighted mean of $F_1$', marker='.')

        if lines != []:
            assert len(lines) == 4, \
                "Expected the line count to be the same as plot count"

            ax.axhline(y=lines[0], color=l0.get_color(), linestyle='--')
            ax.axhline(y=lines[1], color=l1.get_color(), linestyle='--')
            ax.axhline(y=lines[2], color=l2.get_color(), linestyle='--')
            ax.axhline(y=lines[3], color=l3.get_color(), linestyle='--')

        ax.set_xlabel(xlabel)
        ax.set_ylabel('Measure value')

        ax.set_ylim(ylim[0], ylim[1])

        ax.set_xticks(x)
        ax.set_xticklabels(i)

        ax.legend()

        fig.savefig(plot_path)


def plot_k_impact_results():
    plot_results(
        K_IMPACT_CSV,
        K_IMPACT_PLOT,
        lambda row: int(row['k']),
        'Neighbour count $k$',
        (0.75, 1.0),
    )


def plot_k_impact_ignoring_usa_results():
    plot_results(
        K_IMPACT_IGNORING_USA_CSV,
        K_IMPACT_IGNORING_USA_PLOT,
        lambda row: int(row['k']),
        'Neighbour count $k$',
        (0.70, 1.0),
    )


def plot_k_impact_with_chebyshev_and_disabled_first_number_results():
    plot_results(
        K_IMPACT_WITH_CHEBYSHEV_AND_DISABLED_FIRST_NUMBER_CSV,
        K_IMPACT_WITH_CHEBYSHEV_AND_DISABLED_FIRST_NUMBER_PLOT,
        lambda row: int(row['k']),
        'Neighbour count $k$',
        (0.75, 1.0),
    )


def plot_training_set_size_impact_results():
    plot_results(
        TRAINING_SET_SIZE_IMPACT_CSV,
        TRAINING_SET_SIZE_IMPACT_PLOT,
        lambda row: int(round(float(row['training set size']) * 10)),
        'Training set size [$\\%$]',
        (0.75, 1.0),
    )


def plot_metric_impact_results():
    plot_results(
        METRIC_IMPACT_CSV,
        METRIC_IMPACT_PLOT,
        lambda row: row['metric'],
        'Metric',
        (0.75, 1.0),
    )


def plot_metric_impact_ignoring_west_germany_japan_and_uk_results():
    plot_results(
        METRIC_IMPACT_IGNORING_WEST_GERMANY_JAPAN_AND_UK_CSV,
        METRIC_IMPACT_IGNORING_WEST_GERMANY_JAPAN_AND_UK_PLOT,
        lambda row: row['metric'],
        'Metric',
        (0.75, 1.0),
    )


def plot_metric_impact_ignoring_capitalized_word_acronym_and_title():
    plot_results(
        METRIC_IMPACT_IGNORING_CAPITALIZED_WORD_ACRONYM_AND_TITLE_CSV,
        METRIC_IMPACT_IGNORING_CAPITALIZED_WORD_ACRONYM_AND_TITLE_PLOT,
        lambda row: row['metric'],
        'Metric',
        (0.5, 1.0),
    )


def plot_metric_impact_ignoring_all_except_capitalized_word_acronym_and_title():
    plot_results(
        METRIC_IMPACT_IGNORING_ALL_EXCEPT_CAPITALIZED_WORD_ACRONYM_AND_TITLE_CSV,
        METRIC_IMPACT_IGNORING_ALL_EXCEPT_CAPITALIZED_WORD_ACRONYM_AND_TITLE_PLOT,
        lambda row: row['metric'],
        'Metric',
        (0.5, 1.0),
    )


def plot_feature_exclusion_impact_results():
    plot_results(
        EUCLIDEAN_FEATURE_EXCLUSION_IMPACT_CSV,
        EUCLIDEAN_FEATURE_EXCLUSION_IMPACT_PLOT,
        lambda row: OPTION_TO_LABEL[row['feature']],
        'Disabled feature',
        (0.75, 1.0),
        [0.8799465, 0.93279153, 0.86925733, 0.8605769],
    )
    plot_results(
        TAXICAB_FEATURE_EXCLUSION_IMPACT_CSV,
        TAXICAB_FEATURE_EXCLUSION_IMPACT_PLOT,
        lambda row: OPTION_TO_LABEL[row['feature']],
        'Disabled feature',
        (0.75, 1.0),
        [0.87652487, 0.9350982, 0.86842424, 0.85548985],
    )
    plot_results(
        CHEBYSHEV_FEATURE_EXCLUSION_IMPACT_CSV,
        CHEBYSHEV_FEATURE_EXCLUSION_IMPACT_PLOT,
        lambda row: OPTION_TO_LABEL[row['feature']],
        'Disabled feature',
        (0.75, 1.0),
        [0.9314192, 0.97556776, 0.93529546, 0.9143685],
    )


def plot_feature_exclusion_impact_for_single_country_results(country):
    with open(CHEBYSHEV_FEATURE_EXCLUSION_IMPACT_CSV, newline='') as csvfile:
        i = []
        acc = []
        sens = []
        prec = []
        f1 = []

        reader = csv.DictReader(csvfile)
        for row in reader:
            i.append(OPTION_TO_LABEL[row['feature']])
            acc.append(float(row['Accuracy']))
            sens.append(float(row[country.upper() + ' sensitivity']))
            prec.append(float(row[country.upper() + ' precision']))
            f1.append(float(row[country.upper() + ' F1']))

        fig, ax = plt.subplots()
        x = [v for v in range(len(i))]

        ax.plot(x, acc, label='Accuracy', marker='.')
        ax.plot(x, sens, label='Sensitivity', marker='.')
        ax.plot(x, prec, label='Precision', marker='.')
        ax.plot(x, f1, label='$F_1$', marker='.')

        ax.set_xlabel('Disabled feature')
        ax.set_ylabel('Measure value')

        ax.set_ylim(0.5, 1.0)

        ax.set_xticks(x)
        ax.set_xticklabels(i)

        ax.legend()

        fig.savefig(CHEBYSHEV_FEATURE_EXCLUSION_IMPACT_PLOT + '_' + country)


if __name__ == '__main__':
    plot_k_impact_results()
    plot_k_impact_ignoring_usa_results()
    plot_k_impact_with_chebyshev_and_disabled_first_number_results()
    plot_training_set_size_impact_results()
    plot_metric_impact_results()
    plot_metric_impact_ignoring_west_germany_japan_and_uk_results()
    plot_feature_exclusion_impact_results()
    plot_feature_exclusion_impact_for_single_country_results("west_germany")
    plot_feature_exclusion_impact_for_single_country_results("uk")
    plot_feature_exclusion_impact_for_single_country_results("japan")
    plot_metric_impact_ignoring_capitalized_word_acronym_and_title()
    plot_metric_impact_ignoring_all_except_capitalized_word_acronym_and_title()

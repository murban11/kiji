import csv
import matplotlib.pyplot as plt


K_IMPACT_CSV = "data/k_impact.csv"
TRAINING_SET_SIZE_IMPACT_CSV = "data/training_set_size_impact.csv"
METRIC_IMPACT_CSV = "data/metric_impact.csv"
EUCLIDEAN_FEATURE_EXCLUSION_IMPACT_CSV = "data/feature_exclusion_impact_euclidean.csv"
TAXICAB_FEATURE_EXCLUSION_IMPACT_CSV = "data/feature_exclusion_impact_taxicab.csv"
CHEBYSHEV_FEATURE_EXCLUSION_IMPACT_CSV = "data/feature_exclusion_impact_chebyshev.csv"
K_IMPACT_WITH_CHEBYSHEV_AND_DISABLED_FIRST_NUMBER_CSV = "data/k_impact_with_chebyshev_and_disabled_first_number.csv"

K_IMPACT_PLOT = "data/k_impact"
TRAINING_SET_SIZE_IMPACT_PLOT = "data/training_set_size_impact"
METRIC_IMPACT_PLOT = "data/metric_impact"
EUCLIDEAN_FEATURE_EXCLUSION_IMPACT_PLOT = "data/feature_exclusion_impact_euclidean"
TAXICAB_FEATURE_EXCLUSION_IMPACT_PLOT = "data/feature_exclusion_impact_taxicab"
CHEBYSHEV_FEATURE_EXCLUSION_IMPACT_PLOT = "data/feature_exclusion_impact_chebyshev"
K_IMPACT_WITH_CHEBYSHEV_AND_DISABLED_FIRST_NUMBER_PLOT = "data/k_impact_with_chebyshev_and_disabled_first_number"


def plot_results(csv_path, plot_path, iter_extractor, xlabel, lines=[]):
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

        ax.set_ylim(0.75, 1.0)

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
    )


def plot_training_set_size_impact_results():
    plot_results(
        TRAINING_SET_SIZE_IMPACT_CSV,
        TRAINING_SET_SIZE_IMPACT_PLOT,
        lambda row: int(round(float(row['training set size']) * 10)),
        'Training set size [$\\%$]',
    )


def plot_metric_impact_results():
    plot_results(
        METRIC_IMPACT_CSV,
        METRIC_IMPACT_PLOT,
        lambda row: row['metric'],
        'Metric',
    )


def plot_feature_exclusion_impact_results():
    option_to_label = {
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

    plot_results(
        EUCLIDEAN_FEATURE_EXCLUSION_IMPACT_CSV,
        EUCLIDEAN_FEATURE_EXCLUSION_IMPACT_PLOT,
        lambda row: option_to_label[row['feature']],
        'Disabled feature',
        [0.8799465, 0.93279153, 0.86925733, 0.8605769],
    )
    plot_results(
        TAXICAB_FEATURE_EXCLUSION_IMPACT_CSV,
        TAXICAB_FEATURE_EXCLUSION_IMPACT_PLOT,
        lambda row: option_to_label[row['feature']],
        'Disabled feature',
        [0.87652487, 0.9350982, 0.86842424, 0.85548985],
    )
    plot_results(
        CHEBYSHEV_FEATURE_EXCLUSION_IMPACT_CSV,
        CHEBYSHEV_FEATURE_EXCLUSION_IMPACT_PLOT,
        lambda row: option_to_label[row['feature']],
        'Disabled feature',
        [0.9314192, 0.97556776, 0.93529546, 0.9143685],
    )


def plot_k_impact_with_chebyshev_and_disabled_first_number_results():
    plot_results(
        K_IMPACT_WITH_CHEBYSHEV_AND_DISABLED_FIRST_NUMBER_CSV,
        K_IMPACT_WITH_CHEBYSHEV_AND_DISABLED_FIRST_NUMBER_PLOT,
        lambda row: int(row['k']),
        'Neighbour count $k$',
    )


if __name__ == '__main__':
    plot_k_impact_results()
    plot_training_set_size_impact_results()
    plot_metric_impact_results()
    plot_feature_exclusion_impact_results()
    plot_k_impact_with_chebyshev_and_disabled_first_number_results()

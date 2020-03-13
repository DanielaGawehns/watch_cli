package nl.liacs.watch_cli.commands;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.stream.Collectors;

import nl.liacs.watch_cli.Main;
import nl.liacs.watch_cli.Smartwatch;

public class Export implements Command {
    private interface Formatter {
        void format(Collection<Smartwatch> watches, OutputStream out);
    }

    private class TSV implements Formatter {
        TSV() {
        }

        public void format(Collection<Smartwatch> watches, OutputStream out) {
            var ps = new PrintStream(out);

            // REVIEW: do we expect the data to be sorted?

            ps.println("Watch ID\tSensor\tDate\tData...");
            for (var watch : watches) {
                var datapoints = watch.getDatapoints();
                for (var point : datapoints) {
                    ps.print(watch.getID());
                    ps.print('\t');
                    ps.print(point.getSensor());
                    ps.print('\t');
                    ps.print(point.getDate().toString());

                    var data = point.getData();
                    for (int i = 0; i < data.length; i++) {
                        ps.print('\t');
                        ps.print(data[i]);
                    }

                    ps.println();
                }
            }

            ps.flush();
        }
    }


    public String getDescription() {
        return "Export the datapoints of the watches with given IDs (or all watches if no IDs are given).\n  --format accepts 'tsv'.\n  --out is optional, if given the output will be written to the given path instead of stdout.";
    }

    public boolean checkArguments(Arguments args) {
        return true;
    }

    public void run(Arguments args) {
        var format = args.getString("format");
        if (format == null) {
            format = "";
        }

        Formatter formatter;
        if (format.equals("tsv")) {
            formatter = new TSV();
        } else if (format.isBlank()) {
            System.err.println("--format is required");
            return;
        } else {
            System.err.printf("unknown format '%s'\n", format);
            return;
        }

        var outPath = args.getString("out");
        if (outPath == null) {
            outPath = args.getString("output");
        }

        var watches = Utils.getWatchesFromIndicesOrAll(args.getRest());

        OutputStream out;
        boolean mustClose;
        if (outPath != null) {
            // use output file
            mustClose = true;
            try {
                out = new FileOutputStream(outPath);
            } catch (FileNotFoundException e) {
                System.err.printf("file not found: %s\n", outPath);
                return;
            }
        } else {
            // use stdout
            mustClose = false;
            out = System.out;
        }

        formatter.format(watches, out);

        if (mustClose) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
